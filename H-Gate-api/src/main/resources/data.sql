INSERT INTO users (uuid, email, password, nome, cognome, telefono, data_nascita, citta, provincia)
VALUES
(UUID(), 'mario.rossi@example.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Mario', 'Rossi', '3451234567', '1980-02-15', 'Milano', 'MI'),
(UUID(), 'lucia.bianchi@example.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Lucia', 'Bianchi', '3479876543', '1990-08-22', 'Roma', 'RM'),
(UUID(), 'giovanni.verdi@example.com', '$2a$10$EGcbnw2xbw39nlYCWyQuc.0Q9GwZxraLr7n8dNmimZNqPACGaiky.', 'Giovanni', 'Verdi', '3391122334', '1975-11-10', 'Napoli', 'NA');

INSERT INTO roles (user_id, role)
VALUES
(1, 'MEDICO'),
(2, 'PAZIENTE'),
(3, 'AMMINISTRATORE');

INSERT INTO medici (user_id, specializzazione, numero_albo, universita, anno_laurea, bio, durata_visita_minuti, pausa_tra_visite_minuti)
VALUES
(1, 'Cardiologia', 'ALB12345', 'Università degli Studi di Milano', 2005, 'Cardiologo con 15 anni di esperienza', 30, 5);

INSERT INTO pazienti (user_id, codice_fiscale, gruppo_sanguigno, altezza_cm, peso_kg, medico_id, consenso_privacy)
VALUES
(2, 'RSSLCU90M62H501X', 'A+', 170, 65.5, 1, TRUE);

INSERT INTO prenotazioni (uuid, numero_prenotazione, paziente_id, medico_id, data_ora, data_ora_fine, tipo_visita, stato, costo)
VALUES
(UUID(), 'PR0001', 1, 1, '2025-11-20 09:00:00', '2025-11-20 09:30:00', 'Visita cardiologica', 'IN_ATTESA', 80.00);

INSERT INTO referti (uuid, prenotazione_id, medico_id, paziente_id, tipo_referto, titolo, diagnosi)
VALUES
(UUID(), 1, 1, 1, 'Cardiologico', 'Referto visita cardiologica', 'Normale');
