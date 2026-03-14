package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.mappers.TariffeMediciMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.TariffeMedicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("tariffe")
@Tag(name = "Tariffe e Prestazioni", description = "Gestione del listino prezzi e delle prestazioni offerte dai singoli medici")
public class TariffeMediciController {

    private final TariffeMedicoService tariffeMedicoService;
    private final TariffeMediciMapper tariffeMediciMapper;

    @Operation(summary = "Ottieni le mie tariffe", description = "Recupera il listino completo delle prestazioni caricate dal medico attualmente autenticato.")
    @GetMapping("/mie")
    public ResponseDTO<List<TariffeMediciDTO>> getByMedici(
            @Parameter(hidden = true) JwtAuthentication authentication) {
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

    @Operation(summary = "Crea nuova tariffa", description = "Aggiunge una nuova prestazione con relativo prezzo al listino del medico.")
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> create(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @RequestBody TariffeMediciDTO request) {
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

    @Operation(summary = "Aggiorna tariffa", description = "Modifica i dettagli o il prezzo di una prestazione esistente.")
    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> update(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della tariffa") @PathVariable Integer id,
            @RequestBody TariffeMediciDTO dto) {
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

    @Operation(summary = "Elimina tariffa", description = "Rimuove una prestazione dal listino del medico.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> delete(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della tariffa da eliminare") @PathVariable("id") Integer id) {
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

    @Operation(summary = "Attiva/Disattiva prestazione", description = "Cambia lo stato (attivo/non attivo) di una prestazione senza eliminarla.")
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<TariffeMediciDTO> toggleAttiva(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della tariffa") @PathVariable("id") Integer id) {
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