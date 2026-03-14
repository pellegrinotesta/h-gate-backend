package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.NotificaDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.entity.Notifica;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.NotificaMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.NotificheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("notifiche")
@Tag(name = "Notifiche", description = "Gestione delle notifiche utente: lettura, conteggio ed eliminazione")
public class NotificaController {

    private final NotificheService notificheService;
    private final NotificaMapper notificaMapper;

    @Operation(summary = "Ottiene tutte le notifiche", description = "Recupera la cronologia completa delle notifiche dell'utente autenticato con paginazione.")
    @GetMapping
    public ResponseDTO<PageDTO<NotificaDTO>> getNotifiche(
            @Parameter(hidden = true) JwtAuthentication authentication,
            @ParameterObject Pageable pageable
    ) {
        ResponseDTO<PageDTO<NotificaDTO>> res = new ResponseDTO<>();
        try {
            Page<Notifica> notifiche = notificheService.getNotificheUtente(authentication.getId(), pageable);
            res.setOk(true);
            res.setData(notificaMapper.convertModelsPageToDtosPage(notifiche));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Ottiene notifiche non lette", description = "Ritorna la lista delle notifiche che l'utente non ha ancora visualizzato.")
    @GetMapping("/non-lette")
    public ResponseDTO<List<NotificaDTO>> getNotificheNonLette(@Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<List<NotificaDTO>> res = new ResponseDTO<>();
        try {
            List<Notifica> notifiche = notificheService.getNotificheNonLette(authentication.getId());
            res.setOk(true);
            res.setData(notificaMapper.convertModelsToDtos(notifiche));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Conteggio non lette", description = "Restituisce il numero totale di notifiche non lette (utile per badge UI).")
    @GetMapping("/conteggio-non-lette")
    public ResponseDTO<Long> getConteggioNonLette(@Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<Long> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(notificheService.contaNonLette(authentication.getId()));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Marca come letta", description = "Cambia lo stato di una singola notifica in 'letta'.")
    @PutMapping("/{id}/letta")
    public ResponseDTO<?> marcaComeLetta(
            @Parameter(description = "ID della notifica") @PathVariable Integer id,
            @Parameter(hidden = true) JwtAuthentication authentication
    ) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try {
            notificheService.marcaComeLetta(id, authentication.getId());
            res.setOk(true);
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Marca tutte come lette", description = "Cambia lo stato di tutte le notifiche dell'utente in 'letta' in un'unica operazione.")
    @PutMapping("/marca-tutte-lette")
    public ResponseDTO<?> marcaTutteComeLette(@Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try {
            notificheService.marcaTutteComeLette(authentication.getId());
            res.setOk(true);
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Elimina notifica", description = "Rimuove definitivamente una notifica per l'utente autenticato.")
    @DeleteMapping("/{id}")
    public ResponseDTO<?> eliminaNotifica(
            @Parameter(description = "ID della notifica da eliminare") @PathVariable Integer id,
            @Parameter(hidden = true) JwtAuthentication authentication
    ) {
        ResponseDTO<?> res = new ResponseDTO<>();
        try {
            notificheService.eliminaNotifica(id, authentication.getId());
            res.setOk(true);
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}