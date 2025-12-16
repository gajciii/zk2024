FROM eclipse-temurin:17-jdk

WORKDIR /app

# Kopiraj Maven wrapper (mora biti pred COPY . .)
COPY .mvn .mvn
COPY mvnw pom.xml ./

# Kopiraj preostale datoteke
COPY . .

# Dodeli izvršljive pravice za mvnw (po zadnjem COPY, da se pravice ne prepišejo)
RUN chmod +x ./mvnw

# Gradi aplikacijo
RUN ./mvnw clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo z eksplicitno določenim JAR imenom
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
