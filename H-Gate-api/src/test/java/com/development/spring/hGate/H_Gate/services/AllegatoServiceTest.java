package com.development.spring.hGate.H_Gate.services;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class AllegatoServiceTest {

    private static MockMultipartFile file(String type, byte... bytes) {
        return new MockMultipartFile("files", "f", type, bytes);
    }

    @Test
    void riconosceIFormatiAmmessi() {
        assertThat(AllegatoService.contenutoCorrisponde(file("application/pdf", (byte) '%', (byte) 'P', (byte) 'D', (byte) 'F', (byte) '-'), "application/pdf")).isTrue();
        assertThat(AllegatoService.contenutoCorrisponde(file("image/jpeg", (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0), "image/jpeg")).isTrue();
        assertThat(AllegatoService.contenutoCorrisponde(file("image/png", (byte) 0x89, (byte) 'P', (byte) 'N', (byte) 'G', (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A), "image/png")).isTrue();
    }

    @Test
    void rifiutaContenutiCheNonCorrispondono() {
        assertThat(AllegatoService.contenutoCorrisponde(file("application/pdf", (byte) 'M', (byte) 'Z'), "application/pdf")).isFalse();
        assertThat(AllegatoService.contenutoCorrisponde(file("image/png", (byte) 0xFF, (byte) 0xD8, (byte) 0xFF), "image/png")).isFalse();
        assertThat(AllegatoService.contenutoCorrisponde(file("application/pdf"), "application/pdf")).isFalse();
    }

    @Test
    void nomeOriginaleRipulito() {
        assertThat(AllegatoService.nomeOriginaleSicuro("../../etc/passwd.pdf", "pdf")).isEqualTo("passwd.pdf");
        assertThat(AllegatoService.nomeOriginaleSicuro("C:\\Users\\a\\doc.pdf", "pdf")).isEqualTo("doc.pdf");
        assertThat(AllegatoService.nomeOriginaleSicuro("a\"b<c>.pdf", "pdf")).isEqualTo("a_b_c_.pdf");
        assertThat(AllegatoService.nomeOriginaleSicuro(null, "png")).isEqualTo("allegato.png");
        assertThat(AllegatoService.nomeOriginaleSicuro("..", "pdf")).isEqualTo("allegato.pdf");
    }
}
