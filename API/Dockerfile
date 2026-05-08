FROM amazoncorretto:25-alpine-jdk
WORKDIR /app
COPY target/API.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]