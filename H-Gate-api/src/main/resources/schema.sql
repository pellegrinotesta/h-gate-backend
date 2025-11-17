CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `email` VARCHAR(255) UNIQUE NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
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
    `user_id` INT NOT NULL,
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

    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`medico_id`) REFERENCES `medici`(`id`) ON DELETE SET NULL,

    INDEX `idx_codice_fiscale` (`codice_fiscale`),
    INDEX `idx_medico` (`medico_id`),

    CHECK (CHAR_LENGTH(`codice_fiscale`) = 16),
    CHECK (`altezza_cm` BETWEEN 50 AND 250 OR `altezza_cm` IS NULL),
    CHECK (`peso_kg` BETWEEN 2 AND 300 OR `peso_kg` IS NULL)
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

CREATE TABLE `pagamenti` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `uuid` CHAR(36) UNIQUE NOT NULL DEFAULT (UUID()),
    `prenotazione_id` INT NOT NULL,
    `paziente_id` INT NOT NULL,
    `importo` DECIMAL(10,2) NOT NULL,
    `metodo_pagamento` VARCHAR(50) NULL,
    `stato` ENUM('IN_ATTESA', 'COMPLETATO', 'RIMBORSATO', 'FALLITO') DEFAULT 'IN_ATTESA',
    `transaction_id` VARCHAR(100) NULL,
    `payment_gateway` VARCHAR(50) NULL,
    `data_pagamento` DATETIME NULL,
    `data_scadenza` DATETIME NULL,
    `note` TEXT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (`prenotazione_id`) REFERENCES `prenotazioni`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`paziente_id`) REFERENCES `pazienti`(`id`) ON DELETE CASCADE,

    INDEX `idx_prenotazione` (`prenotazione_id`),
    INDEX `idx_paziente` (`paziente_id`),
    INDEX `idx_stato` (`stato`),
    INDEX `idx_data` (`data_pagamento`),

    CHECK (`importo` > 0)
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

CREATE OR REPLACE VIEW v_pazienti_completi AS
SELECT
    u.id,
    u.uuid,
    u.email,
    u.nome,
    u.cognome,
    u.telefono,
    u.data_nascita,
    u.indirizzo,
    u.citta,
    u.is_active,
    p.codice_fiscale,
    p.gruppo_sanguigno,
    p.allergie,
    p.patologie_croniche,
    p.medico_id,
    u.created_at
FROM users u
INNER JOIN pazienti p ON u.id = p.user_id
INNER JOIN roles r ON r.user_id = u.id
WHERE r.role = 'PAZIENTE';

CREATE OR REPLACE VIEW `v_medici_completi` AS
SELECT
    `u`.`id`,
    `u`.`uuid`,
    `u`.`email`,
    `u`.`nome`,
    `u`.`cognome`,
    `u`.`telefono`,
    `u`.`indirizzo`,
    `u`.`citta`,
    `u`.`is_active`,
    `m`.`specializzazione`,
    `m`.`numero_albo`,
    `m`.`bio`,
    `m`.`tariffe`,
    `m`.`orari_disponibilita`,
    `m`.`rating_medio`,
    `m`.`numero_recensioni`,
    `m`.`is_disponibile`,
    `m`.`is_verificato`,
    `u`.`created_at`
FROM `users` `u`
INNER JOIN `medici` `m` ON `u`.`id` = `m`.`id`
INNER JOIN roles r ON r.user_id = u.id
WHERE r.role = 'MEDICO';

CREATE OR REPLACE VIEW `v_prenotazioni_dettagliate` AS
SELECT
    `pr`.`id`,
    `pr`.`uuid`,
    `pr`.`numero_prenotazione`,
    `pr`.`data_ora`,
    `pr`.`data_ora_fine`,
    `pr`.`tipo_visita`,
    `pr`.`stato`,
    `pr`.`costo`,
    CONCAT(`up`.`nome`, ' ', `up`.`cognome`) AS `paziente_nome`,
    `up`.`email` AS `paziente_email`,
    `p`.`codice_fiscale` AS `paziente_cf`,
    CONCAT(`um`.`nome`, ' ', `um`.`cognome`) AS `medico_nome`,
    `um`.`email` AS `medico_email`,
    `m`.`specializzazione` AS `medico_specializzazione`,
    `m`.`rating_medio` AS `medico_rating`,
    `pr`.`created_at`
FROM `prenotazioni` `pr`
INNER JOIN `pazienti` `p` ON `pr`.`paziente_id` = `p`.`id`
INNER JOIN `users` `up` ON `p`.`user_id` = `up`.`id`
INNER JOIN `medici` `m` ON `pr`.`medico_id` = `m`.`id`
INNER JOIN `users` `um` ON `m`.`user_id` = `um`.`id`;
