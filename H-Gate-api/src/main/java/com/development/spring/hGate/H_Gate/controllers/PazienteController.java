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
import com.development.spring.hGate.H_Gate.services.CartellaClinicaPdfService;
import com.development.spring.hGate.H_Gate.audit.AuditContext;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final CartellaClinicaPdfService cartellaClinicaPdfService;

    @Operation(summary = "Ottieni paziente per ID", description = "Recupera i dati anagrafici di un singolo paziente tramite il suo ID univoco.")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'ADMIN') and @access.paziente(authentication, #id)")
    public ResponseDTO<PazienteDTO> getById(
            @Parameter(description = "ID del paziente") @PathVariable("id") Integer id) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(pazienteMapper.convertModelToDTO(pazienteService.getById(id)));
        return res;
    }

    @Operation(summary = "Scarica la cartella clinica in PDF",
            description = "Genera il documento ufficiale (A4) con anagrafica, referti, valutazioni, percorsi e allegati del paziente. "
                    + "L'operazione viene registrata nel registro accessi come DOWNLOAD della cartella clinica.")
    @GetMapping(value = "/{id}/cartella/download", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE', 'ADMIN') and @access.paziente(authentication, #id)")
    public ResponseEntity<byte[]> downloadCartellaClinica(
            @Parameter(description = "ID del paziente") @PathVariable("id") Integer id,
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        // Nel registro accessi l'operazione risulta come esportazione della cartella, non come semplice lettura del paziente
        AuditContext.sovrascriviRisorsa("CARTELLA_CLINICA", id);
        AuditContext.paziente(id);

        CartellaClinicaPdfService.CartellaPdf pdf = cartellaClinicaPdfService.genera(id, jwtAuthentication);
        // Il nome del file è già ridotto a soli caratteri ASCII dal servizio
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(pdf.nomeFile())
                .build();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.noStore())
                .body(pdf.contenuto());
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
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<List<PazienteDTO>> findByUserId(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        ResponseDTO<List<PazienteDTO>> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(pazienteMapper.convertModelsToDtos(pazienteService.findByUserId(jwtAuthentication)));
        return res;
    }

    @Operation(summary = "Aggiorna informazioni paziente", description = "Aggiorna i dati anagrafici di un paziente esistente.")
    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('MEDICO', 'TUTORE') and @access.paziente(authentication, #paziente.id)")
    public ResponseDTO<PazienteDTO> update(@Valid @RequestBody Paziente paziente) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        res.setOk(true);
        res.setData(pazienteMapper.convertModelToDTO(pazienteService.updateInfo(paziente)));
        return res;
    }

    @Operation(summary = "Aggiungi nuovo paziente", description = "Crea un nuovo profilo paziente associandolo automaticamente al tutore che effettua l'operazione.")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<PazienteDTO> addNewPaziente(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication,
            @RequestBody PrenotazioneDTO.PazienteMinDTO paziente) {
        ResponseDTO<PazienteDTO> res = new ResponseDTO<>();
        PazienteDTO creato = pazienteService.addNewPaziente(jwtAuthentication, paziente);
        res.setOk(true);
        res.setData(creato);
        if (creato == null) {
            res.setMessage(PazienteService.MINORE_IN_ATTESA);
        }
        return res;
    }
}