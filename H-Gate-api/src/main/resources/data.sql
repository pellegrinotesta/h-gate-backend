-- ============================================================
-- DATI DI ESEMPIO - NEUROPSICHIATRIA INFANTILE
-- Sistema completo con utenti, pazienti, specialisti, tutori
-- ============================================================

-- NOTA: Password hash per '123456' (bcrypt)
-- Puoi generare nuovi hash con: SELECT crypt('tuapassword', gen_salt('bf'));

-- ============================================================
-- 1. UTENTI (Tabella users)
-- ============================================================

INSERT INTO `users` (`uuid`, `email`, `password`, `nome`, `cognome`, `telefono`, `data_nascita`, `citta`, `provincia`, `is_active`, `is_verified`)
VALUES
-- Admin
(UUID(), 'admin@neuropsychkids.it', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Admin', 'Sistema', '0612345678', '1980-01-01', 'Roma', 'RM', TRUE, TRUE),

-- Medici/Specialisti
(UUID(), 'mario.rossi@npi.it', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Mario', 'Rossi', '3451234567', '1975-05-15', 'Milano', 'MI', TRUE, TRUE),
(UUID(), 'laura.ferrari@npi.it', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Laura', 'Ferrari', '3479876543', '1982-09-22', 'Roma', 'RM', TRUE, TRUE),
(UUID(), 'giulia.bianchi@npi.it', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Giulia', 'Bianchi', '3391122334', '1985-03-10', 'Milano', 'MI', TRUE, TRUE),

-- Tutori (Genitori)
(UUID(), 'anna.verdi@email.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Anna', 'Verdi', '3401234567', '1985-07-12', 'Milano', 'MI', TRUE, TRUE),
(UUID(), 'paolo.verdi@email.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Paolo', 'Verdi', '3407654321', '1983-04-20', 'Milano', 'MI', TRUE, TRUE),
(UUID(), 'claudia.marino@email.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Claudia', 'Marino', '3339988776', '1988-12-05', 'Roma', 'RM', TRUE, TRUE),
(UUID(), 'stefano.colombo@email.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Stefano', 'Colombo', '3456677889', '1980-06-18', 'Milano', 'MI', TRUE, TRUE);

-- ============================================================
-- 2. RUOLI (Tabella roles)
-- ============================================================

INSERT INTO `roles` (`user_id`, `role`)
VALUES
((SELECT id FROM users WHERE email = 'admin@neuropsychkids.it'), 'ADMIN'),
((SELECT id FROM users WHERE email = 'mario.rossi@npi.it'), 'MEDICO'),
((SELECT id FROM users WHERE email = 'laura.ferrari@npi.it'), 'MEDICO'),
((SELECT id FROM users WHERE email = 'giulia.bianchi@npi.it'), 'MEDICO'),
((SELECT id FROM users WHERE email = 'anna.verdi@email.com'), 'TUTORE'),
((SELECT id FROM users WHERE email = 'paolo.verdi@email.com'), 'TUTORE'),
((SELECT id FROM users WHERE email = 'claudia.marino@email.com'), 'TUTORE'),
((SELECT id FROM users WHERE email = 'stefano.colombo@email.com'), 'TUTORE');


-- ============================================================
-- 3. MEDICI
-- ============================================================

INSERT INTO `medici` (`user_id`, `specializzazione`, `numero_albo`, `universita`, `anno_laurea`, `bio`, `durata_visita_minuti`, `is_disponibile`, `is_verificato`, `rating_medio`, `numero_recensioni`)
VALUES
((SELECT id FROM users WHERE email = 'mario.rossi@npi.it'),
 'Neuropsichiatria Infantile', 'RM12345', 'Sapienza Roma', 2005,
 'Specialista in disturbi del neurosviluppo', 60, TRUE, TRUE, 4.8, 45),

((SELECT id FROM users WHERE email = 'laura.ferrari@npi.it'),
 'Psicologia Età Evolutiva', 'RM67890', 'Cattolica Milano', 2010,
 'Psicologa esperta in CBT per bambini', 50, TRUE, TRUE, 4.9, 38),

((SELECT id FROM users WHERE email = 'giulia.bianchi@npi.it'),
 'Logopedia', 'MI11223', 'Università Milano', 2012,
 'Logopedista disturbi del linguaggio', 45, TRUE, TRUE, 4.7, 52);

-- ============================================================
-- 4. PAZIENTI
-- ============================================================

INSERT INTO `pazienti` (`nome`, `cognome`, `codice_fiscale`, `data_nascita`, `sesso`, `gruppo_sanguigno`, `altezza_cm`, `peso_kg`, `allergie`, `patologie_croniche`, `consenso_privacy`)
VALUES
-- Marco Verdi (9 anni)
('Marco', 'Verdi', 'VRDMRC15A15F205X', '2015-01-15', 'M', 'A+', 145, 42.5,
 'Nessuna', 'ADHD', TRUE),

-- Sofia Marino (5 anni)
('Sofia', 'Marino', 'MRNSFO19D45H501Y', '2019-04-05', 'F', 'B+', 115, 20.0,
 'Pollini', NULL, TRUE),

-- Luca Colombo (14 anni)
('Luca', 'Colombo', 'CLMLCU10C10F205Z', '2010-03-10', 'M', '0+', 155, 48.0,
 'Lattosio', 'Disturbo Spettro Autistico', TRUE),

-- Emma Romano (10 anni)
('Emma', 'Romano', 'RMNMME14E55H501W', '2014-05-15', 'F', 'A-', 132, 28.5,
 'Nessuna', 'Disturbo ansia da separazione', TRUE);

-- ============================================================
-- 5. PRENOTAZIONI
-- ============================================================

INSERT INTO `prenotazioni` (`uuid`, `numero_prenotazione`, `paziente_id`, `medico_id`, `data_ora`, `data_ora_fine`, `tipo_visita`, `stato`, `costo`, `is_prima_visita`)
VALUES
(UUID(), 'NPI20250115001',
 (SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'),
 (SELECT id FROM medici WHERE numero_albo = 'RM12345'),
 '2025-01-15 09:00:00', '2025-01-15 10:00:00',
 'Controllo ADHD', 'CONFERMATA', 100.00, FALSE),

(UUID(), 'NPI20250116001',
 (SELECT id FROM pazienti WHERE codice_fiscale = 'MRNSFO19D45H501Y'),
 (SELECT id FROM medici WHERE numero_albo = 'MI11223'),
 '2025-01-16 10:30:00', '2025-01-16 11:15:00',
 'Seduta Logopedia', 'CONFERMATA', 60.00, FALSE);

-- ============================================================
-- 6. REFERTI
-- ============================================================

INSERT INTO referti (uuid, prenotazione_id, medico_id, paziente_id, data_emissione, tipo_referto, titolo, diagnosi, is_firmato)
VALUES
(UUID(), (SELECT id FROM prenotazioni WHERE numero_prenotazione = 'NPI20250115001'), (SELECT id FROM medici WHERE numero_albo = 'RM12345'), (SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'), NOW(), 'Controllo', 'Referto Marco Verdi', 'ADHD in miglioramento', TRUE);

-- ============================================================
-- 7. TUTORI LEGALI
-- ============================================================

INSERT INTO `tutori_legali` (`user_id`)
VALUES
((SELECT id FROM users WHERE email = 'anna.verdi@email.com')),
((SELECT id FROM users WHERE email = 'paolo.verdi@email.com')),
((SELECT id FROM users WHERE email = 'claudia.marino@email.com')),
((SELECT id FROM users WHERE email = 'stefano.colombo@email.com'));


-- ============================================================
-- 7. PAZIENTI TUTORI
-- ============================================================

INSERT INTO `pazienti_tutori` (`paziente_id`, `tutore_id`, `relazione`)
VALUES
-- Marco Verdi -> Anna (madre) e Paolo (padre)
((SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'),
 (SELECT id FROM tutori_legali WHERE user_id = (SELECT id FROM users WHERE email = 'anna.verdi@email.com')),
 'MADRE'),

((SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'),
 (SELECT id FROM tutori_legali WHERE user_id = (SELECT id FROM users WHERE email = 'paolo.verdi@email.com')),
 'PADRE'),

-- Sofia Marino -> Claudia (madre)
((SELECT id FROM pazienti WHERE codice_fiscale = 'MRNSFO19D45H501Y'),
 (SELECT id FROM tutori_legali WHERE user_id = (SELECT id FROM users WHERE email = 'claudia.marino@email.com')),
 'MADRE'),

-- Luca Colombo -> Stefano (padre)
((SELECT id FROM pazienti WHERE codice_fiscale = 'CLMLCU10C10F205Z'),
 (SELECT id FROM tutori_legali WHERE user_id = (SELECT id FROM users WHERE email = 'stefano.colombo@email.com')),
 'PADRE');

INSERT INTO tariffe_medici
(medico_id, tipo_visita, is_prima_visita, costo, durata_minuti)
VALUES
-- ======================
-- Mario Rossi – Neuropsichiatra
-- ======================
((SELECT id FROM medici WHERE numero_albo = 'RM12345'),
 'Prima visita Neuropsichiatrica', TRUE, 130.00, 90),

((SELECT id FROM medici WHERE numero_albo = 'RM12345'),
 'Visita di controllo Neuropsichiatrica', FALSE, 100.00, 60),

-- ======================
-- Laura Ferrari – Psicologa
-- ======================
((SELECT id FROM medici WHERE numero_albo = 'RM67890'),
 'Colloquio Psicologico Età Evolutiva', TRUE, 90.00, 60),

((SELECT id FROM medici WHERE numero_albo = 'RM67890'),
 'Colloquio Psicologico Età Evolutiva', FALSE, 80.00, 50),

-- ======================
-- Giulia Bianchi – Logopedista
-- ======================
((SELECT id FROM medici WHERE numero_albo = 'MI11223'),
 'Seduta di Logopedia', FALSE, 60.00, 45);


INSERT INTO disponibilita_medici
(medico_id, giorno_settimana, ora_inizio, ora_fine)
VALUES
-- Mario Rossi
((SELECT id FROM medici WHERE numero_albo = 'RM12345'), 1, '09:00', '13:00'),
((SELECT id FROM medici WHERE numero_albo = 'RM12345'), 3, '14:00', '18:00'),

-- Laura Ferrari
((SELECT id FROM medici WHERE numero_albo = 'RM67890'), 2, '09:00', '13:00'),
((SELECT id FROM medici WHERE numero_albo = 'RM67890'), 4, '14:00', '18:00'),

-- Giulia Bianchi
((SELECT id FROM medici WHERE numero_albo = 'MI11223'), 1, '10:00', '15:00'),
((SELECT id FROM medici WHERE numero_albo = 'MI11223'), 5, '09:00', '13:00');


INSERT INTO recensioni
(prenotazione_id, paziente_id, medico_id, rating, titolo, commento, is_verificata)
VALUES
(
 (SELECT id FROM prenotazioni WHERE numero_prenotazione = 'NPI20250115001'),
 (SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'),
 (SELECT id FROM medici WHERE numero_albo = 'RM12345'),
 5,
 'Professionista eccellente',
 'Molto attento e chiaro nelle spiegazioni.',
 TRUE
);


INSERT INTO percorsi_terapeutici
(paziente_id, medico_id, titolo, obiettivi, numero_sedute_previste)
VALUES
(
 (SELECT id FROM pazienti WHERE codice_fiscale = 'VRDMRC15A15F205X'),
 (SELECT id FROM medici WHERE numero_albo = 'RM12345'),
 'Percorso ADHD',
 'Migliorare attenzione e regolazione emotiva',
 12
);



-- ============================================================
-- RIEPILOGO DATI INSERITI
-- ============================================================
-- ✅ 1 Amministratore
-- ✅ 4 Specialisti (Neuropsichiatra, Psicologa, Logopedista, Psicomotricista)
-- ✅ 5 Tutori/Genitori
-- ✅ 4 Pazienti (bambini/adolescenti)
-- ✅ 5 Appuntamenti (3 confermati, 1 in attesa, 1 completato)
-- ✅ 1 Referto
-- ✅ 1 Recensione
-- ✅ Disponibilità settimanali per tutti gli specialisti
-- ✅ 2 Notifiche

-- Password per tutti gli utenti: 123456
-- Hash: $2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.

-- ============================================================
-- QUERY UTILI PER VERIFICARE I DATI
-- ============================================================

-- Verifica tutti gli utenti
-- SELECT id, email, nome, cognome, ruolo FROM users;

-- Verifica specialisti con dettagli
-- SELECT u.nome, u.cognome, m.specializzazione, m.numero_albo 
-- FROM users u JOIN medici m ON u.id = m.user_id;

-- Verifica pazienti
-- SELECT u.nome, u.cognome, p.codice_fiscale, p.diagnosi_principale 
-- FROM users u JOIN pazienti p ON u.id = p.user_id;

-- Verifica appuntamenti
-- SELECT * FROM v_prenotazioni_dettagliate ORDER BY data_ora;

-- Statistiche dashboard
-- SELECT * FROM v_statistiche_dashboard;