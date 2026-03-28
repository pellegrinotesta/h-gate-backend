# H-Gate API Service

Backend enterprise per la gestione di flussi clinici in ambito neuropsichiatrico. Sviluppato con **Java 17** e **Spring Boot 3.2**, il servizio espone un'interfaccia RESTful per il management dei pazienti, dei tutori e della schedulazione delle visite.

## Core Stack
* **Framework:** Spring Boot 3 (Spring Security, Spring Data JPA).
* **Database:** MySQL 8.0 con gestione dei tipi di dato JSON per i referti.
* **Security:** Autenticazione Stateless via JWT (JSON Web Token).
* **Documentazione:** Swagger/OpenAPI 3 integrato.

## Architettura dei Dati
Il design del database riflette la complessità del dominio:
- Gestione dei ruoli dinamica (Admin, Medico, Tutore).
- Relazione m:n tra pazienti e tutori per supportare nuclei familiari complessi.
- Integrità referenziale garantita a livello di schema con Foreign Keys e Constraint.

## Configurazione Locale
1. Impostare temporaneamente la modalità di inizializzazione su `always`:
   ```yaml
   spring:
     sql:
       init:
         mode: always
2. Avviare l'applicazione. Spring Boot eseguirà gli script SQL presenti nelle risorse.
3. Importante: Una volta create le tabelle, riportare il valore a never per evitare tentativi di sovrascrittura ai riavvii successivi.

## Variabili d'Ambiente
Il backend richiede le seguenti variabili d'ambiente per il corretto funzionamento. Possono essere configurate nel sistema host, nell'IDE o tramite file .env:
DB_URL: jdbc:mysql://localhost:3306/hgate_dbD
B_USER: root
DB_PASS: tua_password_db
JWT_SECRET: stringa_alfanumerica_32_char
