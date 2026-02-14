package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.mappers.PazienteMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PazienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("paziente")
public class PazienteController {

    private final PazienteService pazienteService;
    private final PazienteMapper pazienteMapper;

    @GetMapping("/user-id")
    public ResponseDTO<List<PazienteDTO>> findByUserId(JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<PazienteDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(pazienteMapper.convertModelsToDtos(pazienteService.findByUserId(jwtAuthentication)));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PutMapping("/update")
    public ResponseDTO<PazienteDTO> update(@Valid @RequestBody Paziente paziente) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(pazienteMapper.convertModelToDTO(pazienteService.updateInfo(paziente)));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PostMapping("/add")
    public ResponseDTO<PazienteDTO> addNewPaziente(JwtAuthentication jwtAuthentication, @RequestBody PrenotazioneDTO.PazienteMinDTO paziente) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(pazienteService.addNewPaziente(jwtAuthentication, paziente));
        } catch (Exception e) {
            res.setOk(false);
            res.setMessage(e.getMessage());
        }

        return res;
    }
}
