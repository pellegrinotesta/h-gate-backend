package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazionePsicologicaDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazioneUpdateDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.ValutazionePsicologica;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.ValutazionePsicologicaRepository;
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
public class ValutazionePsicologicaService extends BasicService {

    private final ValutazionePsicologicaRepository valutazionePsicologicaRepository;
    private final PazienteRepository pazienteRepository;
    private final MedicoRepository medicoRepository;

    public List<ValutazionePsicologicaDTO> getByPaziente(Integer pazienteId) {
        return valutazionePsicologicaRepository.findByPazienteIdOrderByDataValutazioneDesc(pazienteId)
                .stream().map(this::toDTO).toList();
    }

    public ValutazionePsicologicaDTO getById(Integer id) {
        return toDTO(findOrThrow(id));
    }

    @Transactional
    public ValutazionePsicologicaDTO create(
            Integer medicoUserId,
            ValutazioneCreateDTO dto) {

        Paziente paziente = pazienteRepository.findById(dto.getPazienteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paziente non trovato"));

        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);

        ValutazionePsicologica valutazione = ValutazionePsicologica.builder()
                .paziente(paziente)
                .medico(medico.getUser())
                .tipoTest(dto.getTipoTest())
                .punteggi(dto.getPunteggi())
                .interpretazione(dto.getInterpretazione())
                .dataValutazione(LocalDateTime.now())
                .build();

        return toDTO(valutazionePsicologicaRepository.save(valutazione));
    }

    @Transactional
    public ValutazionePsicologicaDTO update(Integer id, ValutazioneUpdateDTO dto) {
        ValutazionePsicologica existing = findOrThrow(id);

        if (dto.getTipoTest() != null) existing.setTipoTest(dto.getTipoTest());
        if (dto.getPunteggi() != null) existing.setPunteggi(dto.getPunteggi());
        if (dto.getInterpretazione() != null) existing.setInterpretazione(dto.getInterpretazione());

        return toDTO(valutazionePsicologicaRepository.save(existing));
    }

    @Transactional
    public void delete(Integer id) {
        valutazionePsicologicaRepository.delete(findOrThrow(id));
    }

    private ValutazionePsicologica findOrThrow(Integer id) {
        return valutazionePsicologicaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Valutazione non trovata"));
    }

    private ValutazionePsicologicaDTO toDTO(ValutazionePsicologica v) {
        return ValutazionePsicologicaDTO.builder()
                .id(v.getId())
                .pazienteId(v.getPaziente().getId())
                .medicoId(v.getMedico().getId())
                .tipoTest(v.getTipoTest())
                .punteggi(v.getPunteggi())
                .interpretazione(v.getInterpretazione())
                .dataValutazione(v.getDataValutazione())
                .medicoNome(v.getMedico().getNome())
                .medicoCognome(v.getMedico().getCognome())
                .build();
    }
}
