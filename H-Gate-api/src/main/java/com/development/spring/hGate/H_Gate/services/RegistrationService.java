package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.UserRegistrationDTO;
import com.development.spring.hGate.H_Gate.entity.*;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.repositories.*;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationService extends BasicService {

    private final UserRepository userRepository;
    private final PazienteRepository pazienteRepository;
    private final TutoreLegaleRepository tutoreLegaleRepository;
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final PazienteTutoreRepository pazienteTutoreRepository;

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Transactional
    public Users registerUser(UserRegistrationDTO dto) {
        validateRegistration(dto);

        // 1. Crea e salva l'utente base
        Users user = createBaseUser(dto);
        user = userRepository.save(user);

        // 2. Crea l'entità specifica in base al ruolo
        if("TUTORE".equalsIgnoreCase(dto.getRuolo())) {
            TutoreLegale tutore = createTutore(user);
            createPazienteAndRelation(dto, tutore);
        } else if("MEDICO".equalsIgnoreCase(dto.getRuolo())) {
            createMedico(dto, user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruolo non valido: " + dto.getRuolo());
        }

        logger.info("User registered successfully with email: {}", dto.getEmail());
        return user;
    }

    private TutoreLegale createTutore(Users user) {
        TutoreLegale tutore = TutoreLegale.builder()
                .user(user)
                .build();

        return tutoreLegaleRepository.save(tutore);
    }

    private void createPazienteAndRelation(UserRegistrationDTO dto, TutoreLegale tutore) {
        if (dto.getMinore() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dati del minore obbligatori per i tutori");
        }

        // Verifica se il paziente esiste già
        Paziente paziente = pazienteRepository.findByCodiceFiscale(dto.getMinore().getCodiceFiscale())
                .orElse(null);

        boolean isNewPaziente = (paziente == null);
        String relazioneDaUsare = dto.getMinore().getRelazione();

        // Validazione relazione
        if (relazioneDaUsare == null || relazioneDaUsare.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La relazione con il minore è obbligatoria");
        }

        if (isNewPaziente) {
            // Crea nuovo paziente
            paziente = Paziente.builder()
                    .nome(dto.getMinore().getNome())
                    .cognome(dto.getMinore().getCognome())
                    .codiceFiscale(dto.getMinore().getCodiceFiscale())
                    .dataNascita(dto.getMinore().getDataNascita())
                    .sesso(dto.getMinore().getSesso())
                    .consensoPrivacy(true)
                    .allergie(dto.getMinore().getAllergie())
                    .patologieCroniche(dto.getMinore().getPatologieCroniche())
                    .pesoKg(dto.getMinore().getPesoKg())
                    .altezzaCm(dto.getMinore().getAltezzaCm())
                    .gruppoSanguigno(dto.getMinore().getGruppoSanguigno())
                    .noteMediche(dto.getMinore().getNoteMediche())
                    .build();

            paziente = pazienteRepository.save(paziente);
        } else {
            // Paziente esistente - verifica se questo tutore ha già una relazione
            PazienteTutoreId checkId = new PazienteTutoreId();
            checkId.setPazienteId(paziente.getId());
            checkId.setTutoreId(tutore.getId());

            if (pazienteTutoreRepository.existsById(checkId)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Questo tutore ha già una relazione con il minore"
                );
            }

            // Recupera la relazione esistente per mantenere coerenza
            PazienteTutore relazioneEsistente = pazienteTutoreRepository
                    .findFirstByPazienteId(paziente.getId())
                    .orElse(null);

            if (relazioneEsistente != null) {
                relazioneDaUsare = relazioneEsistente.getRelazione();
            }
        }

        // Crea la relazione paziente-tutore
        PazienteTutoreId relationId = new PazienteTutoreId();
        relationId.setPazienteId(paziente.getId());
        relationId.setTutoreId(tutore.getId());

        PazienteTutore relazione = PazienteTutore.builder()
                .id(relationId)
                .paziente(paziente)
                .tutore(tutore)
                .relazione(relazioneDaUsare)
                .build();

        pazienteTutoreRepository.save(relazione);
    }

    private void createMedico(UserRegistrationDTO dto, Users user) {
        Medico medico = Medico.builder()
                .user(user)
                .specializzazione(dto.getSpecializzazione())
                .numeroAlbo(dto.getNumeroAlbo())
                .universita(dto.getUniversita())
                .annoLaurea(parseInteger(dto.getAnnoLaurea()))
                .durataVisitaMinuti(parseInteger(dto.getDurataVisitaMinuti()))
                .isDisponibile(true)
                .build();

        medicoRepository.save(medico);
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse integer: {}", value);
            return null;
        }
    }

    private Users createBaseUser(UserRegistrationDTO dto) {
        Set<Role> roles = new HashSet<>();
        try {
            roles.add(Role.valueOf(dto.getRuolo().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ruolo non valido: " + dto.getRuolo());
        }

        return Users.builder()
                .nome(dto.getNome())
                .cognome(dto.getCognome())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getConfirmPassword()))
                .telefono(dto.getTelefono())
                .dataNascita(dto.getDataNascita())
                .indirizzo(dto.getIndirizzo())
                .citta(dto.getCitta())
                .provincia(dto.getProvincia())
                .cap(dto.getCap())
                .roles(roles)
                .createdAt(new Date())
                .build();
    }

    private void validateRegistration(UserRegistrationDTO dto) {
        // Validazione email
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email già in uso");
        }

        // Validazione password
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La password è obbligatoria");
        }

        if (dto.getConfirmPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La password deve contenere almeno 8 caratteri");
        }

        // Validazione specifica per ruolo
        if ("TUTORE".equalsIgnoreCase(dto.getRuolo())) {
            validateTutoreData(dto);
        } else if ("MEDICO".equalsIgnoreCase(dto.getRuolo())) {
            validateMedicoData(dto);
        }
    }

    private void validateTutoreData(UserRegistrationDTO dto) {
        if (dto.getMinore() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dati del minore obbligatori");
        }

        if (dto.getMinore().getCodiceFiscale() == null || dto.getMinore().getCodiceFiscale().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale del minore obbligatorio");
        }

        if (dto.getMinore().getCodiceFiscale().length() != 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale non valido");
        }

        if (dto.getMinore().getRelazione() == null || dto.getMinore().getRelazione().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La relazione con il minore è obbligatoria");
        }
    }

    private void validateMedicoData(UserRegistrationDTO dto) {
        if (dto.getNumeroAlbo() == null || dto.getNumeroAlbo().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numero albo obbligatorio per i medici");
        }

        if (dto.getSpecializzazione() == null || dto.getSpecializzazione().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specializzazione obbligatoria");
        }

        if (medicoRepository.existsByNumeroAlbo(dto.getNumeroAlbo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numero albo già in uso");
        }
    }
}