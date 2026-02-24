package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsiTerapeuticiDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoUpdateDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PercorsiTerapeuticiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("percorsi-terapeutici")
public class PercorsiTerapeuticiController {

    private final PercorsiTerapeuticiService percorsiTerapeuticiService;

    @GetMapping("/paziente/{pazienteId}")
    public ResponseDTO<List<PercorsiTerapeuticiDTO>> percorsiTerapeuticiMedicoAndPaziente(JwtAuthentication jwtAuthentication, @PathVariable("pazienteId") Integer pazienteId) {
        ResponseDTO<List<PercorsiTerapeuticiDTO>> res = new ResponseDTO<>();
        try {

            res.setOk(true);
            res.setData(percorsiTerapeuticiService.percorsiTerapeuticiMedicoAndPaziente(jwtAuthentication.getId(), pazienteId));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @GetMapping("/{pazienteId}/paziente")
    public ResponseDTO<List<PercorsiTerapeuticiDTO>> getByPaziente(@PathVariable("pazienteId") Integer pazienteId) {
        ResponseDTO<List<PercorsiTerapeuticiDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(percorsiTerapeuticiService.getByPaziente(pazienteId));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @GetMapping("/{id}")
    public ResponseDTO<PercorsiTerapeuticiDTO> getById(@PathVariable("id") Integer id) {
        ResponseDTO<PercorsiTerapeuticiDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(percorsiTerapeuticiService.getById(id));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PercorsiTerapeuticiDTO> create( JwtAuthentication jwtAuthentication,
                                                       @RequestBody PercorsoCreateDTO dto) {
        ResponseDTO<PercorsiTerapeuticiDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(percorsiTerapeuticiService.create(jwtAuthentication.getId(), dto));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PercorsiTerapeuticiDTO> update(
            @PathVariable Integer id,
            @RequestBody PercorsoUpdateDTO dto) {
        ResponseDTO<PercorsiTerapeuticiDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(percorsiTerapeuticiService.update(id, dto));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public void delete(@PathVariable Integer id) {
        percorsiTerapeuticiService.delete(id);
    }
}
