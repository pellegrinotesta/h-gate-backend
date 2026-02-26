package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.mappers.TariffeMediciMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.TariffeMedicoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("tariffe")
public class TariffeMediciController {

    private final TariffeMedicoService tariffeMedicoService;
    private final TariffeMediciMapper tariffeMediciMapper;

    @GetMapping("/mie")
    public ResponseDTO<List<TariffeMediciDTO>> getByMedici(JwtAuthentication authentication) {
        ResponseDTO<List<TariffeMediciDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(tariffeMediciMapper.convertModelsToDtos(tariffeMedicoService.getByMedico(authentication.getId())));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> create(JwtAuthentication jwtAuthentication, @RequestBody TariffeMediciDTO request) {
        ResponseDTO<TariffeMediciDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(tariffeMediciMapper.convertModelToDTO(tariffeMedicoService.create(jwtAuthentication.getId(), request)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> update(JwtAuthentication jwtAuthentication, @PathVariable Integer id, @RequestBody TariffeMediciDTO dto) {
        ResponseDTO<TariffeMediciDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(tariffeMediciMapper.convertModelToDTO(tariffeMedicoService.update(id, jwtAuthentication.getId(), dto)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> delete(JwtAuthentication jwtAuthentication,@PathVariable("id") Integer id) {
        ResponseDTO<TariffeMediciDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            tariffeMedicoService.delete(jwtAuthentication.getId(), id);
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> toggleAttiva(JwtAuthentication jwtAuthentication, @PathVariable("id") Integer id) {
        ResponseDTO<TariffeMediciDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(tariffeMediciMapper.convertModelToDTO(tariffeMedicoService.toggleAttiva(jwtAuthentication.getId(), id)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

}
