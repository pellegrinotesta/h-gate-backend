package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.ApprovazioniConteggioDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.MedicoInAttesaDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.MotivoRifiutoDTO;
import com.development.spring.hGate.H_Gate.dtos.approvazioni.RichiestaAssociazioneDTO;
import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.ApprovazioniService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gli errori (404 / 409) vengono propagati con il loro status HTTP, senza essere
 * trasformati in "200 + ok:false", così il frontend li gestisce correttamente.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/approvazioni")
@PreAuthorize("hasAuthority('ADMIN')")
@Tag(name = "Approvazioni", description = "Approvazione dei medici registrati e delle associazioni tutore-minore")
public class ApprovazioniController {

    private final ApprovazioniService approvazioniService;

    @Operation(summary = "Numero di richieste in attesa")
    @GetMapping("/conteggio")
    public ResponseDTO<ApprovazioniConteggioDTO> conteggio() {
        return ok(approvazioniService.conteggio(), null);
    }

    @Operation(summary = "Medici in attesa di approvazione")
    @GetMapping("/medici")
    public ResponseDTO<List<MedicoInAttesaDTO>> mediciInAttesa() {
        return ok(approvazioniService.mediciInAttesa(), null);
    }

    @Operation(summary = "Approva un medico", description = "Il medico potrà accedere e sarà visibile e prenotabile dai tutori.")
    @PutMapping("/medici/{medicoId}/approva")
    public ResponseDTO<Void> approvaMedico(
            @Parameter(hidden = true) JwtAuthentication auth,
            @PathVariable("medicoId") Integer medicoId) {
        approvazioniService.approvaMedico(medicoId, auth.getId());
        return ok(null, "Medico approvato");
    }

    @Operation(summary = "Rifiuta un medico", description = "L'account viene disattivato e il medico avvisato via email.")
    @PutMapping("/medici/{medicoId}/rifiuta")
    public ResponseDTO<Void> rifiutaMedico(
            @Parameter(hidden = true) JwtAuthentication auth,
            @PathVariable("medicoId") Integer medicoId,
            @Valid @RequestBody(required = false) MotivoRifiutoDTO body) {
        approvazioniService.rifiutaMedico(medicoId, auth.getId(), body != null ? body.getMotivo() : null);
        return ok(null, "Registrazione rifiutata");
    }

    @Operation(summary = "Richieste di associazione tutore-minore")
    @GetMapping("/associazioni")
    public ResponseDTO<List<RichiestaAssociazioneDTO>> associazioni(
            @RequestParam(name = "stato", defaultValue = "IN_ATTESA") StatoRichiestaEnum stato) {
        return ok(approvazioniService.richieste(stato), null);
    }

    @Operation(summary = "Approva un'associazione", description = "Il tutore viene collegato al minore e ne vede la cartella.")
    @PutMapping("/associazioni/{richiestaId}/approva")
    public ResponseDTO<Void> approvaAssociazione(
            @Parameter(hidden = true) JwtAuthentication auth,
            @PathVariable("richiestaId") Integer richiestaId) {
        approvazioniService.approvaAssociazione(richiestaId, auth.getId());
        return ok(null, "Associazione approvata");
    }

    @Operation(summary = "Rifiuta un'associazione")
    @PutMapping("/associazioni/{richiestaId}/rifiuta")
    public ResponseDTO<Void> rifiutaAssociazione(
            @Parameter(hidden = true) JwtAuthentication auth,
            @PathVariable("richiestaId") Integer richiestaId,
            @Valid @RequestBody(required = false) MotivoRifiutoDTO body) {
        approvazioniService.rifiutaAssociazione(richiestaId, auth.getId(), body != null ? body.getMotivo() : null);
        return ok(null, "Associazione rifiutata");
    }

    private <T> ResponseDTO<T> ok(T data, String message) {
        ResponseDTO<T> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(data);
        res.setMessage(message);
        return res;
    }
}
