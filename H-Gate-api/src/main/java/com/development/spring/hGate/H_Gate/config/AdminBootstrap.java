package com.development.spring.hGate.H_Gate.config;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Crea il primo amministratore all'avvio, solo se non ne esiste già uno e se
 * ADMIN_EMAIL e ADMIN_PASSWORD sono impostate. Serve per installazioni pulite
 * (senza i dati demo di data.sql), dove altrimenti non ci sarebbe modo di accedere come admin.
 */
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final int MIN_PASSWORD_LENGTH = 12;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.bootstrap.admin.email:}")
    private String adminEmail;

    @Value("${application.bootstrap.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }
        if (userRepository.existsByRole(Role.ADMIN)) {
            logger.info("Amministratore già presente: bootstrap ignorato.");
            return;
        }
        if (adminPassword.length() < MIN_PASSWORD_LENGTH) {
            logger.error("ADMIN_PASSWORD troppo corta (minimo {} caratteri): amministratore NON creato.", MIN_PASSWORD_LENGTH);
            return;
        }
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            logger.error("Esiste già un utente con email {}: amministratore NON creato.", adminEmail);
            return;
        }

        Users admin = Users.builder()
                .uuid(UUID.randomUUID().toString())
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .nome("Amministratore")
                .cognome("Clinica")
                .isActive(true)
                .isVerified(true)
                .roles(new java.util.HashSet<>(Set.of(Role.ADMIN)))
                .createdAt(new Date())
                .build();
        userRepository.save(admin);
        logger.info("Creato l'amministratore iniziale {}. Cambia la password al primo accesso.", adminEmail);
    }
}
