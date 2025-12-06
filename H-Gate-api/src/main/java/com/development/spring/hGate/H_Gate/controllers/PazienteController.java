package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.mappers.PazienteMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PazienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("paziente")
public class PazienteController {

    private final PazienteService pazienteService;
    private final PazienteMapper pazienteMapper;

    @GetMapping("/user-id")
    public ResponseDTO<PazienteDTO> findByUserId(JwtAuthentication jwtAuthentication) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(pazienteMapper.convertModelToDTO(pazienteService.findByUserId(jwtAuthentication)));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
