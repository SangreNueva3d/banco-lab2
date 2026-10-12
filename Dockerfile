# ---------- Etapa 1: compilar con Maven ----------
# Versión fija (no "latest") para que el build sea reproducible; el proyecto usa Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Se copia solo lo necesario (no target/ ni .idea/)
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: imagen final, solo con el JRE ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# El nombre del JAR sale de <finalName> en el pom.xml
COPY --from=build /app/target/lab1v20262.jar app.jar
# Puerto local de la app (en Render lo define la variable PORT)
EXPOSE 8088
# MaxRAMPercentage: la capa gratuita de Render tiene poca memoria; así la JVM usa mejor la que hay
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
