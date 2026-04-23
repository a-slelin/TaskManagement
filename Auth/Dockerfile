FROM amazoncorretto:25-alpine-jdk
WORKDIR /app
COPY target/Auth.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]