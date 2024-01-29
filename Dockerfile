FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY /path/to/application.yml /home/ubuntu/app/application.yml
ENTRYPOINT ["java", "-Dspring.config.location=/home/ubuntu/app/application.yml", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]