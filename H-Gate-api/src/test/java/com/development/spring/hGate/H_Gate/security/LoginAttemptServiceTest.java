package com.development.spring.hGate.H_Gate.security;

import com.development.spring.hGate.H_Gate.security.services.LoginAttemptService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Test
    void bloccaDopoCinqueTentativi() {
        for (int i = 0; i < 4; i++) service.loginFailed("a@test.it");
        assertThat(service.isBlocked("a@test.it")).isFalse();
        service.loginFailed("a@test.it");
        assertThat(service.isBlocked("a@test.it")).isTrue();
    }

    @Test
    void emailNonDistingueMaiuscole() {
        for (int i = 0; i < 5; i++) service.loginFailed("A@Test.it ");
        assertThat(service.isBlocked("a@test.it")).isTrue();
    }

    @Test
    void loginRiuscitoAzzeraIlContatore() {
        for (int i = 0; i < 4; i++) service.loginFailed("a@test.it");
        service.clearCache("a@test.it");
        service.loginFailed("a@test.it");
        assertThat(service.isBlocked("a@test.it")).isFalse();
    }

    @Test
    void utentiDiversiSonoIndipendenti() {
        for (int i = 0; i < 5; i++) service.loginFailed("a@test.it");
        assertThat(service.isBlocked("b@test.it")).isFalse();
    }
}
