FROM eclipse-temurin:17-jdk

WORKDIR /app

# Namesti Maven
RUN apt-get update && apt-get install -y maven

# Kopiraj pom.xml in source
COPY pom.xml ./
COPY src ./src

# Gradi aplikacijo
RUN mvn clean package -DskipTests

# Eksponiraj port
EXPOSE 8080

# Zagni aplikacijo z eksplicitno določenim JAR imenom
CMD ["sh", "-c", "java -jar $(ls target/*.jar | head -n 1)"]
