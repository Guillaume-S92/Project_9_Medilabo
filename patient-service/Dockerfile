FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q -pl patient-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/patient-service/target/patient-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
