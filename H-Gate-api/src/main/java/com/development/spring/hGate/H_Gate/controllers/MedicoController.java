package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.MedicoMapper;
import com.development.spring.hGate.H_Gate.mappers.TariffeMediciMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("medico")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;
    private final TariffeMediciMapper tariffeMediciMapper;
    @PostMapping("/advanced-search")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'ADMIN')")
    public PaginatedResponseDTO<MedicoDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<Medico>> filter,
            @PageableDefault Pageable pageable) {
        PageDTO<MedicoDTO> pageDTO = medicoService.searchAdvanced(filter, pageable);
        PaginatedResponseData<MedicoDTO> data =
                PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

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

    @GetMapping("/tariffe/{id}")
    public ResponseDTO<List<TariffeMediciDTO>> listaTariffe(@PathVariable("id") Integer id) {
        ResponseDTO<List<TariffeMediciDTO>> res = new ResponseDTO<>();
        try {

            res.setOk(true);
            res.setData(tariffeMediciMapper.convertModelsToDtos(medicoService.listaTariffeMedico(id)));

        } catch (Exception e) {
            res.setOk(false);
            res.setMessage(e.getMessage());
        }

        return res;
     }
}
