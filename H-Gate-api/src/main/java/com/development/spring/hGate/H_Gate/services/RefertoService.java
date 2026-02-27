package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.components.RefertoSpecificationsFactory;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoUpdateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PazienteDTO;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class RefertoService extends BasicService {

    private final RefertoRepository refertoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final RefertoMapper refertoMapper;
    private final ObjectMapper objectMapper;
    private final RefertoSpecificationsFactory refertoSpecificationsFactory;

    private final Map<String, Function<Referto, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("dataEmissione", referto -> referto.getDataEmissione() != null ? new ComparableWrapper(String.valueOf(referto.getDataEmissione())) : null);
        put("tipoReferto", referto -> referto.getTipoReferto() != null ? new ComparableWrapper(referto.getTipoReferto()) : null);
        put("nome", referto -> referto.getMedico().getUser().getNome() != null ? new ComparableWrapper(referto.getMedico().getUser().getNome()): null);
    }};

    @Transactional(readOnly = true)
    public PageDTO<RefertoDTO> searchAdvanced(
            Optional<Filter<Referto>> filter,
            Pageable pageable, Integer userId) {
        try {
            Specification<Referto> tutoreSpec = (root, query, cb) -> {
                var subquery = query.subquery(Integer.class);
                var pazienteTutore = subquery.from(PazienteTutore.class);
                subquery.select(pazienteTutore.get("paziente").get("id"))
                        .where(
                                cb.equal(
                                        pazienteTutore.get("tutore").get("user").get("id"),
                                        userId
                                )
                        );
                return root.get("paziente").get("id").in(subquery);
            };

            Specification<Referto> finalSpec = filter.map(f -> getSpecificationForAdvancedSearch(f).and(tutoreSpec)).orElse(tutoreSpec);

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<Referto> page;

            if(isSorted) {
                List<Referto> pazientes = refertoRepository.findAll(finalSpec);
                page = getPage(sortingFields, pazientes, pageable, sortingProperty);
            } else {
                page = refertoRepository.findAll(finalSpec, pageable);
            }

            return refertoMapper.convertModelsPageToDtosPage(page);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<Referto> getSpecificationForAdvancedSearch(
            Filter<Referto> filter) {
        return filter.toSpecification(refertoSpecificationsFactory);
    }


    public Referto getById(Integer refertoId) {
        return refertoRepository.findById(refertoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referto non trovato"));
    }

    public Referto findByPrenotazioneId(Integer prenotazioneId) {
        return refertoRepository.findByPrenotazioneId(prenotazioneId);
    }

    public List<Referto> listaRefertiTutore(Integer tutoreUserId) {
        return refertoRepository.findTop5ByTutoreUserIdOrderByDataEmissioneDesc(tutoreUserId);
    }

    public List<Referto> listaRefertiPaziente(Integer pazienteId) {
        return refertoRepository.listaRefertiPaziente(pazienteId);
    }

    @Transactional
    public Referto create(RefertoCreateDTO dto) {
        Prenotazione prenotazione = prenotazioneRepository.findById(dto.getPrenotazioneId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prenotazione non trovata"));

        if (refertoRepository.findByPrenotazioneId(prenotazione.getId()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esiste già un referto per questa prenotazione");
        }

        if (prenotazione.getMedico() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun medico associato alla prenotazione");
        }

        if (prenotazione.getPaziente() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessun paziente associato alla prenotazione");
        }

        Referto referto = refertoMapper.convertCreateDTOToModel(dto);
        referto.setPrenotazione(prenotazione);
        referto.setMedico(prenotazione.getMedico());
        referto.setPaziente(prenotazione.getPaziente());
        referto.setParametriVitali(serializeParametriVitali(dto.getParametriVitali()));

        return refertoRepository.save(referto);
    }

    @Transactional
    public Referto update(Integer refertoId, RefertoUpdateDTO dto) {
        Referto referto = getById(refertoId);

        if (Boolean.TRUE.equals(referto.getIsFirmato())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non è possibile modificare un referto già firmato");
        }

        refertoMapper.applyUpdateDTO(dto, referto);
        referto.setParametriVitali(serializeParametriVitali(dto.getParametriVitali()));

        return refertoRepository.save(referto);
    }

    @Transactional
    public void delete(Integer refertoId) {
        Referto referto = getById(refertoId);

        if (Boolean.TRUE.equals(referto.getIsFirmato())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non è possibile eliminare un referto già firmato");
        }

        refertoRepository.delete(referto);
    }

    // Serializza parametriVitali da Object (inviato dal frontend) a JSON String (salvato sul DB)
    private String serializeParametriVitali(Object parametriVitali) {
        if (parametriVitali == null) return null;
        try {
            return objectMapper.writeValueAsString(parametriVitali);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Errore serializzazione parametri vitali");
        }
    }
}