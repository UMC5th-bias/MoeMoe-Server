## run_new_was.sh
#
##!/bin/bash
#
#CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
#TARGET_PORT=0
#
#echo "> Current port of running WAS is ${CURRENT_PORT}."
#
#if [ ${CURRENT_PORT} -eq 8081 ]; then
#  TARGET_PORT=8082
#elif [ ${CURRENT_PORT} -eq 8082 ]; then
#  TARGET_PORT=8081
#else
#  echo "> No WAS is connected to nginx"
#fi
#
#TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')
#
#if [ ! -z ${TARGET_PID} ]; then
#  echo "> Kill WAS running at ${TARGET_PORT}."
#  sudo kill ${TARGET_PID}
#fi
#
#nohup java -jar \
#        -Dserver.port=${TARGET_PORT} \
#        -Dspring.config.location=/home/ubuntu/app/application.yml \
#        /home/ubuntu/app/build/libs/* > /home/ubuntu/nohup.out 2>&1 &
#echo "> Now new WAS runs at ${TARGET_PORT}."
#sleep 10s
#exit 0

# run_new_was.sh

#!/bin/bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/build/libs/FavoritePlace-0.0.1-SNAPSHOT.jar"
APP_LOG="$PROJECT_ROOT/run_new_was.log"


# service_url.inc 에서 현재 서비스를 하고 있는 WAS의 포트 번호 가져오기
CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

echo "> Current port of running WAS is ${CURRENT_PORT}."

if [ ${CURRENT_PORT} -eq 8081 ]; then
  TARGET_PORT=8082 # 현재포트가 8081이면 8082로 배포
elif [ ${CURRENT_PORT} -eq 8082 ]; then
  TARGET_PORT=8081 # 현재포트가 8082라면 8081로 배포
else
  echo "> Not connected to nginx" # nginx가 실행되고 있지 않다면 에러 코드
fi

# 현재 포트의 PID를 불러온다
TARGET_PID=$(lsof -Fp -i TCP:${TARGET_PORT} | grep -Po 'p[0-9]+' | grep -Po '[0-9]+')

# PID를 이용해 해당 포트 서버 Kill
if [ ! -z ${TARGET_PID} ]; then
  echo "> Kill ${TARGET_PORT}."
  sudo kill ${TARGET_PID}
fi

# 타켓 포트에 jar파일을 이용해 새로운 서버 실행
nohup java -jar \
        -Dserver.port=${TARGET_PORT} \
        -Dspring.config.location=/home/ubuntu/app/application.yml \
        /home/ubuntu/app/build/libs/* > /home/ubuntu/nohup.out 2>&1 &

echo "> Now new WAS runs at ${TARGET_PORT}."
exit 0
