package com.development.spring.hGate.H_Gate.security;

import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Ogni utente deve vedere e modificare solo i dati dei pazienti a cui è legato:
 * il tutore i propri minori, il medico i pazienti che ha in carico.
 */
class AccessControlTest extends IntegrationTestBase {

    private static final String AUTH = "Authorization";

    @Nested
    class Pazienti {
        @Test
        void tutoreVedeIlProprioMinore() throws Exception {
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(tutoreAUser)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.codiceFiscale").value(pazienteA.getCodiceFiscale()));
        }

        @Test
        void tutoreNonVedeIlMinoreDiUnAltro() throws Exception {
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(tutoreBUser)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.ok").value(false));
        }

        @Test
        void medicoVedeIlPazienteInCarico() throws Exception {
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(medicoA.getUser())))
                    .andExpect(status().isOk());
        }

        @Test
        void medicoNonVedePazientiNonInCarico() throws Exception {
            mvc.perform(get("/paziente/{id}", pazienteB.getId()).header(AUTH, bearer(medicoA.getUser())))
                    .andExpect(status().isForbidden());
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(medicoSenzaPazienti.getUser())))
                    .andExpect(status().isForbidden());
        }

        @Test
        void adminVedeIPazienti() throws Exception {
            mvc.perform(get("/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(admin)))
                    .andExpect(status().isOk());
        }

        @Test
        void pazienteInesistenteNonRivelaNulla() throws Exception {
            mvc.perform(get("/paziente/{id}", 999_999).header(AUTH, bearer(tutoreAUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void tutoreNonModificaIlMinoreDiUnAltro() throws Exception {
            mvc.perform(put("/paziente/update").header(AUTH, bearer(tutoreBUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":" + pazienteA.getId() + ",\"nome\":\"Marco\",\"cognome\":\"Verdi\","
                                    + "\"codiceFiscale\":\"" + pazienteA.getCodiceFiscale() + "\",\"allergie\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class Referti {
        @Test
        void tutoreVedeIlRefertoDelFiglio() throws Exception {
            mvc.perform(get("/referto/{id}", refertoA.getId()).header(AUTH, bearer(tutoreAUser)))
                    .andExpect(status().isOk());
        }

        @Test
        void tutoreNonVedeRefertiAltrui() throws Exception {
            mvc.perform(get("/referto/{id}", refertoA.getId()).header(AUTH, bearer(tutoreBUser)))
                    .andExpect(status().isForbidden());
            mvc.perform(get("/referto/{pid}/all", pazienteA.getId()).header(AUTH, bearer(tutoreBUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void soloIlMedicoAutoreModificaIlReferto() throws Exception {
            mvc.perform(put("/referto/{id}", refertoA.getId()).header(AUTH, bearer(medicoSenzaPazienti.getUser()))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"diagnosi\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void medicoNonCreaRefertiSuPrenotazioniAltrui() throws Exception {
            mvc.perform(post("/referto").header(AUTH, bearer(medicoSenzaPazienti.getUser()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"prenotazioneId\":" + prenotazioneA.getId()
                                    + ",\"titolo\":\"x\",\"diagnosi\":\"x\",\"tipoReferto\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class Prenotazioni {
        @Test
        void tutoreVedeSoloLeProprie() throws Exception {
            mvc.perform(get("/prenotazione/{id}", prenotazioneA.getId()).header(AUTH, bearer(tutoreAUser)))
                    .andExpect(status().isOk());
            mvc.perform(get("/prenotazione/{id}", prenotazioneA.getId()).header(AUTH, bearer(tutoreBUser)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void medicoAggiornaSoloLeProprie() throws Exception {
            mvc.perform(put("/prenotazione/{id}", prenotazioneA.getId()).header(AUTH, bearer(medicoSenzaPazienti.getUser()))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"noteMedico\":\"x\"}"))
                    .andExpect(status().isForbidden());
            mvc.perform(put("/prenotazione/{id}", prenotazioneA.getId()).header(AUTH, bearer(medicoA.getUser()))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"noteMedico\":\"nota\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.noteMedico").value("nota"));
        }

        @Test
        void lIdNelBodyNonAggiraIlControllo() throws Exception {
            var altra = prenotazione(medicoSenzaPazienti, pazienteB, tutoreBUser,
                    prenotazioneA.getDataOra().plusDays(1), prenotazioneA.getStato());
            // medicoA è autorizzato su prenotazioneA (nel body) ma non su "altra" (nel path)
            mvc.perform(put("/prenotazione/{id}", altra.getId()).header(AUTH, bearer(medicoA.getUser()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":" + prenotazioneA.getId() + ",\"noteMedico\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class AltriDatiClinici {
        @Test
        void tutoreNonVedeAllegatiPercorsiValutazioniAltrui() throws Exception {
            String t = bearer(tutoreBUser);
            mvc.perform(get("/allegati/paziente/{id}", pazienteA.getId()).header(AUTH, t)).andExpect(status().isForbidden());
            mvc.perform(get("/percorsi-terapeutici/{id}/paziente", pazienteA.getId()).header(AUTH, t)).andExpect(status().isForbidden());
            mvc.perform(get("/valutazioni/paziente/{id}", pazienteA.getId()).header(AUTH, t)).andExpect(status().isForbidden());
        }

        @Test
        void tutoreVedeAllegatiDelFiglio() throws Exception {
            mvc.perform(get("/allegati/paziente/{id}", pazienteA.getId()).header(AUTH, bearer(tutoreAUser)))
                    .andExpect(status().isOk());
        }

        @Test
        void medicoNonCreaPercorsiPerPazientiNonInCarico() throws Exception {
            mvc.perform(post("/percorsi-terapeutici").header(AUTH, bearer(medicoSenzaPazienti.getUser()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"pazienteId\":" + pazienteA.getId() + ",\"titolo\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    class Ruoli {
        @Test
        void dashboardAdminSoloPerAdmin() throws Exception {
            mvc.perform(get("/dashboard").header(AUTH, bearer(tutoreAUser))).andExpect(status().isForbidden());
        }

        @Test
        void tutoreNonModificaProfiliMedico() throws Exception {
            mvc.perform(put("/medico/update").header(AUTH, bearer(tutoreAUser))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":" + medicoA.getId() + ",\"specializzazione\":\"x\",\"numeroAlbo\":\"x\"}"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void medicoModificaSoloIlProprioProfiloAncheConIdAltrui() throws Exception {
            mvc.perform(put("/medico/update").header(AUTH, bearer(medicoSenzaPazienti.getUser()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":" + medicoA.getId() + ",\"specializzazione\":\"x\",\"numeroAlbo\":\"ALBO-B\",\"bio\":\"nuova bio\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(medicoSenzaPazienti.getId()));
        }
    }
}
