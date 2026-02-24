package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazionePsicologicaDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneUpdateDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.ValutazionePsicologicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("valutazioni")
public class ValutazionePsicologicaController {

    private final ValutazionePsicologicaService valutazioneService;

    @GetMapping("/paziente/{pazienteId}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'ADMIN')")
    public ResponseDTO<List<ValutazionePsicologicaDTO>> getByPaziente(
            @PathVariable Integer pazienteId) {
        ResponseDTO<List<ValutazionePsicologicaDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(valutazioneService.getByPaziente(pazienteId));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'AMMINISTRATORE')")
    public ResponseDTO<ValutazionePsicologicaDTO> getById(@PathVariable Integer id) {
        ResponseDTO<ValutazionePsicologicaDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(valutazioneService.getById(id));
        }catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<ValutazionePsicologicaDTO> create(
            JwtAuthentication jwtAuthentication,
            @RequestBody ValutazioneCreateDTO dto) {
        ResponseDTO<ValutazionePsicologicaDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(valutazioneService.create(jwtAuthentication.getId(), dto));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<ValutazionePsicologicaDTO> update(
            @PathVariable Integer id,
            @RequestBody ValutazioneUpdateDTO dto) {
        ResponseDTO<ValutazionePsicologicaDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(valutazioneService.update(id, dto));
        } catch (Exception e) {
            res.setOk(false);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public void delete(@PathVariable Integer id) {
        valutazioneService.delete(id);
    }
}
