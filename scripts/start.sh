#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"
#JAR_FILE=$(ls -tr $PROJECT_ROOT/*SNAPSHOT.jar | tail -n 1)

APP_LOG="$PROJECT_ROOT/application.log"
ERROR_LOG="$PROJECT_ROOT/error.log"
DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# build 파일 복사
echo "$TIME_NOW > $JAR_FILE 파일 복사" >> $DEPLOY_LOG
cp $PROJECT_ROOT/build/libs/*.jar $JAR_FILE

# jar 파일 실행
echo "$TIME_NOW > $JAR_FILE 파일 실행" >> $DEPLOY_LOG
source ~/.bashrc
nohup sudo java -jar \
        -Dspring.config.location=/home/ubuntu/app/application.yml \
        -Duser.timezone=Asia/Seoul\
        $JAR_FILE > $APP_LOG 2> $ERROR_LOG &

CURRENT_PID=$(pgrep -f $JAR_FILE)
echo "$TIME_NOW > 실행된 프로세스 아이디 $CURRENT_PID 입니다." >> $DEPLOY_LOG