# h-gate-backend
Backend scalabile e sicuro che implementa l'architettura RESTful come Health-Gateway (H-Gate) per l'accesso ai dati sanitari. Fornisce gli endpoint necessari per la gestione centralizzata di servizi (es. referti/prenotazioni) e garantisce l'autenticazione tramite JWT.

## Test

```bash
cd H-Gate-api
./mvnw test
```

I test girano su H2 in memoria (profilo `test`, vedi `src/test/resources/application-test.yml`): non serve MySQL né Docker.
Coprono permessi di accesso ai dati clinici, autenticazione, gestione degli errori, prenotazioni/slot,
allegati e il flusso di approvazione di medici e associazioni tutore-minore.

## Migrazioni database

Le nuove installazioni usano `src/main/resources/schema.sql`. Sui database già esistenti eseguire **una volta**,
in ordine, gli script in `src/main/resources/db/migrations/`:

```bash
mysql -h <host> -u <utente> -p <database> < src/main/resources/db/migrations/2026-11-22_approvazioni.sql
mysql -h <host> -u <utente> -p <database> < src/main/resources/db/migrations/2026-11-22_correzioni_dati.sql
mysql -h <host> -u <utente> -p <database> < src/main/resources/db/migrations/2026-11-22_audit_consensi.sql
```

## Privacy e sicurezza: note operative

- **Registro accessi** (`audit_log`): ogni consultazione/modifica di dati clinici, gli accessi negati e i login.
  L'applicazione non lo modifica né lo cancella. In produzione concedi all'utente del database solo
  `INSERT, SELECT` su questa tabella:
  ```sql
  REVOKE UPDATE, DELETE ON hgate.audit_log FROM 'hgate_user'@'%';
  ```
  Consultazione: pagina *Registro accessi* (admin). I tutori vedono chi ha consultato i dati dei propri minori.
- **Consensi** (`consensi`): storico con versione dell'informativa, autore, data e IP. Quando cambi il testo
  dell'informativa aggiorna `PRIVACY_VERSIONE_INFORMATIVA`: ai tutori verrà chiesto di riconfermare.
  La revoca blocca nuove prenotazioni; la documentazione clinica già prodotta resta conservata.
- **Allegati**: hash SHA-256 salvato al caricamento e verificato a ogni download (un file alterato non viene servito).
- **Backup**: `scripts/backup.sh` e `scripts/restore.sh` nella cartella del `docker-compose.yml`
  (database + allegati, cifrati AES-256). Pianificali con cron, copiali fuori dal server e prova il ripristino.
- **Render**: il filesystem dei servizi Render è temporaneo. Senza un *Persistent Disk* montato su
  `UPLOAD_DIR` gli allegati vengono persi a ogni deploy o riavvio.
- **IP nel registro**: affidabile solo se il backend è raggiungibile esclusivamente tramite un proxy (nginx, Render)
  che imposta `X-Forwarded-For`.
