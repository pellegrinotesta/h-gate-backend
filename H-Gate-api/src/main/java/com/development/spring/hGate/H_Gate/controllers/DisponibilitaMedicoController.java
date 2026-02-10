package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.DisponibilitaMediciDTO;
import com.development.spring.hGate.H_Gate.mappers.DisponibilitaMediciIMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DisponibilitaMediciService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("disponibilita/medico")
public class DisponibilitaMedicoController {

    private final DisponibilitaMediciService disponibilitaMediciService;
    private final DisponibilitaMediciIMapper disponibilitaMediciIMapper;

    @GetMapping("/{medicoUserId}")
    public ResponseDTO<List<DisponibilitaMediciDTO>> findByMedicoUserId(@PathVariable Integer medicoUserId) {
        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.findByMedicoUserId(medicoUserId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
