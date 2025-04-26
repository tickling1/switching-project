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
echo "⬇️ Downloading new deployment package from S3..."
aws s3 cp s3://$S3_BUCKET/$S3_KEY $DEPLOY_PATH/app.zip
if [ $? -ne 0 ]; then
  echo "❗ Failed to download app.zip"
  exit 1
fi
echo "✅ Download successful!"

# -----------------------
# ZIP 압축 해제
# -----------------------
echo "📦 Unzipping app.zip..."
unzip -o $DEPLOY_PATH/app.zip -d $DEPLOY_PATH
if [ $? -ne 0 ]; then
  echo "❗ Failed to unzip app.zip"
  exit 1
fi

# -----------------------
# start.sh 권한 부여 (지금 시점에!)
# -----------------------
chmod +x $DEPLOY_PATH/start.sh
echo "✅ start.sh permission granted!"

# -----------------------
# 현재 실행 중인 포트 확인
# -----------------------
echo "🔍 Checking current running port..."

CURRENT_PORT=$(pgrep -f $JAR_NAME | xargs -r -I {} sudo lsof -Pan -p {} -i | grep LISTEN | awk '{print $9}' | sed 's/.*://')

if [ "$CURRENT_PORT" == "$PORT_A" ]; then
  IDLE_PORT=$PORT_B
elif [ "$CURRENT_PORT" == "$PORT_B" ]; then
  IDLE_PORT=$PORT_A
else
  IDLE_PORT=$PORT_A
fi

echo "✅ Current Port: ${CURRENT_PORT:-none}, Next Port: $IDLE_PORT"

# -----------------------
# 새 서버 실행
# -----------------------
echo "🚀 Starting new server on port $IDLE_PORT..."
nohup java -jar -Dserver.port=$IDLE_PORT $DEPLOY_PATH/$JAR_NAME > $LOG_PATH/nohup_$IDLE_PORT.out 2>&1 &

# -----------------------
# 헬스체크
# -----------------------
echo "⏳ Health check on port $IDLE_PORT (max 10 tries)..."

for i in {1..10}
do
  RESPONSE=$(curl -s http://localhost:$IDLE_PORT$HEALTH_CHECK_PATH | grep '"status":"UP"')
  if [ -n "$RESPONSE" ]; then
    echo "✅ Health check passed!"

    # -----------------------
    # 기존 서버 종료
    # -----------------------
    if [ -n "$CURRENT_PORT" ]; then
      echo "🛑 Stopping old server on port $CURRENT_PORT..."
      OLD_PID=$(sudo lsof -t -i :$CURRENT_PORT)
      if [ -n "$OLD_PID" ]; then
        kill -15 $OLD_PID
        echo "✅ Old server stopped."
      fi
    fi

    exit 0
  else
    echo "❗ Health check failed (attempt $i)..."
    sleep 5
  fi
done

echo "❗ Health check failed after 10 tries. Deployment failed."
exit 1
