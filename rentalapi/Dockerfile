# ---- Étape 1 : Construction de l'application ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copie des fichiers de configuration Maven
COPY pom.xml .
# Téléchargement des dépendances (mise en cache pour accélérer les futurs builds)
RUN mvn dependency:go-offline -B

# Copie du code source et compilation
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Étape 2 : Exécution de l'application ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Récupération du fichier JAR compilé à l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Port exposé par Render (8080 par défaut pour Spring Boot)
EXPOSE 8080

# Commande de démarrage de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
