package com.development.spring.hGate.H_Gate.profile.service;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String INVALID_PASSWORD = "Unable to change password: invalid current password";

    public Users getProfile(JwtAuthentication jwtAuthentication) {
        return userService.getById(jwtAuthentication.getId());
    }

    public Users partialUpdate(JwtAuthentication jwtAuthentication, Users userToEdit, String newPassword) {
        var user = userService.getById(jwtAuthentication.getId());
        if (userToEdit.getPassword() != null) {
            if (!isUserPasswordCorrect(user.getPassword(), userToEdit.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, INVALID_PASSWORD);
            }
            userToEdit.setPassword(newPassword);
        }
        userToEdit.setId(jwtAuthentication.getId());
        return userService.partialUpdate(userToEdit);
    }

    private boolean isUserPasswordCorrect(String encodedPassword, String rawPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
