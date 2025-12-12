package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.DashboardResponse;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService extends BasicService {

    private final PrenotazioniDettagliateService prenotazioniDettagliateService;
    private final RefertoService refertoService;
    private final RefertoMapper refertoMapper;

    public DashboardResponse dashboardPaziente(Integer pazienteId) {
        DashboardResponse res = new DashboardResponse();
        res.setPrenotazioni(prenotazioniDettagliateService.prenotazioniPaziente(pazienteId));
        res.setReferti(refertoMapper.convertModelsToDtos(refertoService.listaRefertiPaziente(pazienteId)));

        return res;
    }
}
