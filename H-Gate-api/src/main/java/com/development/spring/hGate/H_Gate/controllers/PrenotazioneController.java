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
@RequestMapping("prenotazione")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneMapper prenotazioneMapper;
    private final PrenotazioniDettagliateService prenotazioniDettagliateService;


    @PostMapping("/advanced-search")
    @PreAuthorize("hasAuthority('TUTORE')")
    public PaginatedResponseDTO<PrenotazioniDettagliateDTO> advancedSearch(
            @RequestBody(required = false) Optional<Filter<VPrenotazioniDettagliate>> filter,
            @PageableDefault Pageable pageable, JwtAuthentication jwtAuthentication) {
        PageDTO<PrenotazioniDettagliateDTO> pageDTO = prenotazioniDettagliateService.searchAdvanced(filter, pageable, jwtAuthentication.getId());
        PaginatedResponseData<PrenotazioniDettagliateDTO> data =
                PaginatedResponseData.fromPageDTO(pageDTO);

        return PaginatedResponseDTO.success(data);
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<PrenotazioneDTO> creaPrenotazione(JwtAuthentication jwtAuthentication, @RequestBody PrenotazioneCreateDTO dto) {
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

    @PutMapping("/{id}/annulla")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<String> annullaPrenotazione(JwtAuthentication jwtAuthentication, @PathVariable Integer id, @Valid @RequestBody PrenotazioneAnnullaDTO dto) {

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

    @GetMapping("/disponibilita/{medicoId}")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<SlotDisponibiliDTO> verificaDisponibilita(@PathVariable Integer medicoId, @RequestParam String data) {
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

    @PutMapping("/{prenotazioneId}/conferma")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PrenotazioneDTO> confermaPrenotazione(JwtAuthentication jwtAuthentication, @PathVariable("prenotazioneId") Integer prenotazioneId) {
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


    @PutMapping("/{prenotazioneId}/completa")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<PrenotazioneDTO> completaPrenotazione(JwtAuthentication jwtAuthentication, @PathVariable("prenotazioneId") Integer prenotazioneId) {
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE')")
    public ResponseDTO<PrenotazioneDTO> getById(@PathVariable("id") Integer id) {
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
