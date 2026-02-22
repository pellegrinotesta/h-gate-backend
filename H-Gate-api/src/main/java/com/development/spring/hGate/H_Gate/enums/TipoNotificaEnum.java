package com.development.spring.hGate.H_Gate.enums;

import lombok.Getter;

@Getter
public enum TipoNotificaEnum {
    NUOVA_PRENOTAZIONE("Nuova prenotazione"),
    CONFERMA_PRENOTAZIONE("Prenotazione confermata"),
    RIFIUTO_PRENOTAZIONE("Prenotazione rifiutata"),
    ANNULLAMENTO_PRENOTAZIONE("Prenotazione annullata"),
    PROMEMORIA("Promemoria appuntamento"),
    NO_SHOW("Non visualizzata"),
    SOLLECITO("Sollecito promemoria"),

    // Referti
    NUOVO_REFERTO("Nuovo referto disponibile"),

    // Sistema
    BENVENUTO("Benvenuto"),
    VERIFICA_ACCOUNT("Verifica account"),
    RESET_PASSWORD("Reset password"),

    // Medico
    VERIFICA_MEDICO("Verifica documenti medico"),
    APPROVAZIONE_MEDICO("Account medico approvato"),
    RIFIUTO_MEDICO("Account medico rifiutato"),

    // Pagamenti
    PAGAMENTO_RICEVUTO("Pagamento ricevuto"),
    PAGAMENTO_FALLITO("Pagamento fallito"),

    // Altro
    SISTEMA("Notifica di sistema"),
    PROMOZIONALE("Comunicazione promozionale");

    private final String descrizione;

    TipoNotificaEnum(String descrizione) {
        this.descrizione = descrizione;
    }

}
