package com.development.spring.hGate.H_Gate.prenotazioni;

import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PrenotazioniTest extends IntegrationTestBase {

    /** Un lunedì abbastanza lontano da non avere slot "nel passato". */
    private LocalDate lunedi;

    @BeforeEach
    void disponibilitaLunedi() {
        lunedi = LocalDate.now().plusDays(14).with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        // Convenzione di schema e frontend: 0=Domenica, 1=Lunedì
        disponibilitaRepository.save(DisponibilitaMedico.builder()
                .medico(medicoA).giornoSettimana(1)
                .oraInizio(LocalTime.of(9, 0)).oraFine(LocalTime.of(12, 0))
                .build());
    }

    private String slotUrl() {
        return "/prenotazione/disponibilita/" + medicoA.getId();
    }

    @Test
    void lunediConfiguratoMostraGliSlotDelLunedi() throws Exception {
        // Prima della correzione il lunedì veniva cercato come giorno 0 (domenica) e risultava vuoto
        mvc.perform(get(slotUrl()).param("data", lunedi.toString()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slots", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.data.slots[0].dataOra").value(lunedi + "T09:00:00"));
    }

    @Test
    void ilMartediNonHaSlot() throws Exception {
        mvc.perform(get(slotUrl()).param("data", lunedi.plusDays(1).toString()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slots", hasSize(0)));
    }

    @Test
    void medicoSenzaDurataEPausaImpostateNonRompeGliSlot() {
        // Il medico di test è creato con il builder senza durata/pausa: devono valere i default
        assertThat(medicoA.getDurataVisitaMinuti()).isEqualTo(30);
        assertThat(medicoA.getPausaTraVisiteMinuti()).isEqualTo(5);
        assertThat(medicoA.getIsDisponibile()).isTrue();
    }

    @Test
    void prenotazioneAnnullataLiberaLoSlot() throws Exception {
        prenotazione(medicoA, pazienteA, tutoreAUser, lunedi.atTime(9, 0), StatoPrenotazioneEnum.ANNULLATA);
        prenotazione(medicoA, pazienteA, tutoreAUser, lunedi.atTime(9, 35), StatoPrenotazioneEnum.IN_ATTESA);

        mvc.perform(get(slotUrl()).param("data", lunedi.toString()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slots[0].disponibile").value(true))   // 09:00, annullata
                .andExpect(jsonPath("$.data.slots[1].disponibile").value(false)); // 09:35, in attesa
    }

    @Test
    void prenotazioneEConflittoSullaStessaOra() throws Exception {
        String body = "{\"pazienteId\":" + pazienteA.getId() + ",\"medicoId\":" + medicoA.getId()
                + ",\"dataOra\":\"" + lunedi + "T10:10:00\",\"tipoVisita\":\"Visita\"}";

        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stato").value("IN_ATTESA"));

        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lePrenotazioniCreateHannoIPromemoriaDaInviare() throws Exception {
        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pazienteId\":" + pazienteA.getId() + ",\"medicoId\":" + medicoA.getId()
                                + ",\"dataOra\":\"" + lunedi + "T11:00:00\",\"tipoVisita\":\"Visita\"}"))
                .andExpect(status().isOk());

        Prenotazione creata = prenotazioneRepository.findAll().stream()
                .filter(p -> p.getDataOra().equals(lunedi.atTime(11, 0))).findFirst().orElseThrow();
        // Prima era NULL e il job dei promemoria (che cerca FALSE) non la trovava mai
        assertThat(creata.getPromemoriaInviato()).isFalse();
    }

    @Test
    void tutoreNonPrenotaPerIlMinoreDiUnAltro() throws Exception {
        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreBUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pazienteId\":" + pazienteA.getId() + ",\"medicoId\":" + medicoA.getId()
                                + ",\"dataOra\":\"" + lunedi + "T10:10:00\",\"tipoVisita\":\"Visita\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void configurazioneStandardELunediVenerdi() throws Exception {
        mvc.perform(put("/disponibilita/medico/standard").header("Authorization", bearer(medicoSenzaPazienti.getUser())))
                .andExpect(status().isOk());
        var giorni = disponibilitaRepository.findAll().stream()
                .filter(d -> d.getMedico().getId().equals(medicoSenzaPazienti.getId()))
                .map(DisponibilitaMedico::getGiornoSettimana)
                .collect(Collectors.toSet());
        assertThat(giorni).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
    }
}
