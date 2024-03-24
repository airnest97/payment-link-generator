FROM openjdk:17-alpine
MAINTAINER task
EXPOSE 6040
ARG JAR_FILE=target/task.jar
ADD ${JAR_FILE} task.jar
ENTRYPOINT ["java", "-jar", "/task.jar"]