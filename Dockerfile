FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.config.location=/home/ubuntu/app/application.yml", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
