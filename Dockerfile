FROM eclipse-temurin:21-jdk-alpine
RUN adduser -S -u 1001 1001

COPY target/*.jar app.jar

RUN chown 1001 /app.jar

USER 1001

ENTRYPOINT ["java","-jar","/app.jar"]
