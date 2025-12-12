FROM eclipse-temurin:17-jdk

WORKDIR /app

# Kopiraj vse datoteke
COPY . .

# Dodeli izvršljive pravice za mvnw
RUN chmod +x ./mvnw

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo z eksplicitno določenim JAR imenom
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
