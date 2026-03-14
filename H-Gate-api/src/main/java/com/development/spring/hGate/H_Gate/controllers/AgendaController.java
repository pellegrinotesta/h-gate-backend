package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.entity.VAgendaMedici;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.AgendaMediciService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
@Tag(name = "Agenda Medico", description = "Endpoint per la gestione e consultazione degli appuntamenti medici")
public class AgendaController {

    private final AgendaMediciService agendaMediciService;

    @Operation(
            summary = "Recupera l'agenda del medico",
            description = "Ritorna la lista di tutti gli appuntamenti associati al medico autenticato tramite JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operazione riuscita",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResponseDTO.class)) }),
            @ApiResponse(responseCode = "403", description = "Accesso negato - Ruolo non autorizzato", content = @Content),
            @ApiResponse(responseCode = "500", description = "Errore interno del server", content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<List<VAgendaMedici>> findByMedicoId(JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<VAgendaMedici>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(agendaMediciService.findByMedicoId(jwtAuthentication.getId()));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
