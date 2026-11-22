package com.development.spring.hGate.H_Gate.shared;

import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Gli errori arrivano con lo status HTTP corretto e il corpo {ok:false, message},
 * non più come "200 OK" con ok:false.
 */
class ErrorHandlingTest extends IntegrationTestBase {

    @Test
    void erroreDiBusinessHaStatusEMessaggio() throws Exception {
        // prenotazione nel passato -> 400 con il messaggio del service
        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pazienteId\":" + pazienteA.getId() + ",\"medicoId\":" + medicoA.getId()
                                + ",\"dataOra\":\"2020-01-01T10:00:00\",\"tipoVisita\":\"Visita\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false))
                .andExpect(jsonPath("$.message").value(containsString("passato")));
    }

    @Test
    void risorsaNonTrovata404() throws Exception {
        mvc.perform(get("/prenotazione/disponibilita/{id}", 999_999).param("data", "2030-01-07")
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ok").value(false));
    }

    @Test
    void dataNonValida400ConMessaggioChiaro() throws Exception {
        mvc.perform(get("/prenotazione/disponibilita/{id}", medicoA.getId()).param("data", "07/01/2030")
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("AAAA-MM-GG")));
    }

    @Test
    void jsonMalformato400() throws Exception {
        mvc.perform(post("/prenotazione").header("Authorization", bearer(tutoreAUser))
                        .contentType(MediaType.APPLICATION_JSON).content("{non json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok").value(false));
    }

    @Test
    void validazioneDelBody400() throws Exception {
        mvc.perform(put("/medico/update").header("Authorization", bearer(medicoA.getUser()))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Dati non validi")));
    }

    @Test
    void accessoNegato403ConMessaggio() throws Exception {
        mvc.perform(get("/dashboard").header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(containsString("permessi")));
    }

    @Test
    void notificaAltruiNonRivelaDettagliInterni() throws Exception {
        // IllegalArgumentException("Non autorizzato") nel service -> 403, non 200/500
        var notifica = notificheRepository.save(com.development.spring.hGate.H_Gate.entity.Notifica.builder()
                .user(tutoreBUser)
                .tipo(com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum.SISTEMA)
                .titolo("t").messaggio("m").build());
        mvc.perform(put("/notifiche/{id}/letta", notifica.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(not(containsString("Exception"))));
    }

    @org.springframework.beans.factory.annotation.Autowired
    private com.development.spring.hGate.H_Gate.repositories.NotificheRepository notificheRepository;
}
