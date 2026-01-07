CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `email` VARCHAR(255) UNIQUE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `nome` VARCHAR(100) NOT NULL,
    `cognome` VARCHAR(100) NOT NULL,
    `telefono` VARCHAR(20) NULL,
    `data_nascita` DATE NULL,
    `indirizzo` TEXT NULL,
    `citta` VARCHAR(100) NULL,
    `provincia` VARCHAR(2) NULL,
    `cap` VARCHAR(5) NULL,
    `is_active` BOOLEAN DEFAULT TRUE,
    `is_verified` BOOLEAN DEFAULT FALSE,
    `ultimo_accesso` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_email` (`email`),
    INDEX `idx_uuid` (`uuid`),
    INDEX `idx_is_active` (`is_active`),
    INDEX `idx_created_at` (`created_at`)
);

CREATE TABLE `roles` (
  `user_id` INT NOT NULL,
  `role` varchar(255),

  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `password_reset_token` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `token` varchar(255),
  `user_id` INT NOT NULL,
  `expiry_date` datetime,

   FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

CREATE TABLE `medici` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `specializzazione` VARCHAR(100) NOT NULL,
    `numero_albo` VARCHAR(50) UNIQUE NOT NULL,
    `universita` VARCHAR(200) NULL,
    `anno_laurea` INT NULL,
    `bio` TEXT NULL,
    `curriculum` TEXT NULL,
    `tariffe` JSON NULL,
    `orari_disponibilita` JSON NULL,
    `durata_visita_minuti` INT DEFAULT 30,
    `pausa_tra_visite_minuti` INT DEFAULT 5,
    `anticipo_prenotazione_giorni` INT DEFAULT 30,
    `is_disponibile` BOOLEAN DEFAULT TRUE,
    `is_verificato` BOOLEAN DEFAULT FALSE,
    `data_verifica` DATETIME NULL,
    `verificato_da` INT NULL,
    `rating_medio` DECIMAL(3,2) DEFAULT 0.00,
    `numero_recensioni` INT DEFAULT 0,
    `numero_pazienti` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`verificato_da`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    INDEX `idx_specializzazione` (`specializzazione`),
    INDEX `idx_numero_albo` (`numero_albo`),
    INDEX `idx_is_disponibile` (`is_disponibile`),
    INDEX `idx_rating` (`rating_medio` DESC),
    INDEX `idx_verificato` (`is_verificato`),

    CHECK (`rating_medio` BETWEEN 0 AND 5),
    CHECK (`durata_visita_minuti` BETWEEN 10 AND 120)
);

CREATE TABLE `pazienti` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `nome` VARCHAR(100) NOT NULL,
    `cognome` VARCHAR(100) NOT NULL,
    `sesso` ENUM('M', 'F') NULL,
    `data_nascita` DATE NULL,
    `citta` VARCHAR(100) NULL,
    `codice_fiscale` VARCHAR(16) UNIQUE NOT NULL,
    `gruppo_sanguigno` ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', '0+', '0-') NULL,
    `altezza_cm` INT NULL,
    `peso_kg` DECIMAL(5,2) NULL,
    `allergie` TEXT NULL,
    `patologie_croniche` TEXT NULL,
    `note_mediche` TEXT NULL,
    `medico_id` INT NULL,
    `consenso_privacy` BOOLEAN DEFAULT FALSE,
    `consenso_marketing` BOOLEAN DEFAULT FALSE,
    `data_consenso` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE SET NULL,

    INDEX `idx_codice_fiscale` (`codice_fiscale`),
    INDEX `idx_medico` (`medico_id`),

    CHECK (CHAR_LENGTH(`codice_fiscale`) = 16),
    CHECK (`altezza_cm` BETWEEN 50 AND 250 OR `altezza_cm` IS NULL),
    CHECK (`peso_kg` BETWEEN 2 AND 300 OR `peso_kg` IS NULL)
);

CREATE TABLE tutori_legali (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE pazienti_tutori (
    paziente_id INT NOT NULL,
    tutore_id INT NOT NULL,
    relazione VARCHAR(50) NOT NULL,
    PRIMARY KEY (paziente_id, tutore_id),
    FOREIGN KEY (paziente_id) REFERENCES pazienti(id) ON DELETE CASCADE,
    FOREIGN KEY (tutore_id) REFERENCES tutori_legali(id) ON DELETE CASCADE
);

CREATE TABLE percorsi_terapeutici (
    id SERIAL PRIMARY KEY,
    paziente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL,
    titolo VARCHAR(200) NOT NULL,
    obiettivi TEXT,
    data_inizio DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_fine_prevista DATE,
    stato VARCHAR(20) DEFAULT 'ATTIVO', -- ATTIVO, CONCLUSO, SOSPESO
    numero_sedute_previste INTEGER,
    numero_sedute_effettuate INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (paziente_id) REFERENCES pazienti(id) ON DELETE CASCADE,
    FOREIGN KEY (medico_id) REFERENCES medici(id)
);

CREATE INDEX idx_percorsi_paziente ON percorsi_terapeutici(paziente_id);

CREATE TABLE valutazioni_psicologiche (
    id SERIAL PRIMARY KEY,
    paziente_id INTEGER NOT NULL,
    medico_id INTEGER NOT NULL,
    data_valutazione DATETIME DEFAULT CURRENT_TIMESTAMP,
    tipo_test VARCHAR(100) NOT NULL, -- es: WISC-IV, CARS-2
    punteggi JSON,
    interpretazione TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (paziente_id) REFERENCES pazienti(id) ON DELETE CASCADE,
    FOREIGN KEY (medico_id) REFERENCES medici(id)
);

CREATE TABLE `amministratori` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `livello_accesso` INT DEFAULT 1,
    `dipartimento` VARCHAR(100) NULL,
    `permessi` JSON NULL,
    `note` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,

    CHECK (`livello_accesso` BETWEEN 1 AND 10)
);

CREATE TABLE `prenotazioni` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `numero_prenotazione` VARCHAR(20) UNIQUE NOT NULL,
    `paziente_id` INT NOT NULL,
    `medico_id` INT NOT NULL,
    `data_ora` DATETIME NOT NULL,
    `data_ora_fine` DATETIME NOT NULL,
    `tipo_visita` VARCHAR(100) NOT NULL,
    `stato` ENUM('IN_ATTESA', 'CONFERMATA', 'COMPLETATA', 'ANNULLATA', 'NON_PRESENTATO') DEFAULT 'IN_ATTESA',
    `costo` DECIMAL(10,2) NOT NULL,
    `note_paziente` TEXT NULL,
    `note_medico` TEXT NULL,
    `motivo_annullamento` TEXT NULL,
    `annullata_da` INT NULL,
    `data_annullamento` DATETIME NULL,
    `promemoria_inviato` BOOLEAN DEFAULT FALSE,
    `conferma_inviata` BOOLEAN DEFAULT FALSE,
    `is_prima_visita` BOOLEAN DEFAULT FALSE,
    `is_urgente` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`paziente_id`) REFERENCES `pazienti`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`annullata_da`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    INDEX `idx_paziente` (`paziente_id`),
    INDEX `idx_medico` (`medico_id`),
    INDEX `idx_data_ora` (`data_ora`),
    INDEX `idx_stato` (`stato`),
    INDEX `idx_numero` (`numero_prenotazione`),
    INDEX `idx_uuid` (`uuid`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_medico_data` (`medico_id`, `data_ora`, `stato`),

    CHECK (`data_ora_fine` > `data_ora`),
    CHECK (`costo` >= 0)
);

