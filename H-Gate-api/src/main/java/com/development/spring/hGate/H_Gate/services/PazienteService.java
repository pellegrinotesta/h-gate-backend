package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.components.PazienteSpecificationsFactory;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.entity.TutoreLegale;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.PazienteMapper;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteTutoreRepository;
import com.development.spring.hGate.H_Gate.repositories.TutoreLegaleRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PazienteService extends BasicService {

    private final PazienteRepository pazienteRepository;
    private final TutoreLegaleRepository tutoreLegaleRepository;
    private final PazienteTutoreRepository pazienteTutoreRepository;
    private final PazienteSpecificationsFactory pazienteSpecificationsFactory;
    private final PazienteMapper pazienteMapper;

    private final Map<String, Function<Paziente, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("codiceFiscale", paziente -> paziente.getCodiceFiscale() != null ? new ComparableWrapper(paziente.getCodiceFiscale()) : null);
        put("sesso", paziente -> paziente.getSesso() != null ? new ComparableWrapper(paziente.getSesso()) : null);
        put("allergie", paziente -> paziente.getAllergie() != null ? new ComparableWrapper(paziente.getAllergie()): null);
        put("patologieCroniche", paziente -> paziente.getPatologieCroniche() != null ? new ComparableWrapper(paziente.getPatologieCroniche()) : null);
    }};

    private static final String PAZIENTE_ID_NOT_FOUND = "Paziente with id %d not found.";
    private static final String TUTORE_ID_NOT_FOUND = "Tutore with id %d not found.";


    @Transactional(readOnly = true)
    public PageDTO<PazienteDTO> searchAdvanced(
            Optional<Filter<Paziente>> filter,
            Pageable pageable, Integer userId) {
        try {
            Specification<Paziente> med = (root, query, cb) -> {
                var subquery = query.subquery(Integer.class);
                var prenotazione = subquery.from(Prenotazione.class);
                subquery.select(prenotazione.get("paziente").get("id"))
                        .where(
                                cb.equal(
                                        prenotazione.get("medico").get("user").get("id"),
                                        userId
                                )
                        );
                return root.get("id").in(subquery);
            };

            Specification<Paziente> finalSpec = filter.map(f -> getSpecificationForAdvancedSearch(f).and(med)).orElse(med);

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Paziente> page;

            if(isSorted) {
                List<Paziente> pazientes = pazienteRepository.findAll(finalSpec);
                page = getPage(sortingFields, pazientes, pageable, sortingProperty);
            } else {
                page = pazienteRepository.findAll(finalSpec, pageable);
            }

            return pazienteMapper.convertModelsPageToDtosPage(page);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Paziente> getSpecificationForAdvancedSearch(
            Filter<Paziente> filter) {
        return filter.toSpecification(pazienteSpecificationsFactory);
    }

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