package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardMedicoResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardPazienteResponse;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService extends BasicService {

    private final PrenotazioniDettagliateService prenotazioniDettagliateService;
    private final RefertoService refertoService;
    private final RefertoMapper refertoMapper;
    private final MedicoRepository medicoRepository;
    private final PrenotazioneService prenotazioneService;

    /**
     * Dashboard per il TUTORE
     * Mostra i dati di TUTTI i minori del tutore
     */
    public DashboardPazienteResponse dashboardPaziente(Integer tutoreUserId) {
        return DashboardPazienteResponse.builder()
                // Usa i metodi per tutore invece che per paziente
                .prenotazioni(prenotazioniDettagliateService.prenotazioniTutore(tutoreUserId))
                .referti(refertoMapper.convertModelsToDtos(
                        refertoService.listaRefertiTutore(tutoreUserId)
                ))
                .prossimiAppuntamenti(prenotazioniDettagliateService.prossimiAppuntamentiTutore(tutoreUserId))
                .visiteTotali(prenotazioniDettagliateService.visiteTotaliTutore(tutoreUserId))
                .build();
    }

    /**
     * Dashboard per il MEDICO
     */
    public DashboardMedicoResponse dashboardMedico(Integer medicoUserId) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);

        return DashboardMedicoResponse.builder()
                .visiteOggi(prenotazioniDettagliateService.visiteOggi(medicoUserId))
                .pazientiTotali(prenotazioniDettagliateService.pazientiTotali(medicoUserId))
                .refertiDaFirmare(prenotazioniDettagliateService.refertiDaFirmare(medicoUserId))
                .refertiDaCompletare(prenotazioniDettagliateService.refertiDaCompletare(medicoUserId))
                .ratingMedio(medico.getRatingMedio())
                .numeroRecensioni(medico.getNumeroRecensioni())
                .appuntamentiOggi(prenotazioniDettagliateService.appuntamentiOggi(medicoUserId))
                .build();
    }

    /**
     * Dashboard per l'ADMIN
     */
    public DashboardAdminResponse dashboardAdmin(Integer adminUserId) {
        BigDecimal fatturatoMensile = prenotazioneService.fatturatoMensile();

        return DashboardAdminResponse.builder()
                //.mediciAttivi(medicoRepository.countByIsDisponibileTrue())
                .statistiche(prenotazioneService.getStatisticheGenerali())
                .prenotazioniOggi(prenotazioneService.prenotazioniOggi())
                .fatturatoMensile(
                        fatturatoMensile != null
                                ? fatturatoMensile.divide(BigDecimal.valueOf(1000))
                                : BigDecimal.ZERO
                )
                .mediciInAttesa(prenotazioniDettagliateService.mediciInAttesa())
                .mediciDaVerificare(prenotazioniDettagliateService.mediciInAttesa().size())
                .build();
    }
}