CREATE TABLE `referti` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `prenotazione_id` INT UNIQUE NOT NULL,
    `medico_id` INT NOT NULL,
    `paziente_id` INT NOT NULL,
    `data_emissione` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `tipo_referto` VARCHAR(100) NOT NULL,
    `titolo` VARCHAR(200) NOT NULL,
    `anamnesi` TEXT NULL,
    `esame_obiettivo` TEXT NULL,
    `diagnosi` TEXT NOT NULL,
    `terapia` TEXT NULL,
    `prescrizioni` TEXT NULL,
    `note_mediche` TEXT NULL,
    `parametri_vitali` JSON NULL,
    `esami_richiesti` TEXT NULL,
    `prossimo_controllo` DATE NULL,
    `is_firmato` BOOLEAN DEFAULT FALSE,
    `firma_digitale` TEXT NULL,
    `data_firma` DATETIME NULL,
    `is_inviato_paziente` BOOLEAN DEFAULT FALSE,
    `data_invio_paziente` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`prenotazione_id`) REFERENCES `prenotazioni`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE RESTRICT,
    FOREIGN KEY (`paziente_id`) REFERENCES `pazienti`(`id`) ON DELETE CASCADE,

    INDEX `idx_prenotazione` (`prenotazione_id`),
    INDEX `idx_medico` (`medico_id`),
    INDEX `idx_paziente` (`paziente_id`),
    INDEX `idx_data_emissione` (`data_emissione`),
    INDEX `idx_tipo` (`tipo_referto`),
    INDEX `idx_uuid` (`uuid`)
);

