package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.UserRegistrationDTO;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("registration")
@Tag(name = "Registrazione", description = "Endpoint per la creazione di nuovi account utente nel sistema")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Operation(
            summary = "Registra un nuovo utente",
            description = "Crea un nuovo profilo utente (Medico o Tutore) nel sistema. Valida i dati forniti e restituisce l'utente creato."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registrazione completata con successo",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dati di registrazione non validi o utente già esistente", content = @Content),
            @ApiResponse(responseCode = "500", description = "Errore durante il processo di salvataggio", content = @Content)
    })
    @PostMapping("")
    public ResponseDTO<Users> register(
            @Valid @RequestBody UserRegistrationDTO dto) {
        ResponseDTO<Users> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(registrationService.registerUser(dto));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}