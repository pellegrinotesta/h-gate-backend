package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsiTerapeuticiDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsoUpdateDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PercorsiTerapeutici;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.PercorsiTerapeuticiRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PercorsiTerapeuticiService extends BasicService {

    private final PercorsiTerapeuticiRepository percorsiTerapeuticiRepository;
    private final PazienteRepository pazienteRepository;
    private final MedicoRepository medicoRepository;

    public List<PercorsiTerapeuticiDTO> percorsiTerapeuticiMedicoAndPaziente(Integer medicoId, Integer pazienteId) {
        return percorsiTerapeuticiRepository.percorsiTerapeuticiPazienteAndMedico(
                medicoId, pazienteId
        ).stream().map(this::toDTO).toList();
    }

    public List<PercorsiTerapeuticiDTO> getByPaziente(Integer pazienteId) {
        return percorsiTerapeuticiRepository.findByPazienteIdOrderByDataInizioDesc(pazienteId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public PercorsiTerapeuticiDTO create(
            Integer medicoId,
            PercorsoCreateDTO dto) {

        Paziente paziente = pazienteRepository.findById(dto.getPazienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paziente non trovato"));

        Medico medico = medicoRepository.findMedicoByUserId(medicoId);

        PercorsiTerapeutici percorso = PercorsiTerapeutici.builder()
                .paziente(paziente)
                .medico(medico.getUser())
                .titolo(dto.getTitolo())
                .obiettivi(dto.getObiettivi())
                .dataInizio(LocalDateTime.now())
                .dataFinePrevista(dto.getDataFinePrevista())
                .numeroSedutePreviste(dto.getNumeroSedutePreviste())
                .numeroSeduteEffettuate(0)
                .stato("ATTIVO")
                .build();

        return toDTO(percorsiTerapeuticiRepository.save(percorso));
    }

    private PercorsiTerapeutici findOrThrow(Integer id) {
        return percorsiTerapeuticiRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Percorso non trovato"));
    }

    @Transactional
    public PercorsiTerapeuticiDTO update(Integer id, PercorsoUpdateDTO dto) {
        PercorsiTerapeutici existing = findOrThrow(id);

        if (dto.getTitolo() != null) existing.setTitolo(dto.getTitolo());
        if (dto.getObiettivi() != null) existing.setObiettivi(dto.getObiettivi());
        if (dto.getDataFinePrevista() != null) existing.setDataFinePrevista(dto.getDataFinePrevista());
        if (dto.getStato() != null) existing.setStato(dto.getStato());
        if (dto.getNumeroSedutePreviste() != null) existing.setNumeroSedutePreviste(dto.getNumeroSedutePreviste());
        if (dto.getNumeroSeduteEffettuate() != null) existing.setNumeroSeduteEffettuate(dto.getNumeroSeduteEffettuate());

        return toDTO(percorsiTerapeuticiRepository.save(existing));
    }

    @Transactional
    public void delete(Integer id) {
        percorsiTerapeuticiRepository.delete(findOrThrow(id));
    }

    public PercorsiTerapeuticiDTO getById(Integer id) {
        return toDTO(findOrThrow(id));
    }

    private PercorsiTerapeuticiDTO toDTO(PercorsiTerapeutici p) {
        int progresso = 0;
        if (p.getNumeroSedutePreviste() != null && p.getNumeroSedutePreviste() > 0) {
            progresso = Math.round(((float)(p.getNumeroSeduteEffettuate() != null ? p.getNumeroSeduteEffettuate() : 0)
                    / p.getNumeroSedutePreviste()) * 100);
        }
        return PercorsiTerapeuticiDTO.builder()
                .id(p.getId())
                .titolo(p.getTitolo())
                .obiettivi(p.getObiettivi())
                .dataInizio(p.getDataInizio())
                .dataFinePrevista(p.getDataFinePrevista())
                .stato(p.getStato())
                .numeroSedutePreviste(p.getNumeroSedutePreviste())
                .numeroSeduteEffettuate(p.getNumeroSeduteEffettuate())
                .medicoNome(p.getMedico().getNome())
                .medicoCognome(p.getMedico().getCognome())
                .progressoPercentuale(progresso)
                .build();
    }

}
