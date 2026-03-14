package com.development.spring.hGate.H_Gate.shared.controllers;

import com.development.spring.hGate.H_Gate.security.dtos.AuthenticationRequestDTO;
import com.development.spring.hGate.H_Gate.security.dtos.AuthenticationResponseDTO;
import com.development.spring.hGate.H_Gate.security.services.AuthenticationService;
import com.development.spring.hGate.H_Gate.security.services.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticazione", description = "Endpoint per il login e la gestione dei token JWT")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Operation(
            summary = "Login Utente",
            description = "Verifica le credenziali (username/email e password) e restituisce un token JWT valido per le chiamate successive."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticazione riuscita, token generato"),
            @ApiResponse(responseCode = "401", description = "Credenziali errate o utente non trovato")
    })
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

    @Operation(
            summary = "Refresh Token",
            description = "Permette di rinnovare la validità del token JWT esistente prima della sua scadenza."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token rinnovato con successo"),
            @ApiResponse(responseCode = "401", description = "Sessione non valida o scaduta"),
            @ApiResponse(responseCode = "500", description = "Errore interno durante il rinnovo del token")
    })
    @PutMapping
    public AuthenticationResponseDTO refreshToken(
            @Parameter(description = "Token attuale nell'header Authorization", example = "Bearer eyJhbGci...")
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @Parameter(hidden = true) Authentication authentication
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