#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
APP_NAME=study-app
JAR_NAME=app.jar
DEPLOY_PATH=/home/ubuntu/app/deploy
LOG_PATH=/home/ubuntu/app/logs
S3_BUCKET=switching-bucket-202504
S3_KEY=app.zip

PORT_A=9090
PORT_B=9091
HEALTH_CHECK_PATH=/actuator/health

mkdir -p $LOG_PATH

# -----------------------
# 배포 파일 다운로드
# -----------------------
echo "⬇️ S3에서 새 배포 패키지 다운로드 중..."
aws s3 cp s3://$S3_BUCKET/$S3_KEY $DEPLOY_PATH/app.zip
if [ $? -ne 0 ]; then
  echo "❗ app.zip 다운로드 실패"
  exit 1
fi
echo "✅ 다운로드 성공!"

# -----------------------
# ZIP 압축 해제
# -----------------------
echo "📦 app.zip 압축 해제 중..."
unzip -o $DEPLOY_PATH/app.zip -d $DEPLOY_PATH
if [ $? -ne 0 ]; then
  echo "❗ 압축 해제 실패"
  exit 1
fi

# -----------------------
# 현재 실행 중인 포트 확인
# -----------------------
echo "🔍 현재 실행 중인 서버 포트 확인..."

# 실행 중인 JAR 프로세스 PID 추출
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -n "$CURRENT_PID" ]; then
  # PID로 포트 확인 (LISTEN 중인 포트)
  CURRENT_PORT=$(sudo lsof -Pan -p $CURRENT_PID -i | grep LISTEN | awk '{print $9}' | sed 's/.*://')
else
  CURRENT_PORT=""
fi

if [ "$CURRENT_PORT" == "$PORT_A" ]; then
  IDLE_PORT=$PORT_B
elif [ "$CURRENT_PORT" == "$PORT_B" ]; then
  IDLE_PORT=$PORT_A
else
  IDLE_PORT=$PORT_A
fi

echo "✅ 현재 포트: ${CURRENT_PORT:-없음}, 새 서버 포트: $IDLE_PORT"

# -----------------------
# 기존 서버 종료
# -----------------------
if [ -n "$CURRENT_PORT" ]; then
  echo "🛑 기존 서버($CURRENT_PORT 포트) 종료 중..."
  OLD_PIDS=$(sudo lsof -t -i :$CURRENT_PORT)
  if [ -n "$OLD_PIDS" ]; then
    kill -15 $OLD_PIDS
    sleep 5
    # 종료 안 됐으면 강제 종료
    OLD_PIDS=$(sudo lsof -t -i :$CURRENT_PORT)
    if [ -n "$OLD_PIDS" ]; then
      echo "⚠️ 강제 종료 중..."
      kill -9 $OLD_PIDS
    fi
    echo "✅ 기존 서버 종료 완료"
  else
    echo "✅ 종료할 프로세스 없음"
  fi
else
  echo "✅ 실행 중인 서버 없음"
fi

# -----------------------
# 새 서버 실행
# -----------------------
echo "🚀 새 서버를 포트 $IDLE_PORT 로 시작합니다..."
nohup java -jar -Dserver.port=$IDLE_PORT $DEPLOY_PATH/$JAR_NAME > $LOG_PATH/nohup_$IDLE_PORT.out 2>&1 &

# -----------------------
# 헬스체크
# -----------------------
echo "⏳ 새 서버 헬스체크 중 (최대 10회 시도)..."

for i in {1..10}
do
  RESPONSE=$(curl -s http://localhost:$IDLE_PORT$HEALTH_CHECK_PATH | grep '"status":"UP"')
  if [ -n "$RESPONSE" ]; then
    echo "✅ 헬스체크 통과!"

    # -----------------------
    # Nginx 업스트림 포트 스위칭
    # -----------------------
    echo "🔀 Nginx 업스트림 서버 포트 변경 중..."

    # 기존 포트가 9090이면 9091로, 아니면 9090으로 변경
    if grep -q "server 127.0.0.1:9090" /etc/nginx/sites-available/default; then
      # 9090은 주석처리, 9091은 주석 해제
      sudo sed -i 's/^#server 127.0.0.1:9091/server 127.0.0.1:9091/' /etc/nginx/sites-available/default
      sudo sed -i 's/^server 127.0.0.1:9090/#server 127.0.0.1:9090/' /etc/nginx/sites-available/default
      echo "⚡ Nginx 업스트림을 9091로 변경"
    else
      # 9091은 주석처리, 9090은 주석 해제
      sudo sed -i 's/^#server 127.0.0.1:9090/server 127.0.0.1:9090/' /etc/nginx/sites-available/default
      sudo sed -i 's/^server 127.0.0.1:9091/#server 127.0.0.1:9091/' /etc/nginx/sites-available/default
      echo "⚡ Nginx 업스트림을 9090으로 변경"
    fi

    # Nginx 재시작
    sudo nginx -s reload
    echo "✅ Nginx 재시작 완료"

    exit 0
  else
    echo "❗ 헬스체크 실패 ($i 번째 시도)... 5초 후 재시도"
    sleep 5
  fi
done

echo "❗ 헬스체크 10회 실패, 배포 실패"
exit 1
