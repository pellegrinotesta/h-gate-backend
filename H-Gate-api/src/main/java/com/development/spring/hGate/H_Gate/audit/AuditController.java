package com.development.spring.hGate.H_Gate.audit;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/** Consultazione del registro accessi: solo lettura, nessuna modifica possibile. */
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/audit")
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Registro accessi", description = "Consultazione degli accessi ai dati clinici (solo amministratori)")
public class AuditController {

    private final AuditQueryService auditQueryService;

    @Operation(summary = "Cerca nel registro accessi")
    @GetMapping
    public ResponseDTO<PaginaDTO<AuditLogDTO>> cerca(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String codiceFiscale,
            @RequestParam(required = false) Integer pazienteId,
            @RequestParam(required = false) AuditLog.AzioneAudit azione,
            @RequestParam(required = false) AuditLog.EsitoAudit esito,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate da,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate a,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        ResponseDTO<PaginaDTO<AuditLogDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(PaginaDTO.of(auditQueryService.cerca(
                new AuditQueryService.Filtri(email, codiceFiscale, pazienteId, azione, esito, da, a), page, size)));
        return res;
    }
}