CREATE TABLE `allegati` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `referto_id` INT NOT NULL,
    `nome_file` VARCHAR(255) NOT NULL,
    `tipo_file` ENUM('PDF', 'JPG', 'JPEG', 'PNG', 'DICOM', 'TXT') NOT NULL,
    `mime_type` VARCHAR(100) NOT NULL,
    `size_bytes` BIGINT NOT NULL,
    `url` VARCHAR(500) NOT NULL,
    `storage_path` VARCHAR(500) NULL,
    `descrizione` TEXT NULL,
    `hash_file` VARCHAR(64) NULL,
    `uploaded_by` INT NULL,
    `uploaded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`referto_id`) REFERENCES `referti`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`uploaded_by`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    INDEX `idx_referto` (`referto_id`),
    INDEX `idx_tipo` (`tipo_file`),
    INDEX `idx_uploaded_at` (`uploaded_at`),

    CHECK (`size_bytes` > 0),
    CHECK (`size_bytes` <= 10485760)
);
CREATE TABLE `recensioni` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `prenotazione_id` INT UNIQUE NOT NULL,
    `paziente_id` INT NOT NULL,
    `medico_id` INT NOT NULL,
    `rating` INT NOT NULL,
    `titolo` VARCHAR(200) NULL,
    `commento` TEXT NULL,
    `is_anonima` BOOLEAN DEFAULT FALSE,
    `is_verificata` BOOLEAN DEFAULT FALSE,
    `is_pubblicata` BOOLEAN DEFAULT TRUE,
    `risposta_medico` TEXT NULL,
    `data_risposta` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`prenotazione_id`) REFERENCES `prenotazioni`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`paziente_id`) REFERENCES `pazienti`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE CASCADE,

    INDEX `idx_paziente` (`paziente_id`),
    INDEX `idx_medico` (`medico_id`),
    INDEX `idx_rating` (`rating`),
    INDEX `idx_pubblicata` (`is_pubblicata`),

    CHECK (`rating` BETWEEN 1 AND 5)
);

CREATE TABLE `notifiche` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NOT NULL,
    `tipo` ENUM('PRENOTAZIONE_CONFERMATA', 'PRENOTAZIONE_MODIFICATA', 'PRENOTAZIONE_ANNULLATA',
              'REFERTO_DISPONIBILE', 'PROMEMORIA_VISITA', 'SISTEMA') NOT NULL,
    `titolo` VARCHAR(200) NOT NULL,
    `messaggio` TEXT NOT NULL,
    `link` VARCHAR(500) NULL,
    `is_letta` BOOLEAN DEFAULT FALSE,
    `data_lettura` DATETIME NULL,
    `is_inviata_email` BOOLEAN DEFAULT FALSE,
    `data_invio_email` DATETIME NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,

    INDEX `idx_user` (`user_id`),
    INDEX `idx_tipo` (`tipo`),
    INDEX `idx_letta` (`is_letta`),
    INDEX `idx_created_at` (`created_at` DESC)
);

CREATE TABLE `disponibilita_medici` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `medico_id` INT NOT NULL,
    `giorno_settimana` TINYINT NOT NULL COMMENT '0=Domenica, 1=Lunedì, ..., 6=Sabato',
    `ora_inizio` TIME NOT NULL,
    `ora_fine` TIME NOT NULL,
    `is_attiva` BOOLEAN DEFAULT TRUE,
    `note` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE CASCADE,

    INDEX `idx_medico` (`medico_id`),
    INDEX `idx_giorno` (`giorno_settimana`),

    CHECK (`giorno_settimana` BETWEEN 0 AND 6),
    CHECK (`ora_fine` > `ora_inizio`)
);

CREATE TABLE `eccezioni_disponibilita` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `medico_id` INT NOT NULL,
    `data_inizio` DATE NOT NULL,
    `data_fine` DATE NOT NULL,
    `motivo` VARCHAR(200) NULL,
    `descrizione` TEXT NULL,
    `is_ricorrente` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE CASCADE,

    INDEX `idx_medico` (`medico_id`),
    INDEX `idx_date` (`data_inizio`, `data_fine`),

    CHECK (`data_fine` >= `data_inizio`)
);

CREATE TABLE `audit_log` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT NULL,
    `azione` VARCHAR(100) NOT NULL,
    `tabella` VARCHAR(100) NULL,
    `record_id` INT NULL,
    `dati_prima` JSON NULL,
    `dati_dopo` JSON NULL,
    `ip_address` VARCHAR(45) NULL,
    `user_agent` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL,

    INDEX `idx_user` (`user_id`),
    INDEX `idx_azione` (`azione`),
    INDEX `idx_tabella` (`tabella`),
    INDEX `idx_created_at` (`created_at` DESC)
);

CREATE TABLE `statistiche_giornaliere` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `data` DATE UNIQUE NOT NULL,
    `totale_prenotazioni` INT DEFAULT 0,
    `prenotazioni_confermate` INT DEFAULT 0,
    `prenotazioni_completate` INT DEFAULT 0,
    `prenotazioni_annullate` INT DEFAULT 0,
    `nuovi_pazienti` INT DEFAULT 0,
    `referti_emessi` INT DEFAULT 0,
    `fatturato_giornaliero` DECIMAL(10,2) DEFAULT 0,
    `medici_attivi` INT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX `idx_data` (`data` DESC)
);

-- ============================================================
-- VISTE (VIEWS)
-- ============================================================

-- ============================================================
-- VISTA: v_pazienti_completi
-- Pazienti (bambini) con informazioni tutore principale
-- ============================================================
DROP VIEW IF EXISTS `v_pazienti_completi`;

CREATE VIEW `v_pazienti_completi` AS
SELECT
    -- Dati Paziente (bambino)
    p.id AS paziente_id,
    p.nome AS paziente_nome,
    p.cognome AS paziente_cognome,
    p.codice_fiscale,
    p.data_nascita,
    p.sesso,
    p.citta,
    TIMESTAMPDIFF(YEAR, p.data_nascita, CURDATE()) AS eta_anni,
    TIMESTAMPDIFF(MONTH, p.data_nascita, CURDATE()) % 12 AS eta_mesi,
    p.gruppo_sanguigno,
    p.altezza_cm,
    p.peso_kg,
    p.allergie,
    p.patologie_croniche,
    p.note_mediche,
    p.medico_id,
    p.consenso_privacy,
    p.consenso_marketing,
    p.created_at,

    -- Dati Tutore Principale
    u.id AS tutore_user_id,
    u.uuid AS tutore_uuid,
    u.email AS tutore_email,
    u.nome AS tutore_nome,
    u.cognome AS tutore_cognome,
    u.telefono AS tutore_telefono,
    u.indirizzo AS tutore_indirizzo,
    u.citta AS tutore_citta,
    u.provincia AS tutore_provincia,
    pt.relazione AS tutore_relazione,

    -- Conteggi
    (SELECT COUNT(*) FROM pazienti_tutori pt2 WHERE pt2.paziente_id = p.id) AS numero_tutori,
    (SELECT COUNT(*) FROM percorsi_terapeutici perc WHERE perc.paziente_id = p.id) AS numero_percorsi,
    (SELECT COUNT(*) FROM prenotazioni pr WHERE pr.paziente_id = p.id) AS numero_prenotazioni

FROM pazienti p
LEFT JOIN pazienti_tutori pt ON p.id = pt.paziente_id
LEFT JOIN tutori_legali tl ON pt.tutore_id = tl.id
LEFT JOIN users u ON tl.user_id = u.id;

-- ============================================================
-- VISTA: v_medici_completi
-- Specialisti con tutte le informazioni
-- ============================================================
DROP VIEW IF EXISTS `v_medici_completi`;

CREATE VIEW `v_medici_completi` AS
SELECT
    -- Dati User
    u.id AS user_id,
    u.uuid,
    u.email,
    u.nome,
    u.cognome,
    u.telefono,
    u.data_nascita,
    u.indirizzo,
    u.citta,
    u.provincia,
    u.cap,
    u.is_active,
    u.is_verified,
    u.created_at,

    -- Dati Medico
    m.id AS medico_id,
    m.specializzazione,
    m.numero_albo,
    m.universita,
    m.anno_laurea,
    m.bio,
    m.curriculum,
    m.tariffe,
    m.orari_disponibilita,
    m.durata_visita_minuti,
    m.pausa_tra_visite_minuti,
    m.anticipo_prenotazione_giorni,
    m.is_disponibile,
    m.is_verificato,
    m.data_verifica,
    m.rating_medio,
    m.numero_recensioni,
    m.numero_pazienti,

    -- Statistiche
    (SELECT COUNT(*) FROM prenotazioni pr WHERE pr.medico_id = m.id) AS totale_prenotazioni,
    (SELECT COUNT(*) FROM prenotazioni pr WHERE pr.medico_id = m.id AND pr.stato = 'COMPLETATA') AS prenotazioni_completate,
    (SELECT COUNT(*) FROM disponibilita_medici dm WHERE dm.medico_id = m.id AND dm.is_attiva = TRUE) AS fasce_disponibilita

FROM users u
INNER JOIN medici m ON u.id = m.user_id
INNER JOIN roles r ON r.user_id = u.id
WHERE r.role = 'MEDICO';

-- ============================================================
-- VISTA: v_prenotazioni_dettagliate
-- Appuntamenti con info complete paziente, tutore e medico
-- ============================================================
DROP VIEW IF EXISTS `v_prenotazioni_dettagliate`;

CREATE VIEW `v_prenotazioni_dettagliate` AS
SELECT
    -- Dati Prenotazione
    pr.id,
    pr.uuid,
    pr.numero_prenotazione,
    pr.data_ora,
    pr.data_ora_fine,
    pr.tipo_visita,
    pr.stato,
    pr.costo,
    pr.note_paziente,
    pr.note_medico,
    pr.is_prima_visita,
    pr.is_urgente,
    pr.promemoria_inviato,
    pr.conferma_inviata,
    pr.created_at,
    pr.updated_at,

    -- Dati Paziente (bambino)
    p.id AS paziente_id,
    CONCAT(p.nome, ' ', p.cognome) AS paziente_nome_completo,
    p.nome AS paziente_nome,
    p.cognome AS paziente_cognome,
    p.codice_fiscale AS paziente_cf,
    p.data_nascita AS paziente_data_nascita,
    TIMESTAMPDIFF(YEAR, p.data_nascita, CURDATE()) AS paziente_eta,
    p.sesso AS paziente_sesso,
    p.patologie_croniche AS paziente_patologie,

    -- Dati Tutore (chi ha prenotato)
    ut.id AS tutore_user_id,
    ut.email AS tutore_email,
    CONCAT(ut.nome, ' ', ut.cognome) AS tutore_nome_completo,
    ut.nome AS tutore_nome,
    ut.cognome AS tutore_cognome,
    ut.telefono AS tutore_telefono,
    pt.relazione AS tutore_relazione,

    -- Dati Medico/Specialista
    m.id AS medico_id,
    um.id AS medico_user_id,
    CONCAT(um.nome, ' ', um.cognome) AS medico_nome_completo,
    um.nome AS medico_nome,
    um.cognome AS medico_cognome,
    um.email AS medico_email,
    um.telefono AS medico_telefono,
    m.specializzazione AS medico_specializzazione,
    m.numero_albo AS medico_numero_albo,
    m.rating_medio AS medico_rating,
    m.durata_visita_minuti AS medico_durata_visita,

    -- Flag utili
    CASE WHEN pr.data_ora > NOW() THEN 1 ELSE 0 END AS is_futura,
    CASE WHEN DATE(pr.data_ora) = CURDATE() THEN 1 ELSE 0 END AS is_oggi,
    DATEDIFF(pr.data_ora, NOW()) AS giorni_mancanti

FROM prenotazioni pr
INNER JOIN pazienti p ON pr.paziente_id = p.id
LEFT JOIN pazienti_tutori pt ON p.id = pt.paziente_id
LEFT JOIN tutori_legali tl ON pt.tutore_id = tl.id
LEFT JOIN users ut ON tl.user_id = ut.id
INNER JOIN medici m ON pr.medico_id = m.id
INNER JOIN users um ON m.user_id = um.id;

-- ============================================================
-- VISTA: v_tutori_completi
-- Vista dei tutori con i loro pazienti
-- ============================================================
DROP VIEW IF EXISTS `v_tutori_completi`;

CREATE VIEW `v_tutori_completi` AS
SELECT
    -- Dati User/Tutore
    u.id AS user_id,
    u.uuid,
    u.email,
    u.nome,
    u.cognome,
    u.telefono,
    u.indirizzo,
    u.citta,
    u.provincia,
    u.is_active,
    u.created_at,

    -- Dati Tutore Legale
    tl.id AS tutore_id,

    -- Statistiche
    (SELECT COUNT(*) FROM pazienti_tutori pt WHERE pt.tutore_id = tl.id) AS numero_pazienti,
    (SELECT COUNT(*)
     FROM prenotazioni pr
     INNER JOIN pazienti_tutori pt ON pr.paziente_id = pt.paziente_id
     WHERE pt.tutore_id = tl.id) AS totale_prenotazioni,
    (SELECT COUNT(*)
     FROM prenotazioni pr
     INNER JOIN pazienti_tutori pt ON pr.paziente_id = pt.paziente_id
     WHERE pt.tutore_id = tl.id
     AND pr.data_ora > NOW()) AS prenotazioni_future

FROM users u
INNER JOIN tutori_legali tl ON u.id = tl.user_id
INNER JOIN roles r ON r.user_id = u.id
WHERE r.role = 'TUTORE';

-- ============================================================
-- VISTA: v_percorsi_attivi
-- Percorsi terapeutici in corso con dettagli
-- ============================================================
DROP VIEW IF EXISTS `v_percorsi_attivi`;

CREATE VIEW `v_percorsi_attivi` AS
SELECT
    -- Dati Percorso
    pt.id AS percorso_id,
    pt.titolo,
    pt.obiettivi,
    pt.stato,
    pt.data_inizio,
    pt.data_fine_prevista,
    pt.numero_sedute_previste,
    pt.numero_sedute_effettuate,
    CASE
        WHEN pt.numero_sedute_previste > 0
        THEN ROUND((pt.numero_sedute_effettuate / pt.numero_sedute_previste * 100), 2)
        ELSE 0
    END AS percentuale_completamento,

    -- Dati Paziente
    p.id AS paziente_id,
    CONCAT(p.nome, ' ', p.cognome) AS paziente_nome,
    p.data_nascita AS paziente_data_nascita,
    TIMESTAMPDIFF(YEAR, p.data_nascita, CURDATE()) AS paziente_eta,

    -- Dati Medico Referente
    m.id AS medico_id,
    CONCAT(u.nome, ' ', u.cognome) AS medico_nome,
    m.specializzazione AS medico_specializzazione,

    -- Prossimo Appuntamento
    (SELECT MIN(pr.data_ora)
     FROM prenotazioni pr
     WHERE pr.paziente_id = p.id
     AND pr.medico_id = m.id
     AND pr.stato IN ('CONFERMATA', 'IN_ATTESA')
     AND pr.data_ora > NOW()) AS prossimo_appuntamento

FROM percorsi_terapeutici pt
INNER JOIN pazienti p ON pt.paziente_id = p.id
INNER JOIN medici m ON pt.medico_id = m.id
INNER JOIN users u ON m.user_id = u.id
WHERE pt.stato = 'ATTIVO';

-- ============================================================
-- VISTA: v_dashboard_statistiche
-- Statistiche generali per la dashboard
-- ============================================================
DROP VIEW IF EXISTS `v_dashboard_statistiche`;

CREATE VIEW `v_dashboard_statistiche` AS
SELECT
    -- Pazienti
    (SELECT COUNT(*) FROM pazienti) AS totale_pazienti,
    (SELECT COUNT(*) FROM pazienti WHERE consenso_privacy = TRUE) AS pazienti_consenso_attivo,
    (SELECT COUNT(DISTINCT paziente_id) FROM percorsi_terapeutici WHERE stato = 'ATTIVO') AS pazienti_in_terapia,

    -- Specialisti
    (SELECT COUNT(*) FROM medici WHERE is_disponibile = TRUE) AS medici_disponibili,
    (SELECT COUNT(*) FROM medici WHERE is_verificato = TRUE) AS medici_verificati,

    -- Tutori
    (SELECT COUNT(*) FROM tutori_legali) AS totale_tutori,

    -- Prenotazioni
    (SELECT COUNT(*) FROM prenotazioni WHERE DATE(data_ora) = CURDATE()) AS prenotazioni_oggi,
    (SELECT COUNT(*) FROM prenotazioni WHERE data_ora BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY)) AS prenotazioni_prossimi_7_giorni,
    (SELECT COUNT(*) FROM prenotazioni WHERE stato = 'IN_ATTESA') AS prenotazioni_da_confermare,
    (SELECT COUNT(*) FROM prenotazioni WHERE stato = 'COMPLETATA' AND MONTH(data_ora) = MONTH(CURDATE())) AS prenotazioni_completate_mese,

    -- Percorsi
    (SELECT COUNT(*) FROM percorsi_terapeutici WHERE stato = 'ATTIVO') AS percorsi_attivi,
    (SELECT COUNT(*) FROM percorsi_terapeutici WHERE stato = 'IN_VALUTAZIONE') AS percorsi_in_valutazione,

    -- Referti
    (SELECT COUNT(*) FROM referti WHERE MONTH(data_emissione) = MONTH(CURDATE())) AS referti_emessi_mese,
    (SELECT COUNT(*) FROM referti WHERE is_firmato = FALSE) AS referti_da_firmare,

    -- Notifiche
    (SELECT COUNT(*) FROM notifiche WHERE is_letta = FALSE) AS notifiche_non_lette;

-- ============================================================
-- VISTA: v_agenda_giornaliera
-- Appuntamenti del giorno per tutti i medici
-- ============================================================
DROP VIEW IF EXISTS `v_agenda_giornaliera`;

CREATE VIEW `v_agenda_giornaliera` AS
SELECT
    DATE(pr.data_ora) AS data,
    TIME(pr.data_ora) AS ora_inizio,
    TIME(pr.data_ora_fine) AS ora_fine,

    -- Medico
    m.id AS medico_id,
    CONCAT(um.nome, ' ', um.cognome) AS medico_nome,
    m.specializzazione,

    -- Paziente
    p.id AS paziente_id,
    CONCAT(p.nome, ' ', p.cognome) AS paziente_nome,
    TIMESTAMPDIFF(YEAR, p.data_nascita, CURDATE()) AS paziente_eta,

    -- Tutore
    CONCAT(ut.nome, ' ', ut.cognome) AS tutore_nome,
    ut.telefono AS tutore_telefono,

    -- Prenotazione
    pr.tipo_visita,
    pr.stato,
    pr.is_prima_visita,
    pr.note_paziente,

    -- Flags
    CASE WHEN pr.conferma_inviata = TRUE THEN 'Confermato' ELSE 'Da confermare' END AS stato_conferma,
    CASE WHEN pr.promemoria_inviato = TRUE THEN 'Inviato' ELSE 'Da inviare' END AS stato_promemoria

FROM prenotazioni pr
INNER JOIN medici m ON pr.medico_id = m.id
INNER JOIN users um ON m.user_id = um.id
INNER JOIN pazienti p ON pr.paziente_id = p.id
LEFT JOIN pazienti_tutori pt ON p.id = pt.paziente_id
LEFT JOIN tutori_legali tl ON pt.tutore_id = tl.id
LEFT JOIN users ut ON tl.user_id = ut.id
WHERE DATE(pr.data_ora) = CURDATE()
AND pr.stato IN ('CONFERMATA', 'IN_ATTESA')
ORDER BY pr.data_ora;