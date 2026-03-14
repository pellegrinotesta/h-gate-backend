package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.MedicoMapper;
import com.development.spring.hGate.H_Gate.mappers.TariffeMediciMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.MedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("medico")
@Tag(name = "Anagrafica Medici", description = "Endpoint per la ricerca e la gestione delle informazioni dei medici")
public class MedicoController {

    private final MedicoService medicoService;
    private final MedicoMapper medicoMapper;
    private final TariffeMediciMapper tariffeMediciMapper;

    @Operation(
            summary = "Ricerca avanzata medici",
            description = "Esegue una ricerca filtrata e paginata dei medici. Supporta filtri complessi sul modello Medico."
    )
    @PostMapping("/advanced-search")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'ADMIN')")
    public PaginatedResponseDTO<MedicoDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<Medico>> filter,
            @ParameterObject Pageable pageable) {
        PageDTO<MedicoDTO> pageDTO = medicoService.searchAdvanced(filter, pageable);
        PaginatedResponseData<MedicoDTO> data = PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

    @Operation(summary = "Lista completa medici", description = "Ritorna l'elenco di tutti i medici registrati nel sistema senza paginazione.")
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

    @Operation(summary = "Dettaglio medico autenticato", description = "Recupera le informazioni del profilo del medico attualmente loggato tramite il suo User ID.")
    @GetMapping("/user-id")
    public ResponseDTO<MedicoDTO> findMedicoByUserId(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
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

    @Operation(summary = "Aggiorna profilo medico", description = "Aggiorna i dati anagrafici e professionali del medico.")
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

    @Operation(summary = "Tariffe medico", description = "Recupera l'elenco delle prestazioni e delle tariffe associate a un determinato medico.")
    @GetMapping("/tariffe/{id}")
    public ResponseDTO<List<TariffeMediciDTO>> listaTariffe(
            @Parameter(description = "ID del medico") @PathVariable("id") Integer id) {
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