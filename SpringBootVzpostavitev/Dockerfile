FROM openjdk:17-jdk-slim

WORKDIR /app

# Kopiraj vse datoteke
COPY . .

# Dodeli izvršljive pravice za mvnw
RUN chmod +x ./mvnw

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo (uporabi wildcard za JAR ime)
CMD ["sh", "-c", "java -jar target/*.jar"]
