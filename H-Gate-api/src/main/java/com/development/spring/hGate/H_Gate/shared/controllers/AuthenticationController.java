package com.development.spring.hGate.H_Gate.shared.controllers;

import com.development.spring.hGate.H_Gate.security.dtos.AuthenticationRequestDTO;
import com.development.spring.hGate.H_Gate.security.dtos.AuthenticationResponseDTO;
import com.development.spring.hGate.H_Gate.security.services.AuthenticationService;
import com.development.spring.hGate.H_Gate.security.services.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping
    public AuthenticationResponseDTO authenticate(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {
        Optional<String> authenticationToken = authenticationService.authenticate(
                authenticationRequestDTO.getUsername(),
                authenticationRequestDTO.getPassword());
        if (authenticationToken.isPresent()) {
            return AuthenticationResponseDTO.builder().authentication(authenticationToken.get()).build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect credentials.");
        }
    }

    @PutMapping
    public AuthenticationResponseDTO refreshToken(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            Authentication authentication
    ) {
        if (authentication != null) {
            Optional<String> authenticationToken = authenticationService.renewAuthentication(authentication);
            if (authenticationToken.isPresent()) {
                return AuthenticationResponseDTO.builder().authentication(authenticationToken.get()).build();
            }
            Optional<String> currentToken = jwtService.extractTokenFromAuthorizationHeader(authorizationHeader);
            if (currentToken.isPresent()) {
                return AuthenticationResponseDTO.builder().authentication(currentToken.get()).build();
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to refresh authentication.");
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not logged in.");
    }

}
