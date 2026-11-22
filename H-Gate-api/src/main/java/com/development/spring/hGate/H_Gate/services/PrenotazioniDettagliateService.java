package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.components.PrenotazioneSpecificationsFactory;
import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDaVerificareDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioniDettagliateDTO;
import com.development.spring.hGate.H_Gate.entity.*;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.libs.data.models.Filter;
import com.development.spring.hGate.H_Gate.libs.utils.ComparableWrapper;
import com.development.spring.hGate.H_Gate.libs.utils.Pair;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.mappers.PrenotazioneDettagliateMapper;
import com.development.spring.hGate.H_Gate.repositories.PazienteTutoreRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioniDettagliateRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PrenotazioniDettagliateService extends BasicService {

    private final PrenotazioniDettagliateRepository prenotazioniDettagliateRepository;
    private final RefertoRepository refertoRepository;
    private final PrenotazioneSpecificationsFactory prenotazioneSpecificationsFactory;
    private final PrenotazioneDettagliateMapper prenotazioneDettagliateMapper;
    private final PazienteTutoreRepository pazienteTutoreRepository;

    private final Map<String, Function<VPrenotazioniDettagliate, ComparableWrapper>> sortingFields = new HashMap<>() {{
        put("numeroPrenotazione", prenotazioniDettagliate -> prenotazioniDettagliate.getNumeroPrenotazione() != null ? new ComparableWrapper(prenotazioniDettagliate.getNumeroPrenotazione()) : null);
        put("tipoVisita", prenotazioniDettagliate -> prenotazioniDettagliate.getTipoVisita() != null ? new ComparableWrapper(prenotazioniDettagliate.getTipoVisita()) : null);
        put("stato", prenotazioniDettagliate -> prenotazioniDettagliate.getStato() != null ? new ComparableWrapper(prenotazioniDettagliate.getStato()): null);
        put("pazienteNomeCompleto", prenotazioniDettagliate -> prenotazioniDettagliate.getPazienteNomeCompleto() != null ? new ComparableWrapper(prenotazioniDettagliate.getPazienteNomeCompleto()) : null);
        put("tutoreNomeCompleto", prenotazioniDettagliate -> prenotazioniDettagliate.getTutoreNomeCompleto() != null ? new ComparableWrapper(prenotazioniDettagliate.getTutoreNomeCompleto()) : null);
        put("medicoNomeCompleto", prenotazioniDettagliate -> prenotazioniDettagliate.getMedicoNomeCompleto() != null ? new ComparableWrapper(prenotazioniDettagliate.getMedicoNomeCompleto()) : null);
    }};

    /**
     * Ricerca avanzata - USA IL TUO SISTEMA FILTER ESISTENTE
     */
    @Transactional(readOnly = true)
    public PageDTO<PrenotazioniDettagliateDTO> searchAdvanced(
            Optional<Filter<VPrenotazioniDettagliate>> filter,
            Pageable pageable, Integer userId) {
        try {
            List<Integer> pazienteIds = pazienteTutoreRepository.findPazienteIdsByTutoreUserId(userId);

            Specification<VPrenotazioniDettagliate> tutoreSpec = (root, query, cb) ->
                    root.get("pazienteId").in(pazienteIds);
            Specification<VPrenotazioniDettagliate> finalSpec = filter.map(f -> getSpecificationForAdvancedSearch(f).and(tutoreSpec)).orElse(tutoreSpec);

            Pair<Boolean, String> sortingInfo = isSortedOnNonDirectlyMappedField(sortingFields, pageable);
            boolean isSorted = sortingInfo.getFirst();
            String sortingProperty = sortingInfo.getSecond();
            Page<VPrenotazioniDettagliate> page;

            if(isSorted) {
                List<VPrenotazioniDettagliate> frames = filter
                        .map(uf -> prenotazioniDettagliateRepository.findAll(
                                getSpecificationForAdvancedSearch(uf).and(tutoreSpec)))  // ← AND qui
                        .orElseGet(() -> prenotazioniDettagliateRepository.findAll(tutoreSpec));

                page = getPage(sortingFields, frames, pageable, sortingProperty);
            } else {
                page = filter.map(uf -> prenotazioniDettagliateRepository.findAll(
                        getSpecificationForAdvancedSearch(uf).and(tutoreSpec), pageable))
                        .orElseGet(() -> prenotazioniDettagliateRepository.findAll(tutoreSpec, pageable));
            }

            return prenotazioneDettagliateMapper.convertModelsPageToDtosPage(page);

        } catch (PropertyReferenceException ex) {
            String message = String.format(INVALID_SEARCH_CRITERIA, ex.getMessage());
            logger.debug(message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Specification<VPrenotazioniDettagliate> getSpecificationForAdvancedSearch(
            Filter<VPrenotazioniDettagliate> filter) {
        return filter.toSpecification(prenotazioneSpecificationsFactory);
    }

    /**
     * Ottiene le prossime 5 prenotazioni per il tutore
     */
    @Transactional(readOnly = true)
    public List<VPrenotazioniDettagliate> prenotazioniTutore(Integer tutoreUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository.findTop5ByTutoreUserIdAndDataOraAfterAndStatoInOrderByDataOraAsc(
                tutoreUserId, now, stati);
    }

    /**
     * Conta i prossimi appuntamenti del tutore
     */
    @Transactional(readOnly = true)
    public Integer prossimiAppuntamentiTutore(Integer tutoreUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository.countByTutoreUserIdAndDataOraAfterAndStatoIn(
                tutoreUserId, now, stati);
    }

    /**
     * Conta tutte le visite del tutore
     */
    @Transactional(readOnly = true)
    public Integer visiteTotaliTutore(Integer tutoreUserId) {
        return prenotazioniDettagliateRepository.countByTutoreUserId(tutoreUserId);
    }

    /**
     * Conta le visite di oggi del medico
     */
    @Transactional(readOnly = true)
    public Integer visiteOggi(Integer medicoUserId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository.countByMedicoUserIdAndDataOraBetweenAndStatoIn(
                medicoUserId, startOfDay, endOfDay, stati);
    }

    /**
     * Conta i pazienti totali del medico
     */
    @Transactional(readOnly = true)
    public Integer pazientiTotali(Integer medicoUserId) {
        return prenotazioniDettagliateRepository.countDistinctPazientiByMedico(medicoUserId);
    }

    /**
     * Conta i referti da firmare del medico
     */
    @Transactional(readOnly = true)
    public Integer refertiDaFirmare(Integer medicoUserId) {
        return refertoRepository.countByMedicoUserIdAndIsFirmatoFalse(medicoUserId);
    }

    /**
     * Conta i referti da completare (visite completate senza referto)
     */
    @Transactional(readOnly = true)
    public Integer refertiDaCompletare(Integer medicoUserId) {
        return prenotazioniDettagliateRepository.countByMedicoUserIdAndStato(
                medicoUserId, StatoPrenotazioneEnum.COMPLETATA.name());
    }

    /**
     * Ottiene gli appuntamenti di oggi del medico
     */
    @Transactional(readOnly = true)
    public List<PrenotazioneDTO> appuntamentiOggi(Integer medicoUserId) {
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        List<VPrenotazioniDettagliate> prenotazioni = prenotazioniDettagliateRepository.findByMedicoUserIdAndStatoIn(
                medicoUserId, stati);

        return prenotazioni.stream()
                .map(this::mapToPrenotazioneDTO)
                .toList();
    }

    private PrenotazioneDTO mapToPrenotazioneDTO(VPrenotazioniDettagliate v) {
        return PrenotazioneDTO.builder()
                .id(v.getId())
                .numeroPrenotazione(v.getNumeroPrenotazione())
                .dataOra(v.getDataOra())
                .dataOraFine(v.getDataOraFine())
                .tipoVisita(v.getTipoVisita())
                .stato(v.getStato())
                .costo(v.getCosto())
                .paziente(PrenotazioneDTO.PazienteMinDTO.builder()
                        .id(v.getPazienteId())
                        .nome(v.getPazienteNome())
                        .cognome(v.getPazienteCognome())
                        .codiceFiscale(v.getPazienteCf())
                        .build())
                .build();
    }
}