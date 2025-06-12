# Stage 1: Build with Maven and Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src ./src

# Use Maven Wrapper to build the project
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Run con Java 21
FROM eclipse-temurin:21-jdk

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY src/main/resources/wsdl/bank.wsdl /app/src/main/resources/wsdl/bank.wsdl

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]