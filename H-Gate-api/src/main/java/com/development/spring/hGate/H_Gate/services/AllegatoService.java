package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Allegato;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.repositories.AllegatoRepository;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllegatoService extends BasicService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final AllegatoRepository allegatoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final MedicoRepository medicoRepository;

    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf", "image/jpeg", "image/png"
    );
    private static final long MAX_SIZE = 10_485_760;

    @Transactional
    public List<Allegato> uploadAllegati(Integer prenotazioneId, List<MultipartFile> files) {
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        List<Allegato> salvati = new ArrayList<>();

        for(MultipartFile file: files) {
            if(!ALLOWED_TYPES.contains(file.getContentType())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Tipo file non supportato: " + file.getOriginalFilename());
            }

            if(file.getSize() > MAX_SIZE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File troppo grande: " + file.getOriginalFilename());
            }

            try {
                Path dir = Paths.get(uploadDir, String.valueOf(prenotazioneId));
                Files.createDirectories(dir);

                String ext = getEstensione(file.getOriginalFilename());
                String nomeFile = UUID.randomUUID() + "." + ext;
                Path filePath = dir.resolve(nomeFile);

                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String tipoFile = switch (file.getContentType()) {
                    case "application/pdf" -> "PDF";
                    case "image/jpeg" -> "JPEG";
                    case "image/png" -> "PNG";
                    default -> "TXT";
                };

                Allegato allegato = Allegato.builder()
                        .uuid(UUID.randomUUID().toString())
                        .nomeFile(file.getOriginalFilename())
                        .tipoFile(tipoFile)
                        .mimeType(file.getContentType())
                        .sizeBytes(file.getSize())
                        .url("/allegati/" + prenotazioneId + "/" + nomeFile)
                        .storagePath(filePath.toString())
                        .prenotazione(prenotazione)
                        .build();

                salvati.add(allegatoRepository.save(allegato));

            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel salvataggio del file: " + file.getOriginalFilename());
            }
        }

        return salvati;
    }

    public Resource scaricaAllegato(Integer allegatoId) {
        Allegato allegato = allegatoRepository.findById(allegatoId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Allegato non trovato"));

        try {
            Path filePath = Paths.get(allegato.getStoragePath());
            Resource resource = new UrlResource(filePath.toUri());
            if(!resource.exists()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File non trovato");
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel download del file");
        }
    }

    public List<Allegato> getByPrenotazione(Integer prenotazioneId) {
        return allegatoRepository.findByPrenotazioneId(prenotazioneId);
    }

    private String getEstensione(String nomeFile) {
        if(nomeFile == null || nomeFile.contains(".")) return "bin";
        return nomeFile.substring(nomeFile.lastIndexOf('.') + 1).toLowerCase();
    }

    public List<Allegato> getByPaziente(Integer pazienteId, JwtAuthentication auth) {
        List<Allegato> allegati;

        if (auth.hasRole(Role.valueOf("MEDICO"))) {
            Medico medico = medicoRepository.findMedicoByUserId(auth.getId());
            // Il medico vede solo gli allegati delle sue prenotazioni
            allegati = allegatoRepository
                    .findByPrenotazionePazienteIdAndPrenotazioneMedicoId(pazienteId, medico.getId());
        } else {
            // Tutore e admin vedono tutto
            allegati = allegatoRepository.findByPrenotazionePazienteId(pazienteId);
        }

        return allegati;
    }
}
