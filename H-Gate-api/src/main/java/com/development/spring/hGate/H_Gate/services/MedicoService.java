package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.components.MedicoSpecificationsFactory;
import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.TariffeMedici;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.MedicoMapper;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.TariffeMediciRepository;
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

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MedicoService extends BasicService {

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final TariffeMediciRepository tariffeMediciRepository;
    private final MedicoSpecificationsFactory medicoSpecificationsFactory;

    private final Map<String, Function<Medico, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("specializzazione", medico -> medico.getSpecializzazione() != null ? new ComparableWrapper(medico.getSpecializzazione()) : null);
        put("provincia", medico -> medico.getUser().getProvincia() != null ? new ComparableWrapper(medico.getUser().getProvincia()) : null);
        put("citta", medico -> medico.getUser().getCitta() != null ? new ComparableWrapper(medico.getUser().getCitta()): null);
        put("nome", medico -> medico.getUser().getNome() != null ? new ComparableWrapper(medico.getUser().getNome()) : null);
    }};

    private static final String MEDICO_ID_NOT_FOUND = "Medico with id %d not found.";

    @Transactional
    public PageDTO<MedicoDTO> searchAdvanced(Optional<Filter<Medico>> filter, Pageable pageable) {
        try {
            // Mostra solo medici verificati e disponibili
            Specification<Medico> attivi = (root, query, cb) -> {
                query.distinct(true); // ← evita duplicati dalla join su user
                var user = root.join("user");
                return cb.and(
                        cb.equal(root.get("isDisponibile"), true),
                        // solo medici approvati dalla clinica e con account attivo
                        cb.isTrue(user.get("isVerified")),
                        cb.or(cb.isNull(user.get("isActive")), cb.isTrue(user.get("isActive")))
                );
            };

            Specification<Medico> finalSpec = filter
                    .map(f -> getSpecificationForAdvancedSearch(f).and(attivi))
                    .orElse(attivi);

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Medico> medicoPage;

            if (isSorted) {
                List<Medico> medici = medicoRepository.findAll(finalSpec);
                medicoPage = getPage(sortingFields, new ArrayList<>(medici), pageable, sortingProperty);
            } else {
                medicoPage = medicoRepository.findAll(finalSpec, pageable);
            }

            return medicoMapper.convertModelsPageToDtosPage(medicoPage);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public Medico findMedicoByUserId(JwtAuthentication jwtAuthentication) {
        return medicoRepository.findMedicoByUserId(jwtAuthentication.getId());
    }

    /** Elenco pubblico dei medici: solo quelli approvati dalla clinica. */
    public List<Medico> getAll() {
        return medicoRepository.findMediciApprovati();
    }

    /**
     * Aggiorna il profilo del medico autenticato. L'eventuale id nel body viene ignorato:
     * un medico può modificare solo il proprio profilo.
     */
    public Medico update(Integer medicoUserId, Medico medico) {
        Medico medicoExisting = medicoRepository.findMedicoByUserId(medicoUserId);
        if (medicoExisting == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profilo medico non trovato");
        }

        medicoExisting.setAnnoLaurea(medico.getAnnoLaurea());
        medicoExisting.setBio(medico.getBio());
        medicoExisting.setCurriculum(medico.getCurriculum());
        medicoExisting.setDurataVisitaMinuti(medico.getDurataVisitaMinuti());
        medicoExisting.setNumeroAlbo(medico.getNumeroAlbo());
        medicoExisting.setUniversita(medico.getUniversita());

        return medicoRepository.save(medicoExisting);
    }

    public List<TariffeMedici> listaTariffeMedico(Integer medicoId) {
        return tariffeMediciRepository.findByMedicoId(medicoId);
    }

    private Specification<Medico> getSpecificationForAdvancedSearch(Filter<Medico> etsiFilter) {
        return etsiFilter.toSpecification(medicoSpecificationsFactory);
    }
}
