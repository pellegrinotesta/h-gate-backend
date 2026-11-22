package com.development.spring.hGate.H_Gate.consensi;

import com.development.spring.hGate.H_Gate.entity.Consenso;
import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.repositories.ConsensoRepository;
import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConsensoTest extends IntegrationTestBase {

    @Autowired
    private ConsensoRepository consensoRepository;

    private String registrazione(boolean consenso) {
        return "{\"email\":\"genitore@test.it\",\"confirmPassword\":\"" + PASSWORD + "\",\"nome\":\"Giovanni\","
                + "\"cognome\":\"Nuovo\",\"ruolo\":\"TUTORE\",\"minore\":{\"nome\":\"Pietro\",\"cognome\":\"Nuovo\","
                + "\"codiceFiscale\":\"NVOPTR15A01H501X\",\"relazione\":\"PADRE\""
                + (consenso ? ",\"consensoPrivacy\":true" : "") + "}}";
    }

    @Test
    void registrazioneSenzaConsensoRifiutata() throws Exception {
        mvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON).content(registrazione(false)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("consenso")));
        assertThat(userRepository.findByEmail("genitore@test.it")).isEmpty();
    }

    @Test
    void registrazioneConConsensoRegistraLoStorico() throws Exception {
        mvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON).content(registrazione(true))
                        .header("X-Forwarded-For", "203.0.113.7"))
                .andExpect(status().isOk());
        Integer pid = pazienteRepository.findByCodiceFiscale("NVOPTR15A01H501X").orElseThrow().getId();
        Consenso c = consensoRepository.storico(pid).get(0);
        assertThat(c.getConcesso()).isTrue();
        assertThat(c.getVersioneInformativa()).isEqualTo("2026-11");
        assertThat(c.getUser().getEmail()).isEqualTo("genitore@test.it");
        assertThat(c.getIpAddress()).isEqualTo("203.0.113.7");
        assertThat(pazienteRepository.findById(pid).orElseThrow().getConsensoPrivacy()).isTrue();
    }

    @Test
    void aggiuntaMinoreSenzaConsensoRifiutata() throws Exception {
        mvc.perform(post("/paziente/add").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Lia\",\"cognome\":\"Verdi\",\"codiceFiscale\":\"VRDLIA18C41H501Z\",\"relazione\":\"MADRE\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("consenso")));
    }

    @Test
    void revocaBloccaLePrenotazioniELaRiconcessioneLeRiabilita() throws Exception {
        LocalDate lunedi = LocalDate.now().plusDays(14).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        disponibilitaRepository.save(DisponibilitaMedico.builder().medico(medicoA).giornoSettimana(1)
                .oraInizio(LocalTime.of(9, 0)).oraFine(LocalTime.of(12, 0)).build());
        String prenota = "{\"pazienteId\":" + pazienteA.getId() + ",\"medicoId\":" + medicoA.getId()
                + ",\"dataOra\":\"" + lunedi + "T09:00:00\",\"tipoVisita\":\"Visita\"}";

        mvc.perform(post("/consensi/paziente/{id}/revoca", pazienteA.getId()).header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Trasferimento ad altra struttura\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attivo").value(false));

        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content(prenota))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("revocato")));

        // revocare due volte non ha senso
        mvc.perform(post("/consensi/paziente/{id}/revoca", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isConflict());

        mvc.perform(post("/consensi/paziente/{id}/concedi", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.attivo").value(true))
                .andExpect(jsonPath("$.data.aggiornato").value(true))
                .andExpect(jsonPath("$.data.storico.length()").value(2))
                .andExpect(jsonPath("$.data.storico[1].note").value("Trasferimento ad altra struttura"));

        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content(prenota))
                .andExpect(status().isOk());
    }

    @Test
    void unTutoreNonGestisceIConsensiDiAltriMinori() throws Exception {
        mvc.perform(post("/consensi/paziente/{id}/revoca", pazienteA.getId()).header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
        mvc.perform(post("/consensi/paziente/{id}/concedi", pazienteA.getId()).header("Authorization", bearer(medicoA.getUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    void consensoPrecedenteAlloStoricoVaRiconfermato() throws Exception {
        // pazienteA ha consenso_privacy = true ma nessuna riga di storico (dati precedenti)
        mvc.perform(get("/consensi/miei").header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].attivo").value(true))
                .andExpect(jsonPath("$.data[0].aggiornato").value(false))
                .andExpect(jsonPath("$.data[0].versioneCorrente").value("2026-11"));
    }
}
