package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.AllegatoDTO;
import com.development.spring.hGate.H_Gate.mappers.AllegatoMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.AllegatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import com.development.spring.hGate.H_Gate.entity.Allegato;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("allegati")
@Tag(name = "Allegati", description = "Endpoint per la gestione, l'upload e il download dei documenti legati alle prenotazioni")
public class AllegatoController {

    private final AllegatoService allegatoService;
    private final AllegatoMapper allegatoMapper;

    @Operation(
            summary = "Carica allegati per una prenotazione",
            description = "Permette a un TUTORE di caricare uno o più file associandoli a una specifica prenotazione."
    )
    @PostMapping(value = "/prenotazione/{prenotazioneId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('TUTORE') and @access.prenotazioneDelTutore(authentication, #prenotazioneId)")
    public ResponseDTO<List<AllegatoDTO>> upload(
            @Parameter(description = "ID della prenotazione a cui associare i file")
            @PathVariable("prenotazioneId") Integer prenotazioneId,
            @Parameter(description = "Lista dei file da caricare")
            @RequestParam("files") List<MultipartFile> files) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(allegatoMapper.convertModelsToDtos(allegatoService.uploadAllegati(prenotazioneId, files)));
        return res;
    }

    @Operation(summary = "Ottieni allegati per prenotazione", description = "Recupera la lista dei metadati degli allegati per una data prenotazione.")
    @GetMapping("/prenotazione/{prenotazioneId}")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN') and @access.prenotazione(authentication, #prenotazioneId)")
    public ResponseDTO<List<AllegatoDTO>> getByPrenotazione(
            @Parameter(description = "ID della prenotazione") @PathVariable("prenotazioneId") Integer prenotazioneId) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(allegatoMapper.convertModelsToDtos(allegatoService.getByPrenotazione(prenotazioneId)));
        return res;
    }

    @Operation(summary = "Download file", description = "Scarica il file fisico dell'allegato tramite il suo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File scaricato con successo",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "404", description = "Allegato non trovato", content = @Content)
    })
    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN') and @access.allegato(authentication, #id)")
    public ResponseEntity<Resource> download(
            @Parameter(description = "ID univoco dell'allegato") @PathVariable Integer id) {
        Allegato allegato = allegatoService.getAllegato(id);
        Resource resource = allegatoService.scaricaAllegato(allegato);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(allegato.getMimeType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        // ContentDisposition gestisce correttamente caratteri speciali e accentati nel nome (RFC 6266)
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(allegato.getNomeFile(), StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }

    @Operation(summary = "Ottieni allegati per paziente", description = "Recupera tutti gli allegati associati a un paziente specifico.")
    @GetMapping("/paziente/{pazienteId}")
    @PreAuthorize("hasAnyAuthority('TUTORE', 'MEDICO', 'ADMIN') and @access.paziente(authentication, #pazienteId)")
    public ResponseDTO<List<AllegatoDTO>> findByPaziente(
            @Parameter(description = "ID del paziente") @PathVariable("pazienteId") Integer pazienteId,
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<AllegatoDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(allegatoMapper.convertModelsToDtos(allegatoService.getByPaziente(pazienteId, jwtAuthentication)));
        return res;
    }
}