-- ============================================================
-- MIGRAZIONE 2026-11-22: approvazione medici e associazioni tutore-minore
--
-- Da eseguire UNA VOLTA sui database creati prima di questa versione
-- (es. produzione su Render). Le nuove installazioni la includono già in schema.sql.
--
--   mysql -h <host> -u <utente> -p <database> < 2026-11-22_approvazioni.sql
-- ============================================================

-- 1. I medici già presenti erano operativi prima che esistesse l'approvazione:
--    vengono considerati approvati, altrimenti non potrebbero più accedere.
UPDATE users u
JOIN medici m ON m.user_id = u.id
SET u.is_verified = TRUE
WHERE u.is_verified IS NULL OR u.is_verified = FALSE;

-- 2. Tabella delle richieste di associazione tutore-minore
CREATE TABLE IF NOT EXISTS richieste_associazione (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paziente_id INT NOT NULL,
    tutore_id INT NOT NULL,
    relazione VARCHAR(50) NOT NULL,
    stato VARCHAR(20) NOT NULL DEFAULT 'IN_ATTESA',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    gestita_da INT NULL,
    data_gestione DATETIME NULL,
    motivo_rifiuto TEXT NULL,

    FOREIGN KEY (paziente_id) REFERENCES pazienti(id) ON DELETE CASCADE,
    FOREIGN KEY (tutore_id) REFERENCES tutori_legali(id) ON DELETE CASCADE,
    FOREIGN KEY (gestita_da) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_richieste_stato (stato),
    INDEX idx_richieste_paziente_tutore (paziente_id, tutore_id)
);
