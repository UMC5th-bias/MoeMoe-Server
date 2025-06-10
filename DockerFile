FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY build/libs/favoriteplace-0.0.1-SNAPSHOT.jar ./app.jar

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
