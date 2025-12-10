package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioniDettagliateRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrenotazioniDettagliateService extends BasicService {

    private final PrenotazioniDettagliateRepository prenotazioniDettagliateRepository;

    public List<VPrenotazioniDettagliate> prenotazioniPaziente(Integer pazienteId) {
        return prenotazioniDettagliateRepository.prenotazioniPaziente(pazienteId);
    }
}
