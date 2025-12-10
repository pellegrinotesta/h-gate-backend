package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.DashboardResponse;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService extends BasicService {

    private final PrenotazioniDettagliateService prenotazioniDettagliateService;

    public DashboardResponse dashboardPaziente(Integer pazienteId) {
        DashboardResponse res = new DashboardResponse();
        res.setPrenotazioni(prenotazioniDettagliateService.prenotazioniPaziente(pazienteId));

        return res;
    }
}
