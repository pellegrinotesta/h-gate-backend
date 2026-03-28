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
1. **Database:** Creare uno schema MySQL denominato `hgate_db`.
2. **Environment:** Clonare il file `application-dev.properties` e configurare le proprie credenziali DB.
3. **Build:** ```bash
   ./mvnw clean install
