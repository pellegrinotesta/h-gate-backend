package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.DashboardMedicoResponse;
import com.development.spring.hGate.H_Gate.dtos.DashboardPazienteResponse;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService extends BasicService {

    private final PrenotazioniDettagliateService prenotazioniDettagliateService;
    private final RefertoService refertoService;
    private final RefertoMapper refertoMapper;
    private final MedicoRepository medicoRepository;

    public DashboardPazienteResponse dashboardPaziente(Integer pazienteId) {

        return DashboardPazienteResponse.builder().
                prenotazioni(prenotazioniDettagliateService.prenotazioniPaziente(pazienteId))
                .referti(refertoMapper.convertModelsToDtos(refertoService.listaRefertiPaziente(pazienteId)))
                .prossimiAppuntamenti(prenotazioniDettagliateService.prossimiAppuntamenti(pazienteId))
                .visiteTotali(prenotazioniDettagliateService.visiteTotali(pazienteId))
                .build();
    }

    public DashboardMedicoResponse dashboardMedico(Integer medicoUserId) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        return DashboardMedicoResponse.builder().
                visiteOggi(prenotazioniDettagliateService.visiteOggi(medicoUserId))
                .pazientiTotali(prenotazioniDettagliateService.pazientiTotali(medicoUserId))
                .refertiDaFirmare(prenotazioniDettagliateService.refertiDaFirmare(medicoUserId))
                .refertiDaCompletare(prenotazioniDettagliateService.refertiDaCompletare(medicoUserId))
                .ratingMedio(medico.getRatingMedio())
                .numeroRecensioni(medico.getNumeroRecensioni())
                .appuntamentiOggi(prenotazioniDettagliateService.appuntamentiOggi(medicoUserId))
                .build();
    }
}
