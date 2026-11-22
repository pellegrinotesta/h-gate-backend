-- ============================================================
-- MIGRAZIONE 2026-11-22: registro accessi e storico dei consensi
--
-- Da eseguire UNA VOLTA sui database esistenti (dopo 2026-11-22_approvazioni.sql).
-- Le nuove installazioni la includono già in schema.sql.
-- ============================================================

-- Storico dei consensi al trattamento dei dati dei minori: ogni concessione/revoca è una riga nuova.
CREATE TABLE IF NOT EXISTS consensi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    paziente_id INT NOT NULL,
    user_id INT NULL,                         -- tutore che ha espresso il consenso / la revoca
    tipo VARCHAR(40) NOT NULL,                -- PRIVACY_SANITARIA
    concesso BOOLEAN NOT NULL,
    versione_informativa VARCHAR(30) NOT NULL,
    data_evento DATETIME NOT NULL,
    ip_address VARCHAR(45) NULL,
    note TEXT NULL,

    FOREIGN KEY (paziente_id) REFERENCES pazienti(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_consensi_paziente (paziente_id, tipo, data_evento)
);

-- Registro degli accessi ai dati clinici (GDPR / linee guida Garante sul dossier sanitario).
-- Nessuna chiave esterna: il registro deve restare integro anche se utenti o pazienti vengono eliminati.
-- In produzione è consigliabile concedere all'utente applicativo solo INSERT e SELECT su questa tabella.
CREATE TABLE IF NOT EXISTS audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_evento DATETIME NOT NULL,
    user_id INT NULL,
    user_email VARCHAR(255) NULL,
    ruoli VARCHAR(100) NULL,
    azione VARCHAR(30) NOT NULL,              -- LETTURA, CREAZIONE, MODIFICA, ELIMINAZIONE, DOWNLOAD, LOGIN...
    risorsa_tipo VARCHAR(40) NULL,            -- PAZIENTE, REFERTO, PRENOTAZIONE, ALLEGATO, ...
    risorsa_id INT NULL,
    paziente_id INT NULL,
    esito VARCHAR(10) NOT NULL,               -- OK, NEGATO, ERRORE
    http_status INT NULL,
    http_method VARCHAR(10) NULL,
    path VARCHAR(500) NULL,
    ip_address VARCHAR(45) NULL,
    user_agent VARCHAR(300) NULL,

    INDEX idx_audit_data (data_evento),
    INDEX idx_audit_paziente (paziente_id, data_evento),
    INDEX idx_audit_user (user_id, data_evento)
);

-- I consensi raccolti prima di questa versione non hanno storico: si registrano con versione
-- "PRECEDENTE", così ai tutori verrà chiesto di confermarli sull'informativa in vigore.
INSERT INTO consensi (paziente_id, user_id, tipo, concesso, versione_informativa, data_evento, note)
SELECT p.id,
       (SELECT t.user_id FROM pazienti_tutori pt JOIN tutori_legali t ON t.id = pt.tutore_id
         WHERE pt.paziente_id = p.id ORDER BY t.id LIMIT 1),
       'PRIVACY_SANITARIA', TRUE, 'PRECEDENTE',
       COALESCE(p.data_consenso, p.created_at, NOW()),
       'Consenso registrato prima dell''introduzione dello storico dei consensi'
FROM pazienti p
WHERE p.consenso_privacy = TRUE
  AND NOT EXISTS (SELECT 1 FROM consensi c WHERE c.paziente_id = p.id);
