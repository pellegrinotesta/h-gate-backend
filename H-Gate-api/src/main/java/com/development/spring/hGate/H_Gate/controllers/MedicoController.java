package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.mappers.MedicoMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.MedicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("medico")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;

    @GetMapping("/user-id")
    public ResponseDTO<MedicoDTO> findMedicoByUserId(JwtAuthentication jwtAuthentication) {
        ResponseDTO<MedicoDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(medicoMapper.convertModelToDTO(medicoService.findMedicoByUserId(jwtAuthentication)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}
