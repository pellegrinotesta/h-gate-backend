package com.development.spring.hGate.H_Gate.dtos.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DashboardAdminResponse {
    private KpiData kpi;
    private List<StatCard> stats;
    private List<MedicoDaVerificare> mediciDaVerificare;
    private List<AttivitaRecente> attivitaRecenti;

    // ==================== KPI DATA ====================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiData {
        // Pazienti
        private Integer totalePazienti;
        private Integer pazientiConsensoAttivo;
        private Integer pazientiInTerapia;
        private Integer pazienti0_5Anni;
        private Integer pazienti6_12Anni;
        private Integer pazienti13_18Anni;

        // Tutori
        private Integer totaleTutori;

        // Medici
        private Integer mediciDisponibili;
        private Integer mediciVerificati;
        private Integer mediciAttivi;
        private Integer neuropsichiatri;
        private Integer psicologi;
        private Integer logopedisti;

        // Prenotazioni
        private Integer prenotazioniOggi;
        private Integer prenotazioniOggiConfermate;
        private Integer prenotazioniOggiCompletate;
        private Integer prenotazioniProssimi7Giorni;
        private Integer prenotazioniQuestaSettimana;
        private Integer prenotazioniQuestoMese;
        private Integer prenotazioniCompletateMese;
        private Integer prenotazioniDaConfermare;
        private Integer prenotazioniAnnullateMese;

        // Percorsi
        private Integer percorsiAttivi;
        private Integer percorsiInValutazione;
        private Integer percorsiSospesi;

        // Referti
        private Integer refertiEmessiMese;
        private Integer refertiDaFirmare;
        private Integer refertiDaInviare;

        // Notifiche
        private Integer notificheNonLette;
        private Integer notificheOggi;

        // Metriche
        private BigDecimal ratingMedioMedici;
        private BigDecimal mediaSedutePerPercorso;
        private Integer trendPrenotazioniMese;

        private LocalDateTime ultimoAggiornamento;
    }

    // ==================== STAT CARD ====================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatCard {
        private String title;
        private String value;
        private String icon;
        private String color;
        private String change;
        private String trend;
    }

    // ==================== MEDICO DA VERIFICARE ====================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicoDaVerificare {
        private Integer id;
        private String nome;
        private String cognome;
        private String email;
        private String specializzazione;
        private String numeroAlbo;
        private String universita;
        private Integer annoLaurea;
        private LocalDateTime createdAt;
        private Boolean hasDocuments;
    }

    // ==================== ATTIVITA RECENTE ====================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttivitaRecente {
        private Long id;
        private String action;
        private LocalDateTime time;
        private String icon;
        private String type;
    }
}
