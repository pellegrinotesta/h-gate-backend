package com.development.spring.hGate.H_Gate.controllers;

import org.springframework.security.access.prepost.PreAuthorize;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.DisponibilitaMediciDTO;
import com.development.spring.hGate.H_Gate.mappers.DisponibilitaMediciIMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DisponibilitaMediciService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("disponibilita/medico")
@Tag(name = "Disponibilità Medici", description = "Gestione delle fasce orarie e dei giorni di disponibilità per i medici")
public class DisponibilitaMedicoController {

    private final DisponibilitaMediciService disponibilitaMediciService;
    private final DisponibilitaMediciIMapper disponibilitaMediciIMapper;

    @Operation(summary = "Recupera disponibilità per medico", description = "Restituisce la lista delle disponibilità settimanali di un medico specifico tramite il suo ID.")
    @GetMapping("/{medicoId}")
    public ResponseDTO<List<DisponibilitaMediciDTO>> getDisponibilita(
            @Parameter(description = "ID del medico di cui consultare l'agenda") @PathVariable("medicoId") Integer medicoId) {
        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.getDisponibilitaMedico(medicoId)));
        return res;
    }

    @Operation(summary = "Salva o aggiorna disponibilità", description = "Permette al medico autenticato di salvare una nuova fascia di disponibilità.")
    @PostMapping
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<DisponibilitaMediciDTO> saveDisponibilita(
            @RequestBody DisponibilitaMediciDTO dto,
            @Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<DisponibilitaMediciDTO> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciIMapper.convertModelToDTO(disponibilitaMediciService.salvaDisponibilita(authentication.getId(), dto)));
        return res;
    }

    @Operation(summary = "Disabilita un giorno", description = "Imposta come non disponibile un intero giorno della settimana (1=Lunedì, 7=Domenica).")
    @PutMapping("/{giornoSettimana}/disabilita")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<String> disabilitaGiorno(
            @Parameter(description = "Giorno della settimana: 0=Domenica, 1=Lunedì, ..., 6=Sabato") @PathVariable Integer giornoSettimana,
            @Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciService.disabilitaGiorno(authentication.getId(), giornoSettimana));
        return res;
    }

    @Operation(summary = "Abilita un giorno", description = "Ripristina la disponibilità per un giorno della settimana precedentemente disabilitato.")
    @PutMapping("/{giornoSettimana}/abilitaGiorno")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<String> abilitaGiorno(
            @Parameter(description = "Giorno della settimana: 0=Domenica, 1=Lunedì, ..., 6=Sabato") @PathVariable Integer giornoSettimana,
            @Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciService.abilitaGiorno(authentication.getId(), giornoSettimana));
        return res;
    }

    @Operation(summary = "Elimina disponibilità", description = "Rimuove definitivamente la configurazione di disponibilità per un determinato giorno.")
    @DeleteMapping("/{giornoSettimana}")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<String> eliminaDisponibilita(
            @Parameter(description = "Giorno della settimana: 0=Domenica, 1=Lunedì, ..., 6=Sabato") @PathVariable Integer giornoSettimana,
            @Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciService.eliminaDisponibilita(authentication.getId(), giornoSettimana));
        return res;
    }

    @Operation(summary = "Configura disponibilità standard", description = "Applica automaticamente un template di orari standard (es. 09:00-18:00) per il medico autenticato.")
    @PutMapping("/standard")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<List<DisponibilitaMediciDTO>> configuraStandard(
            @Parameter(hidden = true) JwtAuthentication authentication) {
        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.configuraDisponibilitaStandard(authentication.getId())));
        return res;
    }
}