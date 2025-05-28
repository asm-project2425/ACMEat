FROM jolielang/jolie

# Create the working directory inside the container
WORKDIR /app

# Copy everything from the local src/ folder into /app
COPY src/ /app/

# Copy the PostgreSQL JDBC driver into the container
COPY lib/postgresql-42.7.5.jar /app/lib/

# Set the CLASSPATH environment variable
ENV CLASSPATH=/app/lib/postgresql-42.7.5.jar

# Expose the port used by the Jolie service (as defined in server.ol)
EXPOSE 8000

# Start the Jolie service
CMD ["jolie", "server.ol"]