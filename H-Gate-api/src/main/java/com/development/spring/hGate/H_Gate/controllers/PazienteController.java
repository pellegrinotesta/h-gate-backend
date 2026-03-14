package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.PazienteMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PazienteService;
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
@RequestMapping("paziente")
@Tag(name = "Anagrafica Pazienti", description = "Endpoint per la gestione dei pazienti, ricerca avanzata e associazione con i tutori")
public class PazienteController {

    private final PazienteService pazienteService;
    private final PazienteMapper pazienteMapper;

    @Operation(summary = "Ottieni paziente per ID", description = "Recupera i dati anagrafici di un singolo paziente tramite il suo ID univoco.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE')")
    public ResponseDTO<PazienteDTO> getById(
            @Parameter(description = "ID del paziente") @PathVariable("id") Integer id) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(pazienteMapper.convertModelToDTO(pazienteService.getById(id)));
        } catch (Exception e) {
            res.setOk(false);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    @Operation(summary = "Ricerca avanzata pazienti", description = "Permette ai medici di cercare pazienti filtrandoli per criteri specifici con supporto alla paginazione.")
    @PostMapping("/advanced-search")
    @PreAuthorize("hasAuthority('MEDICO')")
    public PaginatedResponseDTO<PazienteDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<Paziente>> filter,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        PageDTO<PazienteDTO> pageDTO = pazienteService.searchAdvanced(filter, pageable, jwtAuthentication.getId());
        PaginatedResponseData<PazienteDTO> data = PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

    @Operation(summary = "Lista pazienti per utente", description = "Recupera la lista di pazienti associati all'utente (tutore) attualmente autenticato.")
    @GetMapping("/user-id")
    public ResponseDTO<List<PazienteDTO>> findByUserId(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
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

    @Operation(summary = "Aggiorna informazioni paziente", description = "Aggiorna i dati anagrafici di un paziente esistente.")
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

    @Operation(summary = "Aggiungi nuovo paziente", description = "Crea un nuovo profilo paziente associandolo automaticamente al tutore che effettua l'operazione.")
    @PostMapping("/add")
    public ResponseDTO<PazienteDTO> addNewPaziente(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @RequestBody PrenotazioneDTO.PazienteMinDTO paziente) {
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