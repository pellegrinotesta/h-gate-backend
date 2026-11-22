package com.development.spring.hGate.H_Gate.audit;

import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditTest extends IntegrationTestBase {

    @Autowired
    private AuditLogRepository auditRepository;

    @AfterEach
    void sblocca() {
        loginAttemptService.clearCache("tutore.a@test.it");
    }

    private List<AuditLog> righe() {
        return auditRepository.findAll();
    }

    private AuditLog ultima() {
        List<AuditLog> r = righe();
        return r.get(r.size() - 1);
    }

    @Test
    void letturaDelPazienteRegistrataConPazienteEUtente() throws Exception {
        mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk());
        AuditLog r = ultima();
        assertThat(r.getAzione()).isEqualTo(AuditLog.AzioneAudit.LETTURA);
        assertThat(r.getEsito()).isEqualTo(AuditLog.EsitoAudit.OK);
        assertThat(r.getPazienteId()).isEqualTo(pazienteA.getId());
        assertThat(r.getUserEmail()).isEqualTo("tutore.a@test.it");
        assertThat(r.getRuoli()).isEqualTo("TUTORE");
        assertThat(r.getHttpStatus()).isEqualTo(200);
    }

    @Test
    void accessoNegatoRegistrato() throws Exception {
        mvc.perform(get("/referto/{id}", refertoA.getId()).header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
        AuditLog r = ultima();
        assertThat(r.getEsito()).isEqualTo(AuditLog.EsitoAudit.NEGATO);
        assertThat(r.getRisorsaTipo()).isEqualTo("REFERTO");
        assertThat(r.getRisorsaId()).isEqualTo(refertoA.getId());
        // anche se l'accesso è negato si sa di quale paziente erano i dati richiesti
        assertThat(r.getPazienteId()).isEqualTo(pazienteA.getId());
    }

    @Test
    void downloadEModificheHannoLAzioneGiusta() throws Exception {
        byte[] pdf = "%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII);
        mvc.perform(multipart("/allegati/prenotazione/{id}", prenotazioneA.getId())
                .file(new MockMultipartFile("files", "a.pdf", "application/pdf", pdf))
                .header("Authorization", bearer(tutoreAUser))).andExpect(status().isOk());
        assertThat(ultima().getAzione()).isEqualTo(AuditLog.AzioneAudit.CREAZIONE);

        Integer allegatoId = allegatoRepository.findByPrenotazioneId(prenotazioneA.getId()).get(0).getId();
        mvc.perform(get("/allegati/{id}/download", allegatoId).header("Authorization", bearer(medicoA.getUser())))
                .andExpect(status().isOk());
        AuditLog r = ultima();
        assertThat(r.getAzione()).isEqualTo(AuditLog.AzioneAudit.DOWNLOAD);
        assertThat(r.getRisorsaTipo()).isEqualTo("ALLEGATO");
        assertThat(r.getPazienteId()).isEqualTo(pazienteA.getId());
    }

    @Test
    void loginRiuscitiEFallitiSenzaPassword() throws Exception {
        mvc.perform(post("/authentication").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"tutore.a@test.it\",\"password\":\"sbagliata\"}"));
        mvc.perform(post("/authentication").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"tutore.a@test.it\",\"password\":\"" + PASSWORD + "\"}"));
        List<AuditLog> r = righe();
        assertThat(r).extracting(AuditLog::getAzione)
                .contains(AuditLog.AzioneAudit.LOGIN_FALLITO, AuditLog.AzioneAudit.LOGIN);
        assertThat(r).allSatisfy(a -> assertThat(a.getPath()).doesNotContain("sbagliata"));
    }

    @Test
    void richiesteNonClinicheNonVengonoRegistrate() throws Exception {
        mvc.perform(get("/notifiche/conteggio-non-lette").header("Authorization", bearer(tutoreAUser)));
        assertThat(righe()).isEmpty();
    }

    @Test
    void adminCercaPerCodiceFiscale() throws Exception {
        mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(medicoA.getUser())));
        mvc.perform(get("/paziente/{id}", pazienteB.getId()).header("Authorization", bearer(tutoreBUser)));

        mvc.perform(get("/admin/audit").param("codiceFiscale", pazienteA.getCodiceFiscale().toLowerCase())
                        .header("Authorization", bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[*].pazienteId", everyItem(is(pazienteA.getId()))))
                .andExpect(jsonPath("$.data.content[0].userEmail").value("medico.a@test.it"))
                .andExpect(jsonPath("$.data.content[0].pazienteNome").value("Marco Verdi"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    void soloLAdminConsultaIlRegistroCompleto() throws Exception {
        mvc.perform(get("/admin/audit").header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/admin/audit").header("Authorization", bearer(medicoA.getUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    void ilRegistroNonSiPuoModificare() throws Exception {
        mvc.perform(delete("/admin/audit").header("Authorization", bearer(admin)))
                .andExpect(status().isMethodNotAllowed());
        mvc.perform(put("/admin/audit").header("Authorization", bearer(admin)))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void ilTutoreVedeChiHaConsultatoIDatiDelFiglio() throws Exception {
        mvc.perform(get("/referto/{id}", refertoA.getId()).header("Authorization", bearer(medicoA.getUser())));
        mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)));

        mvc.perform(get("/consensi/paziente/{id}/accessi", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].ruolo", hasItem("Specialista")))
                .andExpect(jsonPath("$.data[*].risorsaTipo", hasItem("REFERTO")))
                // gli accessi del tutore stesso non sono elencati
                .andExpect(jsonPath("$.data[*].ruolo", not(hasItem("Tutore"))));

        mvc.perform(get("/consensi/paziente/{id}/accessi", pazienteA.getId()).header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void iTentativiNegatiDiAltriTutoriSonoAnonimi() throws Exception {
        mvc.perform(get("/referto/{id}", refertoA.getId()).header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/consensi/paziente/{id}/accessi", pazienteA.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(jsonPath("$.data[0].utente").value("Utente non autorizzato"))
                .andExpect(jsonPath("$.data[0].esito").value("NEGATO"));
    }

    @Autowired
    private com.development.spring.hGate.H_Gate.repositories.AllegatoRepository allegatoRepository;
}
