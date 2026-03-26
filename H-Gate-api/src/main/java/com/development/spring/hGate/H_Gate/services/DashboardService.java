package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardMedicoResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardPazienteResponse;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.entity.VDashboardKpi;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.DashboardKpiRepository;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService extends BasicService {

    private final PrenotazioniDettagliateService prenotazioniDettagliateService;
    private final RefertoService refertoService;
    private final RefertoMapper refertoMapper;
    private final MedicoRepository medicoRepository;
    private final PrenotazioneService prenotazioneService;
    private final UserRepository userRepository;
    private final DashboardKpiRepository kpiRepository;

    private final Logger log = LoggerFactory.getLogger(DashboardService.class);

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
        Users user = userRepository.findById(medicoUserId).orElseThrow(() -> buildEntityWithIdNotFoundException(medico.getId(), "Utente non trovato"));

        return DashboardMedicoResponse.builder()
                .medicoId(medico.getId())
                .nomeMedico(user.getNomeCompleto())
                .visiteOggi(prenotazioniDettagliateService.visiteOggi(medicoUserId))
                .pazientiTotali(prenotazioniDettagliateService.pazientiTotali(medicoUserId))
                .refertiDaFirmare(prenotazioniDettagliateService.refertiDaFirmare(medicoUserId))
                .refertiDaCompletare(prenotazioniDettagliateService.refertiDaCompletare(medicoUserId))
                .ratingMedio(medico.getRatingMedio())
                .appuntamentiOggi(prenotazioniDettagliateService.appuntamentiOggi(medicoUserId))
                .build();
    }

    /**
     * Dashboard per l'ADMIN
     */
    @Transactional(readOnly = true)
    public DashboardAdminResponse getDashboardData() {
        log.info("Recupero dati dashboard completi");

        DashboardAdminResponse.KpiData kpi = getKpiData();

        return DashboardAdminResponse.builder()
                .kpi(kpi)
                .stats(generateStatCards(kpi))
                .attivitaRecenti(getAttivitaRecenti())
                .build();
    }

    /**
     * Recupera KPI con cache (5 minuti)
     */
    @Cacheable(value = "dashboard-kpi", unless = "#result == null")
    @Transactional(readOnly = true)
    public DashboardAdminResponse.KpiData getKpiData() {
        log.info("Recupero KPI da database");

        VDashboardKpi kpi = kpiRepository.findLatestKpi()
                .orElseThrow(() -> new EntityNotFoundException("Nessun dato KPI disponibile"));

        return mapKpiToDto(kpi);
    }

    /**
     * Invalida cache KPI
     */
    @CacheEvict(value = "dashboard-kpi", allEntries = true)
    public void invalidateKpiCache() {
        log.info("Cache KPI invalidata");
    }

    /**
     * Refresh automatico cache ogni 5 minuti
     */
    @Scheduled(fixedRate = 300000)
    @CacheEvict(value = "dashboard-kpi", allEntries = true)
    public void refreshKpiCache() {
        log.debug("Refresh automatico cache KPI");
    }

    /**
     * Mappa entity a DTO
     */
    private DashboardAdminResponse.KpiData mapKpiToDto(VDashboardKpi kpi) {
        return DashboardAdminResponse.KpiData.builder()
                .totalePazienti(kpi.getTotalePazienti())
                .pazientiConsensoAttivo(kpi.getPazientiConsensoAttivo())
                .pazientiInTerapia(kpi.getPazientiInTerapia())
                .pazienti0_5Anni(kpi.getPazienti0_5Anni())
                .pazienti6_12Anni(kpi.getPazienti6_12Anni())
                .pazienti13_18Anni(kpi.getPazienti13_18Anni())
                .totaleTutori(kpi.getTotaleTutori())
                .mediciDisponibili(kpi.getMediciDisponibili())
                .mediciAttivi(kpi.getMediciAttivi())
                .neuropsichiatri(kpi.getNeuropsichiatri())
                .psicologi(kpi.getPsicologi())
                .logopedisti(kpi.getLogopedisti())
                .prenotazioniOggi(kpi.getPrenotazioniOggi())
                .prenotazioniOggiConfermate(kpi.getPrenotazioniOggiConfermate())
                .prenotazioniOggiCompletate(kpi.getPrenotazioniOggiCompletate())
                .prenotazioniProssimi7Giorni(kpi.getPrenotazioniProssimi7Giorni())
                .prenotazioniQuestaSettimana(kpi.getPrenotazioniQuestaSettimana())
                .prenotazioniQuestoMese(kpi.getPrenotazioniQuestoMese())
                .prenotazioniCompletateMese(kpi.getPrenotazioniCompletateMese())
                .prenotazioniDaConfermare(kpi.getPrenotazioniDaConfermare())
                .prenotazioniAnnullateMese(kpi.getPrenotazioniAnnullateMese())
                .percorsiAttivi(kpi.getPercorsiAttivi())
                .percorsiSospesi(kpi.getPercorsiSospesi())
                .refertiEmessiMese(kpi.getRefertiEmessiMese())
                .refertiDaFirmare(kpi.getRefertiDaFirmare())
                .refertiDaInviare(kpi.getRefertiDaInviare())
                .notificheNonLette(kpi.getNotificheNonLette())
                .notificheOggi(kpi.getNotificheOggi())
                .mediaSedutePerPercorso(kpi.getMediaSedutePerPercorso())
                .trendPrenotazioniMese(kpi.getTrendPrenotazioniMese())
                .ultimoAggiornamento(kpi.getUltimoAggiornamento())
                .build();
    }

    /**
     * Genera stat cards
     */
    private List<DashboardAdminResponse.StatCard> generateStatCards(DashboardAdminResponse.KpiData kpi) {
        List<DashboardAdminResponse.StatCard> cards = new ArrayList<>();

        cards.add(DashboardAdminResponse.StatCard.builder()
                .title("Pazienti Totali")
                .value(String.valueOf(kpi.getTotalePazienti()))
                .icon("people")
                .color("primary")
                .change(kpi.getPazientiInTerapia() + " in terapia")
                .trend("up")
                .build());

        cards.add(DashboardAdminResponse.StatCard.builder()
                .title("Medici Attivi")
                .value(String.valueOf(kpi.getMediciAttivi()))
                .icon("local_hospital")
                .color("success")
                .change(kpi.getMediciVerificati() + " verificati")
                .build());

        cards.add(DashboardAdminResponse.StatCard.builder()
                .title("Prenotazioni Oggi")
                .value(String.valueOf(kpi.getPrenotazioniOggi()))
                .icon("event")
                .color("warning")
                .change(kpi.getPrenotazioniOggiConfermate() + " confermate")
                .trend(kpi.getTrendPrenotazioniMese() >= 0 ? "up" : "down")
                .build());

        cards.add(DashboardAdminResponse.StatCard.builder()
                .title("Da Confermare")
                .value(String.valueOf(kpi.getPrenotazioniDaConfermare()))
                .icon("pending_actions")
                .color("info")
                .change(kpi.getPrenotazioniProssimi7Giorni() + " prossimi 7gg")
                .build());

        return cards;
    }



    /**
     * Recupera attività recenti
     * TODO: Implementare con tabella audit_logs
     */
    @Transactional(readOnly = true)
    public List<DashboardAdminResponse.AttivitaRecente> getAttivitaRecenti() {
        // Placeholder - implementare con audit logs
        return Arrays.asList(
                DashboardAdminResponse.AttivitaRecente.builder()
                        .id(1L)
                        .action("Nuovo medico registrato")
                        .time(java.time.LocalDateTime.now().minusHours(2))
                        .icon("person_add")
                        .type("success")
                        .build(),
                DashboardAdminResponse.AttivitaRecente.builder()
                        .id(2L)
                        .action("Prenotazione confermata")
                        .time(java.time.LocalDateTime.now().minusHours(5))
                        .icon("event_available")
                        .type("primary")
                        .build()
        );
    }
}