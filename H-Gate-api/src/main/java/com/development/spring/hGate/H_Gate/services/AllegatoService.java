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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AllegatoService extends BasicService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final AllegatoRepository allegatoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final MedicoRepository medicoRepository;

    /** Tipi ammessi: MIME dichiarato -> estensione e codice tipo_file salvati. */
    private static final Map<String, String[]> ALLOWED_TYPES = Map.of(
            "application/pdf", new String[]{"pdf", "PDF"},
            "image/jpeg", new String[]{"jpg", "JPEG"},
            "image/png", new String[]{"png", "PNG"}
    );
    private static final long MAX_SIZE = 10_485_760;
    private static final int MAX_FILES_PER_UPLOAD = 10;

    @Transactional
    public List<Allegato> uploadAllegati(Integer prenotazioneId, List<MultipartFile> files) {
        Prenotazione prenotazione = prenotazioneRepository.findById(prenotazioneId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun file selezionato");
        }
        if (files.size() > MAX_FILES_PER_UPLOAD) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Puoi caricare al massimo " + MAX_FILES_PER_UPLOAD + " file alla volta");
        }

        // Validazione di tutti i file prima di scrivere qualsiasi cosa su disco
        for (MultipartFile file : files) {
            validaFile(file);
        }

        Path dir = Paths.get(uploadDir, String.valueOf(prenotazioneId)).toAbsolutePath().normalize();
        List<Path> scritti = new ArrayList<>();
        List<Allegato> salvati = new ArrayList<>();

        try {
            Files.createDirectories(dir);
            for (MultipartFile file : files) {
                String[] tipo = ALLOWED_TYPES.get(file.getContentType());
                // Il nome su disco è generato dal server: il nome originale non entra mai nel percorso
                String nomeFile = UUID.randomUUID() + "." + tipo[0];
                Path filePath = dir.resolve(nomeFile);

                try (InputStream in = file.getInputStream()) {
                    Files.copy(in, filePath);
                }
                scritti.add(filePath);

                Allegato allegato = Allegato.builder()
                        .uuid(UUID.randomUUID().toString())
                        .nomeFile(nomeOriginaleSicuro(file.getOriginalFilename(), tipo[0]))
                        .tipoFile(tipo[1])
                        .mimeType(file.getContentType())
                        .sizeBytes(file.getSize())
                        .url("/allegati/" + prenotazioneId + "/" + nomeFile)
                        .storagePath(filePath.toString())
                        .prenotazione(prenotazione)
                        .build();

                salvati.add(allegatoRepository.save(allegato));
            }
        } catch (IOException | RuntimeException e) {
            // Nessun file orfano su disco se l'operazione fallisce a metà (il DB fa rollback)
            for (Path p : scritti) {
                try { Files.deleteIfExists(p); } catch (IOException ignored) { }
            }
            if (e instanceof ResponseStatusException rse) throw rse;
            logger.error("Errore nel salvataggio degli allegati per la prenotazione {}", prenotazioneId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel salvataggio dei file");
        }

        return salvati;
    }

    private void validaFile(MultipartFile file) {
        String nome = file.getOriginalFilename();
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File vuoto: " + nome);
        }
        if (file.getSize() > MAX_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File troppo grande (max 10 MB): " + nome);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.containsKey(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo file non supportato (ammessi PDF, JPG, PNG): " + nome);
        }
        // Il Content-Type è dichiarato dal client: si verifica che il contenuto corrisponda davvero
        if (!contenutoCorrisponde(file, contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il contenuto del file non corrisponde al tipo dichiarato: " + nome);
        }
    }

    /** Controllo delle "magic bytes" iniziali del file. */
    static boolean contenutoCorrisponde(MultipartFile file, String contentType) {
        byte[] head = new byte[8];
        int letti;
        try (InputStream in = file.getInputStream()) {
            letti = in.readNBytes(head, 0, head.length);
        } catch (IOException e) {
            return false;
        }
        return switch (contentType) {
            case "application/pdf" -> letti >= 4 && head[0] == '%' && head[1] == 'P' && head[2] == 'D' && head[3] == 'F';
            case "image/jpeg" -> letti >= 3 && (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF;
            case "image/png" -> letti >= 8 && (head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G'
                    && head[4] == 0x0D && head[5] == 0x0A && head[6] == 0x1A && head[7] == 0x0A;
            default -> false;
        };
    }

    /** Nome da mostrare all'utente: niente percorsi né caratteri di controllo. */
    static String nomeOriginaleSicuro(String originale, String estensione) {
        String nome = originale == null ? "" : originale;
        nome = nome.replace('\\', '/');
        nome = nome.substring(nome.lastIndexOf('/') + 1);
        nome = nome.replaceAll("[\\p{Cntrl}\"<>|:*?]", "_").trim();
        if (nome.isEmpty() || nome.equals(".") || nome.equals("..")) {
            nome = "allegato." + estensione;
        }
        return nome.length() > 200 ? nome.substring(nome.length() - 200) : nome;
    }

    public Allegato getAllegato(Integer allegatoId) {
        return allegatoRepository.findById(allegatoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Allegato non trovato"));
    }

    public Resource scaricaAllegato(Allegato allegato) {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = Paths.get(allegato.getStoragePath()).toAbsolutePath().normalize();
            // Difesa in profondità: si servono solo file dentro la cartella degli upload
            if (!filePath.startsWith(base)) {
                logger.error("Percorso allegato {} fuori dalla cartella upload", allegato.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File non trovato");
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File non trovato");
            return resource;
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore nel download del file");
        }
    }

    public List<Allegato> getByPrenotazione(Integer prenotazioneId) {
        return allegatoRepository.findByPrenotazioneId(prenotazioneId);
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
