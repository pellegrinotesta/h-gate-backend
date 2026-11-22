package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.PaginatedResponseData;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.*;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.PrenotazioneMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PrenotazioneService;
import com.development.spring.hGate.H_Gate.services.PrenotazioniDettagliateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("prenotazione")
@Tag(name = "Prenotazioni", description = "Gestione del ciclo di vita delle prenotazioni (creazione, conferma, annullamento e ricerca)")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneMapper prenotazioneMapper;
    private final PrenotazioniDettagliateService prenotazioniDettagliateService;

    @Operation(summary = "Ricerca avanzata prenotazioni", description = "Permette al tutore di filtrare le proprie prenotazioni con dettagli estesi e paginazione.")
    @PostMapping("/advanced-search")
    @PreAuthorize("hasAuthority('TUTORE')")
    public PaginatedResponseDTO<PrenotazioniDettagliateDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<VPrenotazioniDettagliate>> filter,
            @ParameterObject Pageable pageable,
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        PageDTO<PrenotazioniDettagliateDTO> pageDTO = prenotazioniDettagliateService.searchAdvanced(filter, pageable, jwtAuthentication.getId());
        PaginatedResponseData<PrenotazioniDettagliateDTO> data = PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

    @Operation(summary = "Crea una nuova prenotazione", description = "Il tutore prenota uno slot temporale per un paziente presso un medico.")
    @PostMapping()
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<PrenotazioneDTO> creaPrenotazione(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @RequestBody PrenotazioneCreateDTO dto) {
        ResponseDTO<PrenotazioneDTO> res = new ResponseDTO<>();
        try {
            Prenotazione prenotazione = prenotazioneService.creaPrenotazione(jwtAuthentication.getId(), dto);
            res.setData(prenotazioneMapper.convertModelToDTO(prenotazione));
            res.setOk(true);
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Annulla prenotazione", description = "Permette al tutore di annullare una prenotazione esistente specificando il motivo.")
    @PutMapping("/{id}/annulla")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO')")
    public ResponseDTO<String> annullaPrenotazione(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della prenotazione") @PathVariable Integer id,
            @Valid @RequestBody PrenotazioneAnnullaDTO dto) {
        ResponseDTO<String> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneService.annullaPrenotazione(jwtAuthentication.getId(), id, dto.getMotivo()));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Verifica disponibilità", description = "Restituisce gli slot orari disponibili per un medico in una determinata data.")
    @GetMapping("/disponibilita/{medicoId}")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<SlotDisponibiliDTO> verificaDisponibilita(
            @Parameter(description = "ID del medico") @PathVariable Integer medicoId,
            @Parameter(description = "Data in formato YYYY-MM-DD", example = "2024-05-20") @RequestParam String data) {
        ResponseDTO<SlotDisponibiliDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneService.getSlotDisponibili(medicoId, data));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Conferma prenotazione", description = "Azione riservata al medico per confermare una richiesta di prenotazione.")
    @PutMapping("/{prenotazioneId}/conferma")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PrenotazioneDTO> confermaPrenotazione(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della prenotazione") @PathVariable("prenotazioneId") Integer prenotazioneId) {
        ResponseDTO<PrenotazioneDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneMapper.convertModelToDTO(prenotazioneService.confermaPrenotazione(jwtAuthentication.getId(), prenotazioneId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Completa prenotazione", description = "Il medico marca la prenotazione come completata dopo la visita.")
    @PutMapping("/{prenotazioneId}/completa")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PrenotazioneDTO> completaPrenotazione(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @Parameter(description = "ID della prenotazione") @PathVariable("prenotazioneId") Integer prenotazioneId) {
        ResponseDTO<PrenotazioneDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneMapper.convertModelToDTO(prenotazioneService.completaPrenotazione(jwtAuthentication.getId(), prenotazioneId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Dettaglio prenotazione", description = "Recupera i dati di una specifica prenotazione.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE')")
    public ResponseDTO<PrenotazioneDTO> getById(@Parameter(description = "ID della prenotazione") @PathVariable("id") Integer id) {
        ResponseDTO<PrenotazioneDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneMapper.convertModelToDTO(prenotazioneService.getById(id)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @Operation(summary = "Aggiorna prenotazione", description = "Modifica i dettagli di una prenotazione esistente (azione riservata al medico).")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PrenotazioneDTO> update(@RequestBody PrenotazioneUpdateDTO request) {
        ResponseDTO<PrenotazioneDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(prenotazioneMapper.convertModelToDTO(prenotazioneService.update(request)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}