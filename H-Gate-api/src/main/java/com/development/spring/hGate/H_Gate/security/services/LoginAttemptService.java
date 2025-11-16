package com.development.spring.hGate.H_Gate.security.services;

import com.development.spring.hGate.H_Gate.services.EmailService;
import com.development.spring.hGate.H_Gate.services.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private final UserService userService;
    private final EmailService emailService;
    private final int MAX_ATTEMPT = 5;
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Set<String> lockCache = ConcurrentHashMap.newKeySet();  // Replacing ConcurrentSet with ConcurrentHashMap.newKeySet()

    public LoginAttemptService(@Lazy UserService userService, @Lazy EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    public void loginFailed(String email) {
        int attempts = attemptsCache.getOrDefault(email, 0);
        attempts++;
        attemptsCache.put(email, attempts);

        // If the number of attempts exceeds the max, lock the user
        if (attempts >= MAX_ATTEMPT) {
            lockCache.add(email);
        }
    }

    public boolean isBlocked(String email) {
        return lockCache.contains(email);
    }

    public void clearCache(String email) {
        attemptsCache.remove(email);
        lockCache.remove(email);
    }
}
