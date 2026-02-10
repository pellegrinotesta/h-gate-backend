package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneAnnullaDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.SlotDisponibiliDTO;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.mappers.PrenotazioneMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.PrenotazioneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("prenotazione")
public class PrenotazioneController {

    private final PrenotazioneService prenotazioneService;
    private final PrenotazioneMapper prenotazioneMapper;

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

    @DeleteMapping("/{id}")
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

}
