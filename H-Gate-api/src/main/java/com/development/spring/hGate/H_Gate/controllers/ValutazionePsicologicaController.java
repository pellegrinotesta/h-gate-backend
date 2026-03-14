package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazionePsicologicaDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneUpdateDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.ValutazionePsicologicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("valutazioni")
@Tag(name = "Valutazioni Psicologiche", description = "Gestione delle valutazioni psicologiche e dei test clinici somministrati ai pazienti")
public class ValutazionePsicologicaController {

    private final ValutazionePsicologicaService valutazioneService;

    @Operation(summary = "Valutazioni medico per paziente", description = "Recupera la lista delle valutazioni effettuate dal medico autenticato per uno specifico paziente.")
    @GetMapping("/paziente/{pazienteId}/medico")
    public ResponseDTO<List<ValutazionePsicologicaDTO>> valutazionePsicologicaMedicoAndPaziente(
            @Parameter(description = "ID del paziente") @PathVariable("pazienteId") Integer pazienteId,
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<ValutazionePsicologicaDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(valutazioneService.valutazionePsicologicaMedicoAndPaziente(pazienteId, jwtAuthentication.getId()));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Storico valutazioni paziente", description = "Recupera tutte le valutazioni psicologiche di un paziente, visibile a medici, tutori e amministratori.")
    @GetMapping("/paziente/{pazienteId}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'ADMIN')")
    public ResponseDTO<List<ValutazionePsicologicaDTO>> getByPaziente(
            @Parameter(description = "ID del paziente") @PathVariable Integer pazienteId) {
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

    @Operation(summary = "Dettaglio valutazione", description = "Recupera i dati completi di una specifica valutazione tramite il suo ID.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'AMMINISTRATORE')")
    public ResponseDTO<ValutazionePsicologicaDTO> getById(
            @Parameter(description = "ID della valutazione") @PathVariable Integer id) {
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

    @Operation(summary = "Crea nuova valutazione", description = "Permette a un medico di inserire una nuova valutazione psicologica per un paziente.")
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<ValutazionePsicologicaDTO> create(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
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

    @Operation(summary = "Aggiorna valutazione", description = "Consente al medico di modificare una valutazione psicologica esistente.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<ValutazionePsicologicaDTO> update(
            @Parameter(description = "ID della valutazione") @PathVariable Integer id,
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

    @Operation(summary = "Elimina valutazione", description = "Rimuove definitivamente una valutazione dal sistema (riservato ai medici).")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public void delete(@Parameter(description = "ID della valutazione da eliminare") @PathVariable Integer id) {
        valutazioneService.delete(id);
    }
}