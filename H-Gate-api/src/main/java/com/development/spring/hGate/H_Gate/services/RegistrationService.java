package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.UserRegistrationDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.enums.GruppoSanguignoEnum;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
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
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);


    @Transactional
    public Users registerUser(UserRegistrationDTO dto) {
        validateRegistration(dto);

        Users user = createBaseUser(dto);

        user = userRepository.save(user);

        if("PAZIENTE".equalsIgnoreCase(dto.getRuolo())) {
            createPaziente(dto, user);
        } else if("MEDICO".equalsIgnoreCase(dto.getRuolo())) {
            createMedico(dto, user);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RUOLO NON VALIDO: " + dto.getRuolo());
        }

        logger.info("User registered successfully with email: {}", dto.getEmail());
        return  user;
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

    private void createPaziente(UserRegistrationDTO dto, Users user) {
        Paziente paziente = Paziente.builder()
                .codiceFiscale(dto.getCodiceFiscale())
                .gruppoSanguigno(dto.getGruppoSanguigno())
                .altezzaCm(parseInteger(dto.getAltezzaCm()))
                .allergie(dto.getAllergie())
                .noteMediche(dto.getPatologieCroniche())
                .build();

        pazienteRepository.save(paziente);
    }

    private GruppoSanguignoEnum parseGruppoSanguigno(String gruppoSanguigno) {
        if (gruppoSanguigno == null || gruppoSanguigno.isEmpty()) {
            return null;
        }
        try {
            // Gestisce il caso "0+" -> "ZERO_POSITIVE"
            String normalized = gruppoSanguigno.replace("0", "ZERO")
                    .replace("+", "_POSITIVE")
                    .replace("-", "_NEGATIVE");
            return GruppoSanguignoEnum.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid blood type: {}", gruppoSanguigno);
            return null;
        }
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
        if(userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email già in uso");
        }

        if(dto.getConfirmPassword() == null || dto.getConfirmPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La password è obbligatoria");
        }

        if(dto.getConfirmPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La password deve contenere almeno 8 caratteri");
        }

        if("PAZIENTE".equalsIgnoreCase(dto.getRuolo())) {
            validatePazienteData(dto);
        } else if("MEDICO".equalsIgnoreCase(dto.getRuolo())) {
            validateMedicoData(dto);
        }
    }

    private void validatePazienteData(UserRegistrationDTO dto) {
        if(dto.getCodiceFiscale() == null || dto.getCodiceFiscale().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale obbligatorio per i pazienti");
        }

        if(dto.getCodiceFiscale().length() != 16) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale non valido");
        }

        if(pazienteRepository.existsByCodiceFiscale(dto.getCodiceFiscale())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale già in uso");
        }
    }

    private void validateMedicoData(UserRegistrationDTO dto) {
        if(dto.getNumeroAlbo() == null || dto.getNumeroAlbo().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numero albo obbligatorio per i medici");
        }

        if(dto.getSpecializzazione() == null || dto.getSpecializzazione().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Specializzazione obbligatoria");
        }

        if(medicoRepository.existsByNumeroAlbo(dto.getNumeroAlbo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Numero albo già in uso");
        }
    }

    public boolean isEmailAvailable(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean isCodiceFiscaleAvailable(String codiceFiscale) {
        return !pazienteRepository.existsByCodiceFiscale(codiceFiscale);
    }

    public boolean isNumeroAlboAvailable(String numeroAlbo) {
        return !medicoRepository.existsByNumeroAlbo(numeroAlbo);
    }

}
