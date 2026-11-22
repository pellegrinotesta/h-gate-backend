-- ============================================================
-- MIGRAZIONE 2026-11-22: correzione valori NULL dovuti a un bug
--
-- Le entità create dall'applicazione (Lombok @Builder senza @Builder.Default)
-- venivano salvate con NULL al posto dei valori predefiniti. Conseguenze:
--   - promemoria 24h mai inviati (il job cerca promemoria_inviato = FALSE)
--   - slot dei medici registrati dall'app non calcolabili (pausa NULL)
-- Da eseguire UNA VOLTA sui database esistenti. È idempotente.
-- ============================================================

UPDATE prenotazioni SET promemoria_inviato = FALSE WHERE promemoria_inviato IS NULL;
UPDATE prenotazioni SET conferma_inviata   = FALSE WHERE conferma_inviata   IS NULL;
UPDATE prenotazioni SET is_prima_visita    = FALSE WHERE is_prima_visita    IS NULL;
UPDATE prenotazioni SET is_urgente         = FALSE WHERE is_urgente         IS NULL;

UPDATE medici SET durata_visita_minuti    = 30   WHERE durata_visita_minuti    IS NULL;
UPDATE medici SET pausa_tra_visite_minuti = 5    WHERE pausa_tra_visite_minuti IS NULL;
UPDATE medici SET rating_medio            = 0.00 WHERE rating_medio            IS NULL;
UPDATE medici SET is_disponibile          = TRUE WHERE is_disponibile          IS NULL;

UPDATE disponibilita_medici SET is_attiva = TRUE WHERE is_attiva IS NULL;

UPDATE notifiche SET is_letta         = FALSE WHERE is_letta         IS NULL;
UPDATE notifiche SET is_inviata_email = FALSE WHERE is_inviata_email IS NULL;

UPDATE pazienti SET consenso_marketing = FALSE WHERE consenso_marketing IS NULL;

UPDATE referti SET is_firmato          = FALSE WHERE is_firmato          IS NULL;
UPDATE referti SET is_inviato_paziente = FALSE WHERE is_inviato_paziente IS NULL;
