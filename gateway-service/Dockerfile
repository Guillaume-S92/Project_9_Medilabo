FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -q -pl gateway-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/gateway-service/target/gateway-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
