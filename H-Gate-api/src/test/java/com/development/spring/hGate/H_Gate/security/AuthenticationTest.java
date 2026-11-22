package com.development.spring.hGate.H_Gate.security;

import com.development.spring.hGate.H_Gate.support.IntegrationTestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationTest extends IntegrationTestBase {

    @AfterEach
    void pulisciBlocchi() {
        loginAttemptService.clearCache("tutore.a@test.it");
    }

    private ResultActions login(String email, String password) throws Exception {
        return mvc.perform(post("/authentication").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + email + "\",\"password\":\"" + password + "\"}"));
    }

    @Test
    void loginRestituisceUnJwtInChiaro() throws Exception {
        login("tutore.a@test.it", PASSWORD)
                .andExpect(status().isOk())
                // header.payload.firma: niente più cifratura AES con chiave nel frontend
                .andExpect(jsonPath("$.authentication").value(matchesPattern("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$")));
    }

    @Test
    void credenzialiErrate401() throws Exception {
        login("tutore.a@test.it", "sbagliata")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenziali non valide."));
    }

    @Test
    void bloccoDopoCinqueTentativiAncheConPasswordGiusta() throws Exception {
        for (int i = 0; i < 5; i++) login("tutore.a@test.it", "sbagliata");
        login("tutore.a@test.it", PASSWORD)
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value(containsString("15 minuti")));
    }

    @Test
    void utenteDisattivatoNonAccede() throws Exception {
        tutoreAUser.setIsActive(false);
        userRepository.save(tutoreAUser);
        login("tutore.a@test.it", PASSWORD).andExpect(status().isUnauthorized());
    }

    @Test
    void senzaTokenRisponde401InJson() throws Exception {
        mvc.perform(get("/paziente/{id}", pazienteA.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ok").value(false));
    }

    @Test
    void tokenManomessoRisponde401() throws Exception {
        String t = token(tutoreAUser);
        String manomesso = t.substring(0, t.length() - 4) + "abcd";
        mvc.perform(get("/paziente/{id}", pazienteA.getId()).header("Authorization", "Bearer " + manomesso))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshDelToken() throws Exception {
        mvc.perform(put("/authentication").header("Authorization", bearer(tutoreAUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authentication").isNotEmpty());
    }
}
