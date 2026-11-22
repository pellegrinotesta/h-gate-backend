package com.development.spring.hGate.H_Gate.security.services;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blocco temporaneo dopo troppi tentativi di login falliti.
 * <p>
 * Dopo {@value #MAX_ATTEMPTS} tentativi falliti entro {@link #WINDOW} l'account resta bloccato
 * per {@link #LOCK_DURATION}, poi si sblocca da solo. Lo stato è in memoria: con più
 * istanze del backend andrebbe spostato su un archivio condiviso (DB/Redis).
 */
@Service
public class LoginAttemptService {

    static final int MAX_ATTEMPTS = 5;
    static final Duration WINDOW = Duration.ofMinutes(15);
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record Attempts(int count, Instant firstFailure, Instant lockedUntil) {}

    private final ConcurrentHashMap<String, Attempts> attempts = new ConcurrentHashMap<>();

    public void loginFailed(String email) {
        Instant now = Instant.now();
        attempts.compute(key(email), (k, current) -> {
            if (current == null || current.firstFailure().plus(WINDOW).isBefore(now)) {
                current = new Attempts(0, now, null);
            }
            int count = current.count() + 1;
            Instant lockedUntil = count >= MAX_ATTEMPTS ? now.plus(LOCK_DURATION) : current.lockedUntil();
            return new Attempts(count, current.firstFailure(), lockedUntil);
        });
    }

    public boolean isBlocked(String email) {
        Attempts a = attempts.get(key(email));
        if (a == null || a.lockedUntil() == null) return false;
        if (a.lockedUntil().isBefore(Instant.now())) {
            attempts.remove(key(email));
            return false;
        }
        return true;
    }

    public void clearCache(String email) {
        attempts.remove(key(email));
    }

    private String key(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
