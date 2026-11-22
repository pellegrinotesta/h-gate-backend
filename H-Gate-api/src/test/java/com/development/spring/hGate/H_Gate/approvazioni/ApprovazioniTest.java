package com.development.spring.hGate.H_Gate.approvazioni;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.RichiestaAssociazione;
import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import com.development.spring.hGate.H_Gate.repositories.RichiestaAssociazioneRepository;
import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApprovazioniTest extends IntegrationTestBase {

    @Autowired
    private RichiestaAssociazioneRepository richiestaRepository;

    private ResultActions login(String email) throws Exception {
        return mvc.perform(post("/authentication").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + email + "\",\"password\":\"" + PASSWORD + "\"}"));
    }

    private ResultActions registra(String body) throws Exception {
        return mvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Nested
    class Medici {

        private Medico registraMedico() throws Exception {
            registra("{\"email\":\"nuovo.medico@test.it\",\"confirmPassword\":\"" + PASSWORD + "\","
                    + "\"nome\":\"Franco\",\"cognome\":\"Nuovi\",\"ruolo\":\"MEDICO\","
                    + "\"specializzazione\":\"Logopedista\",\"numeroAlbo\":\"NUOVO-1\"}")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(containsString("approvazione della clinica")));
            return medicoRepository.findAll().stream()
                    .filter(m -> "NUOVO-1".equals(m.getNumeroAlbo())).findFirst().orElseThrow();
        }

        @Test
        void medicoRegistratoNonAccedeEnonEPrenotabileFinoAllApprovazione() throws Exception {
            Medico nuovo = registraMedico();

            login("nuovo.medico@test.it")
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value(containsString("in attesa di approvazione")));
            mvc.perform(get("/medico/all").header("Authorization", bearer(tutoreAUser)))
                    .andExpect(jsonPath("$.data[*].numeroAlbo", not(hasItem("NUOVO-1"))));

            mvc.perform(put("/admin/approvazioni/medici/{id}/approva", nuovo.getId()).header("Authorization", bearer(admin)))
                    .andExpect(status().isOk());

            login("nuovo.medico@test.it").andExpect(status().isOk());
            mvc.perform(get("/medico/all").header("Authorization", bearer(tutoreAUser)))
                    .andExpect(jsonPath("$.data[*].numeroAlbo", hasItem("NUOVO-1")));
        }

        @Test
        void approvareDueVolteDa409() throws Exception {
            Medico nuovo = registraMedico();
            mvc.perform(put("/admin/approvazioni/medici/{id}/approva", nuovo.getId()).header("Authorization", bearer(admin)))
                    .andExpect(status().isOk());
            mvc.perform(put("/admin/approvazioni/medici/{id}/approva", nuovo.getId()).header("Authorization", bearer(admin)))
                    .andExpect(status().isConflict());
        }

        @Test
        void medicoRifiutatoVieneDisattivato() throws Exception {
            Medico nuovo = registraMedico();
            mvc.perform(put("/admin/approvazioni/medici/{id}/rifiuta", nuovo.getId()).header("Authorization", bearer(admin))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Albo non verificabile\"}"))
                    .andExpect(status().isOk());
            assertThat(userRepository.findByEmail("nuovo.medico@test.it").orElseThrow().getIsActive()).isFalse();
            login("nuovo.medico@test.it").andExpect(status().isUnauthorized());
        }

        @Test
        void soloLAdminGestisceLeApprovazioni() throws Exception {
            Medico nuovo = registraMedico();
            mvc.perform(get("/admin/approvazioni/medici").header("Authorization", bearer(tutoreAUser)))
                    .andExpect(status().isForbidden());
            mvc.perform(put("/admin/approvazioni/medici/{id}/approva", nuovo.getId()).header("Authorization", bearer(medicoA.getUser())))
                    .andExpect(status().isForbidden());
            mvc.perform(get("/admin/approvazioni/medici").header("Authorization", bearer(admin)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data[*].numeroAlbo", hasItem("NUOVO-1")));
        }
    }

    @Nested
    class Associazioni {

        @Test
        void registrazioneConMinoreEsistenteCreaUnaRichiestaInAttesa() throws Exception {
            registra("{\"email\":\"nonna@test.it\",\"confirmPassword\":\"" + PASSWORD + "\",\"nome\":\"Maria\","
                    + "\"cognome\":\"Verdi\",\"ruolo\":\"TUTORE\",\"minore\":{\"nome\":\"Marco\",\"cognome\":\"Verdi\","
                    + "\"codiceFiscale\":\"" + pazienteA.getCodiceFiscale() + "\",\"relazione\":\"tutore_legale\"}}")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(containsString("verifica della segreteria")));

            var nonna = userRepository.findByEmail("nonna@test.it").orElseThrow();
            // Prima dell'approvazione: nessun accesso al minore
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(nonna)))
                    .andExpect(status().isForbidden());

            RichiestaAssociazione r = richiestaRepository.findByStatoOrderByCreatedAtAsc(StatoRichiestaEnum.IN_ATTESA).get(0);
            assertThat(r.getRelazione()).isEqualTo("TUTORE_LEGALE");

            mvc.perform(put("/admin/approvazioni/associazioni/{id}/approva", r.getId()).header("Authorization", bearer(admin)))
                    .andExpect(status().isOk());
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(nonna)))
                    .andExpect(status().isOk());
        }

        @Test
        void tutoreGiaRegistratoCheAggiungeUnMinoreEsistente() throws Exception {
            String body = "{\"nome\":\"Marco\",\"cognome\":\"Verdi\",\"codiceFiscale\":\""
                    + pazienteA.getCodiceFiscale() + "\",\"relazione\":\"PADRE\"}";
            mvc.perform(post("/paziente/add").header("Authorization", bearer(tutoreBUser))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andExpect(jsonPath("$.message").value(containsString("richiesta di associazione")));
            // Una seconda richiesta uguale non crea duplicati
            mvc.perform(post("/paziente/add").header("Authorization", bearer(tutoreBUser))
                    .contentType(MediaType.APPLICATION_JSON).content(body));
            assertThat(richiestaRepository.countByStato(StatoRichiestaEnum.IN_ATTESA)).isEqualTo(1);

            RichiestaAssociazione r = richiestaRepository.findByStatoOrderByCreatedAtAsc(StatoRichiestaEnum.IN_ATTESA).get(0);
            mvc.perform(get("/admin/approvazioni/associazioni").header("Authorization", bearer(admin)))
                    .andExpect(jsonPath("$.data[0].tutoriAttuali[0]").value(containsString("(MADRE)")));

            mvc.perform(put("/admin/approvazioni/associazioni/{id}/rifiuta", r.getId()).header("Authorization", bearer(admin))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Non risulta genitore\"}"))
                    .andExpect(status().isOk());
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(tutoreBUser)))
                    .andExpect(status().isForbidden());
            mvc.perform(put("/admin/approvazioni/associazioni/{id}/approva", r.getId()).header("Authorization", bearer(admin)))
                    .andExpect(status().isConflict());
        }

        @Test
        void registrazioneConMinoreNuovoAssociaSubito() throws Exception {
            registra("{\"email\":\"nuovo.genitore@test.it\",\"confirmPassword\":\"" + PASSWORD + "\",\"nome\":\"Giovanni\","
                    + "\"cognome\":\"Nuovo\",\"ruolo\":\"TUTORE\",\"minore\":{\"nome\":\"Pietro\",\"cognome\":\"Nuovo\","
                    + "\"codiceFiscale\":\"NVOPTR15A01H501X\",\"relazione\":\"PADRE\",\"consensoPrivacy\":true}}")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(containsString("Ora puoi accedere")));
            assertThat(richiestaRepository.countByStato(StatoRichiestaEnum.IN_ATTESA)).isZero();
        }
    }
}
