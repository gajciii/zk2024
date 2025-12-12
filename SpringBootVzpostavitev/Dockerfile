FROM eclipse-temurin:17-jdk

WORKDIR /app

# Kopiraj Maven wrapper in konfiguracijo
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Dodeli izvršljive pravice za mvnw
RUN chmod +x ./mvnw

# Kopiraj source kodo
COPY src/ ./src/

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo z eksplicitno določenim JAR imenom
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
