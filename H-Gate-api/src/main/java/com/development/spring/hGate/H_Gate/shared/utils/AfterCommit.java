package com.development.spring.hGate.H_Gate.shared.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Esegue un'azione (es. notifica/email) solo dopo il commit della transazione corrente:
 * niente avvisi per operazioni poi annullate. Gli errori dell'azione vengono registrati e ignorati.
 */
public final class AfterCommit {

    private static final Logger logger = LoggerFactory.getLogger(AfterCommit.class);

    private AfterCommit() {}

    public static void run(Runnable azione) {
        Runnable sicura = () -> {
            try {
                azione.run();
            } catch (Exception e) {
                logger.error("Azione post-commit fallita", e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    sicura.run();
                }
            });
        } else {
            sicura.run();
        }
    }
}
