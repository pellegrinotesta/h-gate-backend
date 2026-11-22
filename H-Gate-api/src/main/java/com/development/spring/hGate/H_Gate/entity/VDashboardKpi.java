package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_dashboard_kpi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VDashboardKpi implements IdentifiableEntity<LocalDateTime> {

    @Id
    @Column(name = "ultimo_aggiornamento")
    private LocalDateTime ultimoAggiornamento;

    // ==================== PAZIENTI ====================
    @Column(name = "totale_pazienti")
    private Integer totalePazienti;

    @Column(name = "pazienti_consenso_attivo")
    private Integer pazientiConsensoAttivo;

    @Column(name = "pazienti_in_terapia")
    private Integer pazientiInTerapia;

    @Column(name = "pazienti_0_5_anni")
    private Integer pazienti0_5Anni;

    @Column(name = "pazienti_6_12_anni")
    private Integer pazienti6_12Anni;

    @Column(name = "pazienti_13_18_anni")
    private Integer pazienti13_18Anni;

    // ==================== TUTORI ====================
    @Column(name = "totale_tutori")
    private Integer totaleTutori;

    // ==================== MEDICI ====================
    @Column(name = "medici_disponibili")
    private Integer mediciDisponibili;

    @Column(name = "medici_attivi")
    private Integer mediciAttivi;

    @Column(name = "neuropsichiatri")
    private Integer neuropsichiatri;

    @Column(name = "psicologi")
    private Integer psicologi;

    @Column(name = "logopedisti")
    private Integer logopedisti;

    // ==================== PRENOTAZIONI ====================
    @Column(name = "prenotazioni_oggi")
    private Integer prenotazioniOggi;

    @Column(name = "prenotazioni_oggi_confermate")
    private Integer prenotazioniOggiConfermate;

    @Column(name = "prenotazioni_oggi_completate")
    private Integer prenotazioniOggiCompletate;

    @Column(name = "prenotazioni_prossimi_7_giorni")
    private Integer prenotazioniProssimi7Giorni;

    @Column(name = "prenotazioni_questa_settimana")
    private Integer prenotazioniQuestaSettimana;

    @Column(name = "prenotazioni_questo_mese")
    private Integer prenotazioniQuestoMese;

    @Column(name = "prenotazioni_completate_mese")
    private Integer prenotazioniCompletateMese;

    @Column(name = "prenotazioni_da_confermare")
    private Integer prenotazioniDaConfermare;

    @Column(name = "prenotazioni_annullate_mese")
    private Integer prenotazioniAnnullateMese;

    // ==================== PERCORSI TERAPEUTICI ====================
    @Column(name = "percorsi_attivi")
    private Integer percorsiAttivi;

    @Column(name = "percorsi_sospesi")
    private Integer percorsiSospesi;

    // ==================== REFERTI ====================
    @Column(name = "referti_emessi_mese")
    private Integer refertiEmessiMese;

    @Column(name = "referti_da_firmare")
    private Integer refertiDaFirmare;

    @Column(name = "referti_da_inviare")
    private Integer refertiDaInviare;

    // ==================== NOTIFICHE ====================
    @Column(name = "notifiche_non_lette")
    private Integer notificheNonLette;

    @Column(name = "notifiche_oggi")
    private Integer notificheOggi;

    // ==================== METRICHE ====================

    @Column(name = "media_sedute_per_percorso")
    private BigDecimal mediaSedutePerPercorso;

    @Column(name = "trend_prenotazioni_mese")
    private Integer trendPrenotazioniMese;

    @Override
    public LocalDateTime getId() {
        return ultimoAggiornamento;
    }

    @Override
    public void setId(LocalDateTime paramId) {
        this.ultimoAggiornamento = paramId;
    }
}
