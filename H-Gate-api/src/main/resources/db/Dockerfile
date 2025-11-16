# Usa l'immagine ufficiale di MySQL
FROM mysql:8.0

# Imposta variabili d'ambiente per configurare MySQL
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=h_gate
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=password

# Copia gli script SQL nella directory di inizializzazione di MySQL
COPY ../schema.sql /docker-entrypoint-initdb.d/
COPY ../data.sql /docker-entrypoint-initdb.d/

# (Facoltativo) Copia il file di configurazione personalizzato
# COPY my.cnf /etc/mysql/conf.d/

# Esponi la porta 3306
EXPOSE 3306
