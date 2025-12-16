FROM eclipse-temurin:17-jdk

WORKDIR /app

# Kopiraj Maven wrapper (mora biti pred COPY . .)
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Kopiraj preostale datoteke
COPY . .

# Dodeli izvršljive pravice in odstrani CRLF iz wrapper datotek (včasih Git/Windows doda \r)
RUN chmod +x ./mvnw \
    && sed -i 's/\r$//' ./mvnw \
    && sed -i 's/\r$//' .mvn/wrapper/maven-wrapper.properties

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo z eksplicitno določenim JAR imenom
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
