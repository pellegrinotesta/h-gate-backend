package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioniDettagliateDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.PazienteMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PazienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("paziente")
public class PazienteController {

    private final PazienteService pazienteService;
    private final PazienteMapper pazienteMapper;

    @PostMapping("/advanced-search")
    @PreAuthorize("hasAuthority('MEDICO')")
    public PaginatedResponseDTO<PazienteDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<Paziente>> filter,
            @PageableDefault Pageable pageable, JwtAuthentication jwtAuthentication) {
        PageDTO<PazienteDTO> pageDTO = pazienteService.searchAdvanced(filter, pageable, jwtAuthentication.getId());
        PaginatedResponseData<PazienteDTO> data =
                PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }
    
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
