package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsiTerapeuticiDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoUpdateDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PercorsiTerapeuticiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("percorsi-terapeutici")
@Tag(name = "Percorsi Terapeutici", description = "Gestione dei piani di cura e dei percorsi clinici assegnati ai pazienti")
public class PercorsiTerapeuticiController {

    private final PercorsiTerapeuticiService percorsiTerapeuticiService;

    @Operation(summary = "Percorsi medico per paziente", description = "Recupera i percorsi terapeutici creati dal medico autenticato per uno specifico paziente.")
    @GetMapping("/paziente/{pazienteId}")
    public ResponseDTO<List<PercorsiTerapeuticiDTO>> percorsiTerapeuticiMedicoAndPaziente(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID del paziente") @PathVariable("pazienteId") Integer pazienteId) {
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

    @Operation(summary = "Tutti i percorsi del paziente", description = "Recupera la lista completa dei percorsi terapeutici di un paziente (anche di medici diversi).")
    @GetMapping("/{pazienteId}/paziente")
    public ResponseDTO<List<PercorsiTerapeuticiDTO>> getByPaziente(
            @Parameter(description = "ID del paziente") @PathVariable("pazienteId") Integer pazienteId) {
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

    @Operation(summary = "Dettaglio percorso", description = "Recupera le informazioni dettagliate di un singolo percorso terapeutico tramite il suo ID.")
    @GetMapping("/{id}")
    public ResponseDTO<PercorsiTerapeuticiDTO> getById(
            @Parameter(description = "ID del percorso terapeutico") @PathVariable("id") Integer id) {
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

    @Operation(summary = "Crea nuovo percorso", description = "Permette a un medico di creare un nuovo piano terapeutico per un paziente.")
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PercorsiTerapeuticiDTO> create(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
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

    @Operation(summary = "Aggiorna percorso", description = "Modifica un percorso terapeutico esistente (riservato ai medici).")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PercorsiTerapeuticiDTO> update(
            @Parameter(description = "ID del percorso da aggiornare") @PathVariable Integer id,
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

    @Operation(summary = "Elimina percorso", description = "Rimuove un percorso terapeutico dal sistema.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public void delete(@Parameter(description = "ID del percorso da eliminare") @PathVariable Integer id) {
        percorsiTerapeuticiService.delete(id);
    }
}