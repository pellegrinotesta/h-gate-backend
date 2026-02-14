package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.TutoreLegale;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteTutoreRepository;
import com.development.spring.hGate.H_Gate.repositories.TutoreLegaleRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PazienteService extends BasicService {

    private final PazienteRepository pazienteRepository;
    private final TutoreLegaleRepository tutoreLegaleRepository;
    private final PazienteTutoreRepository pazienteTutoreRepository;

    private static final String PAZIENTE_ID_NOT_FOUND = "Paziente with id %d not found.";
    private static final String TUTORE_ID_NOT_FOUND = "Tutore with id %d not found.";

    public List<Paziente> findByUserId(JwtAuthentication jwtAuthentication) {
        return pazienteRepository.findByUserId(jwtAuthentication.getId());
    }

    @Transactional
    public PazienteDTO addNewPaziente(JwtAuthentication jwtAuthentication, PrenotazioneDTO.PazienteMinDTO createDTO) {
        TutoreLegale tutore = tutoreLegaleRepository.findByUserId(jwtAuthentication.getId())
                .orElseThrow(() -> buildEntityWithIdNotFoundException(
                        jwtAuthentication.getId(),
                        TUTORE_ID_NOT_FOUND
                ));

        Paziente paziente = pazienteRepository.findByCodiceFiscale(createDTO.getCodiceFiscale())
                .orElse(null);

        boolean isNewPaziente = (paziente == null);
        String relazioneDaUsare = createDTO.getRelazione();

        if (isNewPaziente && (relazioneDaUsare == null || relazioneDaUsare.trim().isEmpty())) {
            throw new IllegalArgumentException("La relazione con il minore è obbligatoria");
        }

        if (isNewPaziente) {

            paziente = new Paziente();
            paziente.setNome(createDTO.getNome());
            paziente.setCognome(createDTO.getCognome());
            paziente.setCodiceFiscale(createDTO.getCodiceFiscale());
            paziente.setDataNascita(createDTO.getDataNascita());
            paziente.setSesso(createDTO.getSesso());
            paziente.setConsensoPrivacy(true);
            paziente.setAllergie(createDTO.getAllergie());
            paziente.setPatologieCroniche(createDTO.getPatologieCroniche());
            paziente.setPesoKg(createDTO.getPesoKg());
            paziente.setGruppoSanguigno(createDTO.getGruppoSanguigno());


            paziente = pazienteRepository.save(paziente);
        } else {
            PazienteTutore relazioneEsistente = pazienteTutoreRepository
                    .findFirstByPazienteId(paziente.getId())
                    .orElse(null);

            if (relazioneEsistente != null) {
                relazioneDaUsare = relazioneEsistente.getRelazione();
            } else if (relazioneDaUsare == null || relazioneDaUsare.trim().isEmpty()) {
                throw new IllegalArgumentException("La relazione con il minore è obbligatoria");
            }

            PazienteTutoreId checkId = new PazienteTutoreId();
            checkId.setPazienteId(paziente.getId());
            checkId.setTutoreId(tutore.getId());

            if (pazienteTutoreRepository.existsById(checkId)) {
                PazienteTutore relazioneEsistenteTutore = pazienteTutoreRepository
                        .findById(checkId)
                        .orElseThrow();
                return mapToPazienteDTO(paziente, relazioneEsistenteTutore);
            }
        }

        PazienteTutoreId relationId = new PazienteTutoreId();
        relationId.setPazienteId(paziente.getId());
        relationId.setTutoreId(tutore.getId());

        PazienteTutore relazione = new PazienteTutore();
        relazione.setId(relationId);
        relazione.setPaziente(paziente);
        relazione.setTutore(tutore);
        relazione.setRelazione(relazioneDaUsare);

        relazione = pazienteTutoreRepository.save(relazione);

        return mapToPazienteDTO(paziente, relazione);
    }

    @Transactional
    public Paziente updateInfo(Paziente pazienteUpdate) {
        Paziente existing = pazienteRepository.findById(pazienteUpdate.getId())
                .orElseThrow(() -> buildEntityWithIdNotFoundException(
                        pazienteUpdate.getId(),
                        PAZIENTE_ID_NOT_FOUND
                ));

        existing.setAllergie(pazienteUpdate.getAllergie());
        existing.setAltezzaCm(pazienteUpdate.getAltezzaCm());
        existing.setGruppoSanguigno(pazienteUpdate.getGruppoSanguigno());
        existing.setNoteMediche(pazienteUpdate.getNoteMediche());
        existing.setPatologieCroniche(pazienteUpdate.getPatologieCroniche());
        existing.setPesoKg(pazienteUpdate.getPesoKg());

        return pazienteRepository.save(existing);
    }

    private PazienteDTO mapToPazienteDTO(Paziente p, PazienteTutore rel) {
        return PazienteDTO.builder()
                .id(p.getId())
                .nome(p.getNome())
                .cognome(p.getCognome())
                .codiceFiscale(p.getCodiceFiscale())
                .dataNascita(p.getDataNascita())
                .sesso(p.getSesso())
                .gruppoSanguigno(p.getGruppoSanguigno())
                .relazione(rel != null ? rel.getRelazione() : null)
                // Uncomment quando implementi calculateAge
                // .eta(calculateAge(p.getDataNascita()))
                .build();
    }

    // Metodo helper per calcolare l'età (opzionale)
    /*
    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    */
}