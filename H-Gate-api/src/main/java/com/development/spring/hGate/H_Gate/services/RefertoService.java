package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.medici.RefertoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoUpdateDTO;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefertoService extends BasicEntity {

    private final RefertoRepository refertoRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final RefertoMapper refertoMapper;
    private final ObjectMapper objectMapper;

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

    public List<Referto> listaRefertiPaziente(Integer userId) {
        return refertoRepository.listaRefertiPaziente(userId);
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