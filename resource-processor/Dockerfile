FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /resource-processor
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=build /resource/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar","app.jar"]