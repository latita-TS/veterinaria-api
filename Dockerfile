# ==========================================================
#  Imagen Docker de la API (la usa Render para publicarla).
#  Etapa 1: compila el proyecto con Maven.
#  Etapa 2: solo copia el .jar a una imagen liviana con Java.
# ==========================================================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/veterinaria-api.jar app.jar
ENV TZ=America/Lima
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx384m", "-jar", "app.jar"]
