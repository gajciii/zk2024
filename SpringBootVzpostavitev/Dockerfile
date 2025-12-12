FROM openjdk:17-jdk-slim AS backend

WORKDIR /app

# Kopiraj vse datoteke (podobno kot originalni Dockerfile)
COPY . .

# Dodeli izvršljive pravice za mvnw
RUN chmod +x ./mvnw

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo
CMD ["java", "-jar", "target/ris-0.0.1-SNAPSHOT.jar"]
