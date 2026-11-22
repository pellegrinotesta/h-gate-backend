package com.development.spring.hGate.H_Gate.allegati;

import com.development.spring.hGate.H_Gate.entity.Allegato;
import com.development.spring.hGate.H_Gate.repositories.AllegatoRepository;
import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AllegatiTest extends IntegrationTestBase {

    private static final byte[] PDF = "%PDF-1.4\n%test\n".getBytes(StandardCharsets.US_ASCII);

    @Autowired
    private AllegatoRepository allegatoRepository;

    private String url() {
        return "/allegati/prenotazione/" + prenotazioneA.getId();
    }

    @Test
    void uploadPdfValidoSalvatoConEstensioneCorretta() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "referto scuola.pdf", "application/pdf", PDF))
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk());

        Allegato a = allegatoRepository.findByPrenotazioneId(prenotazioneA.getId()).get(0);
        assertThat(a.getStoragePath()).endsWith(".pdf");   // prima era sempre ".bin"
        assertThat(a.getNomeFile()).isEqualTo("referto scuola.pdf");
        assertThat(a.getTipoFile()).isEqualTo("PDF");
    }

    @Test
    void contenutoCheNonCorrispondeAlTipoVieneRifiutato() throws Exception {
        byte[] eseguibile = {0x4D, 0x5A, (byte) 0x90, 0x00};  // "MZ": eseguibile Windows
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "finto.pdf", "application/pdf", eseguibile))
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("non corrisponde")));
    }

    @Test
    void tipoNonAmmessoVieneRifiutato() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "a.html", "text/html", "<script>".getBytes()))
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void nomeConPercorsoNonEsceDallaCartella() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "x./../../../etc/passwd.pdf", "application/pdf", PDF))
                        .header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk());
        Allegato a = allegatoRepository.findByPrenotazioneId(prenotazioneA.getId()).get(0);
        assertThat(a.getStoragePath()).doesNotContain("..").contains(String.valueOf(prenotazioneA.getId()));
        assertThat(a.getNomeFile()).isEqualTo("passwd.pdf");
    }

    @Test
    void hashRegistratoEFileAlteratoRilevato() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "a.pdf", "application/pdf", PDF))
                .header("Authorization", bearer(tutoreAUser))).andExpect(status().isOk());
        Allegato a = allegatoRepository.findByPrenotazioneId(prenotazioneA.getId()).get(0);
        assertThat(a.getHashFile()).hasSize(64);
        assertThat(a.getUploadedBy().getId()).isEqualTo(tutoreAUser.getId());

        // Qualcuno modifica il file su disco
        java.nio.file.Files.write(java.nio.file.Path.of(a.getStoragePath()), "%PDF-1.4 alterato".getBytes());
        mvc.perform(get("/allegati/{id}/download", a.getId()).header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("alterato")));
    }

    @Test
    void tutoreNonCaricaSuPrenotazioniAltrui() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "a.pdf", "application/pdf", PDF))
                        .header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void downloadConNomeOriginaleETipoEAccessoControllato() throws Exception {
        mvc.perform(multipart(url()).file(new MockMultipartFile("files", "relazione è.pdf", "application/pdf", PDF))
                .header("Authorization", bearer(tutoreAUser)));
        Allegato a = allegatoRepository.findByPrenotazioneId(prenotazioneA.getId()).get(0);

        mvc.perform(get("/allegati/{id}/download", a.getId()).header("Authorization", bearer(medicoA.getUser())))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(header().string("Content-Disposition", containsString("filename*=UTF-8''relazione%20%C3%A8.pdf")))
                .andExpect(content().bytes(PDF));

        mvc.perform(get("/allegati/{id}/download", a.getId()).header("Authorization", bearer(tutoreBUser)))
                .andExpect(status().isForbidden());
    }
}
