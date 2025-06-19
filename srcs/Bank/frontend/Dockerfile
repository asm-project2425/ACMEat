FROM webdevops/php-nginx:8.2-alpine

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file del sito dalla directory locale ./service
COPY ./service/ /app/

# Assicura i permessi corretti per l'utente www-data
RUN chown -R application:application /app
# Variabili d'ambiente opzionali per PHP
ENV PHP_MEMORY_LIMIT=256M \
    PHP_MAX_EXECUTION_TIME=60 \
    PHP_UPLOAD_MAX_FILESIZE=50M \
    PHP_POST_MAX_SIZE=50M

# La porta 80 è già esposta dall'immagine base
EXPOSE 80

# Il CMD è già definito nell'immagine base