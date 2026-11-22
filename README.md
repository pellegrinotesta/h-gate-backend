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
```
