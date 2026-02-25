package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.AllegatoDTO;
import com.development.spring.hGate.H_Gate.mappers.AllegatoMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.AllegatoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("allegati")
public class AllegatoController {

    private final AllegatoService allegatoService;
    private final AllegatoMapper allegatoMapper;

    @PostMapping("/prenotazione/{prenotazioneId}")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<List<AllegatoDTO>> upload(
            @PathVariable("prenotazioneId") Integer prenotazioneId,
            @RequestParam("files")List<MultipartFile> files) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(allegatoMapper.convertModelsToDtos(allegatoService.uploadAllegati(prenotazioneId, files)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @GetMapping("/prenotazione/{prenotazioneId}")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN')")
    public ResponseDTO<List<AllegatoDTO>> getByPrenotazione(@PathVariable("prenotazioneId") Integer prenotazioneId) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(allegatoMapper.convertModelsToDtos(allegatoService.getByPrenotazione(prenotazioneId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN')")
    public ResponseEntity<Resource> download(@PathVariable Integer id) {
        Resource resource = allegatoService.scaricaAllegato(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/paziente/{pazienteId}")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN')")
    public ResponseDTO<List<AllegatoDTO>> findByPaziente(@PathVariable("pazienteId") Integer pazienteId, JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(allegatoMapper.convertModelsToDtos(allegatoService.getByPaziente(pazienteId,jwtAuthentication)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

}
