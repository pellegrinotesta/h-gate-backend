package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoUpdateDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.RefertoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("referto")
public class RefertoController {

    private final RefertoMapper refertoMapper;
    private final RefertoService refertoService;

    @PostMapping("/advanced-search")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'ADMIN')")
    public PaginatedResponseDTO<RefertoDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<Referto>> filter,
            @PageableDefault Pageable pageable, JwtAuthentication jwtAuthentication) {
        PageDTO<RefertoDTO> pageDTO = refertoService.searchAdvanced(filter, pageable, jwtAuthentication.getId());
        PaginatedResponseData<RefertoDTO> data =
                PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

    @GetMapping("/prenotazione/{prenotazioneId}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE')")
    public ResponseDTO<RefertoDTO> findByPrenotazione(@PathVariable Integer prenotazioneId) {
        ResponseDTO<RefertoDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(refertoMapper.convertModelToDTO(refertoService.findByPrenotazioneId(prenotazioneId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{pazienteId}/all")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'ADMIN')")
    public ResponseDTO<List<RefertoDTO>> listaRefertiPaziente(@PathVariable("pazienteId") Integer pazienteId) {
        ResponseDTO<List<RefertoDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(refertoMapper.convertModelsToDtos(refertoService.listaRefertiPaziente(pazienteId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{refertoId}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE')")
    public ResponseDTO<RefertoDTO> findById(@PathVariable Integer refertoId) {
        ResponseDTO<RefertoDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(refertoMapper.convertModelToDTO(refertoService.getById(refertoId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<RefertoDTO> create(@RequestBody RefertoCreateDTO dto) {
        ResponseDTO<RefertoDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(refertoMapper.convertModelToDTO(refertoService.create(dto)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PutMapping("/{refertoId}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<RefertoDTO> update(@PathVariable Integer refertoId,
                                          @RequestBody RefertoUpdateDTO dto) {
        ResponseDTO<RefertoDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(refertoMapper.convertModelToDTO(refertoService.update(refertoId, dto)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @DeleteMapping("/{refertoId}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<Void> delete(@PathVariable Integer refertoId) {
        ResponseDTO<Void> res = new ResponseDTO<>();
        try {
            refertoService.delete(refertoId);
            res.setOk(true);
            res.setMessage("Referto eliminato con successo");
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}