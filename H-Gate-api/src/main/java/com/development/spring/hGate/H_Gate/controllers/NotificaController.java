package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.NotificaDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.entity.Notifica;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.NotificaMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.NotificheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("notifiche")
public class NotificaController {

    private final NotificheService notificheService;
    private final NotificaMapper notificaMapper;

    @GetMapping
    public ResponseDTO<PageDTO<NotificaDTO>> getNotifiche(
            JwtAuthentication authentication,
            Pageable pageable
    ) {
        ResponseDTO<PageDTO<NotificaDTO>> res = new ResponseDTO<>();
        try {

            Page<Notifica> notifiche = notificheService
                    .getNotificheUtente(authentication.getId(), pageable);

            res.setOk(true);
            res.setData(notificaMapper.convertModelsPageToDtosPage(notifiche));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    /**
     * Ottiene solo le notifiche non lette
     */
    @GetMapping("/non-lette")
    public ResponseDTO<List<NotificaDTO>> getNotificheNonLette(JwtAuthentication authentication) {
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

    /**
     * Ottiene il conteggio delle notifiche non lette
     */
    @GetMapping("/conteggio-non-lette")
    public ResponseDTO<Long> getConteggioNonLette(JwtAuthentication authentication) {

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

    /**
     * Marca una notifica come letta
     */
    @PutMapping("/{id}/letta")
    public ResponseDTO<?> marcaComeLetta(
            @PathVariable Integer id,
            JwtAuthentication authentication
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

    /**
     * Marca tutte le notifiche come lette
     */
    @PutMapping("/marca-tutte-lette")
    public ResponseDTO<?> marcaTutteComeLette(JwtAuthentication authentication) {

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

    /**
     * Elimina una notifica
     */
    @DeleteMapping("/{id}")
    public ResponseDTO<?> eliminaNotifica(
            @PathVariable Integer id,
            JwtAuthentication authentication
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
