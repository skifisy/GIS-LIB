FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /workspace
COPY backend/ /workspace/
RUN mvn -f /workspace/pom.xml -DskipTests package

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
