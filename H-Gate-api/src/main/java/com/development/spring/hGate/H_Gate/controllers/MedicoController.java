package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.mappers.MedicoMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("medico")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;

    @GetMapping("/all")
    public ResponseDTO<List<MedicoDTO>> getAll() {
        ResponseDTO<List<MedicoDTO>> res = new ResponseDTO<>();
        try {

            res.setOk(true);
            res.setData(medicoMapper.convertModelsToDtos(medicoService.getAll()));

        } catch (Exception e) {
            res.setOk(false);
            res.setMessage(e.getMessage());
        }

        return res;
    }

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

    @PutMapping("/update")
    public ResponseDTO<MedicoDTO> update(@Valid @RequestBody Medico medico) {
        ResponseDTO<MedicoDTO> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(medicoMapper.convertModelToDTO(medicoService.update(medico)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
