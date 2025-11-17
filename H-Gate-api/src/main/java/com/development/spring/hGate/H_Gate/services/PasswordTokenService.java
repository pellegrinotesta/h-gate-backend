package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.PasswordResetToken;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.repositories.PasswordTokenRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordTokenService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Scheduled(fixedDelay = 60 * 60 * 1000) // Esegui ogni ora
    public void removeExpiredPasswordTokens() {
        List<PasswordResetToken> expiredTokens = passwordTokenRepository.findByExpiryDateBefore(new Date());
        for (PasswordResetToken token : expiredTokens) {
            passwordTokenRepository.delete(token);
        }
    }

    public String resetPassword(String email) {
        Optional<Users> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.OK, "E-mail sent successfully");
        }
        Users user = optUser.get();
        String token = generateRandomString();
        createPasswordResetTokenForUser(user, token);
//        emailService.sendResetPassword(token, user);
        return "E-mail sent successfully";
    }

    public void createPasswordResetTokenForUser(Users user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public void changeUserPassword(Users user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }


    public static String generateRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

    public Optional<Users> getUserByPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);

        if (passwordResetToken != null) {
            // Se trovi il token, restituisci l'utente associato ad esso
            return Optional.of(passwordResetToken.getUser());
        } else {
            // Se il token non esiste o non è valido, restituisci Optional.empty()
            return Optional.empty();
        }
    }

    public String savePassword(String token, String password) {
        String result = validatePasswordResetToken(token);
        if(result != null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not valid!");
        }
        Optional<Users> user = getUserByPasswordResetToken(token);
        if(user.isPresent()) {
            changeUserPassword(user.get(), password);
            return "Password changed";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not existing");
        }
    }
}
