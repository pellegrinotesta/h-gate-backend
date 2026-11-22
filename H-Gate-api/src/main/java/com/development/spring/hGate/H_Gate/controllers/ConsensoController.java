package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.audit.AccessoPazienteDTO;
import com.development.spring.hGate.H_Gate.audit.AuditQueryService;
import com.development.spring.hGate.H_Gate.audit.AuditService;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.consensi.RevocaConsensoDTO;
import com.development.spring.hGate.H_Gate.dtos.consensi.StatoConsensoDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.ConsensoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("consensi")
@Tag(name = "Privacy e consensi", description = "Consensi al trattamento dei dati dei minori e registro degli accessi visibile ai tutori")
public class ConsensoController {

    private static final int LIMITE_ACCESSI = 200;

    private final ConsensoService consensoService;
    private final AuditQueryService auditQueryService;

    @Operation(summary = "Versione dell'informativa privacy in vigore")
    @GetMapping("/informativa")
    public ResponseDTO<Map<String, String>> informativa() {
        return ok(Map.of("versione", consensoService.getVersioneInformativa()), null);
    }

    @Operation(summary = "Stato dei consensi dei minori del tutore autenticato")
    @GetMapping("/miei")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<List<StatoConsensoDTO>> miei(@Parameter(hidden = true) JwtAuthentication auth) {
        return ok(consensoService.statoMinoriDelTutore(auth.getId()), null);
    }

    @Operation(summary = "Stato e storico del consenso di un minore")
    @GetMapping("/paziente/{id}")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN') and @access.paziente(authentication, #id)")
    public ResponseDTO<StatoConsensoDTO> stato(@PathVariable("id") Integer id) {
        return ok(consensoService.stato(id, true), null);
    }

    @Operation(summary = "Presta il consenso", description = "Registra il consenso sull'informativa in vigore.")
    @PostMapping("/paziente/{id}/concedi")
    @PreAuthorize("hasAuthority('TUTORE') and @access.paziente(authentication, #id)")
    public ResponseDTO<StatoConsensoDTO> concedi(@Parameter(hidden = true) JwtAuthentication auth,
                                                 @PathVariable("id") Integer id, HttpServletRequest request) {
        consensoService.concedi(id, auth.getId(), AuditService.clientIp(request));
        return ok(consensoService.stato(id, true), "Consenso registrato");
    }

    @Operation(summary = "Revoca il consenso",
            description = "Blocca nuove prenotazioni. La documentazione clinica già prodotta resta conservata come previsto dalla legge.")
    @PostMapping("/paziente/{id}/revoca")
    @PreAuthorize("hasAuthority('TUTORE') and @access.paziente(authentication, #id)")
    public ResponseDTO<StatoConsensoDTO> revoca(@Parameter(hidden = true) JwtAuthentication auth,
                                                @PathVariable("id") Integer id,
                                                @Valid @RequestBody(required = false) RevocaConsensoDTO body,
                                                HttpServletRequest request) {
        consensoService.revoca(id, auth.getId(), AuditService.clientIp(request), body != null ? body.getMotivo() : null);
        return ok(consensoService.stato(id, true), "Consenso revocato");
    }

    @Operation(summary = "Chi ha consultato i dati del minore",
            description = "Accessi di altri utenti (specialisti, amministrazione) ai dati del minore, dal registro accessi.")
    @GetMapping("/paziente/{id}/accessi")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'ADMIN') and @access.paziente(authentication, #id)")
    public ResponseDTO<List<AccessoPazienteDTO>> accessi(@Parameter(hidden = true) JwtAuthentication auth,
                                                         @PathVariable("id") Integer id) {
        return ok(auditQueryService.accessiPaziente(id, auth.getId(), LIMITE_ACCESSI), null);
    }

    private <T> ResponseDTO<T> ok(T data, String message) {
        ResponseDTO<T> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(data);
        res.setMessage(message);
        return res;
    }
}
