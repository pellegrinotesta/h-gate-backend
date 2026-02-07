package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.entity.VAgendaMedici;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.AgendaMediciService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda")
public class AgendaController {

    private final AgendaMediciService agendaMediciService;

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
