FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ./target/UDBApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]