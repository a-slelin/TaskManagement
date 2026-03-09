FROM amazoncorretto:25-alpine-jdk
WORKDIR /app
COPY target/TaskManagementSystem.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]