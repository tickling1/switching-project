#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
APP_NAME=study-app             # 애플리케이션 이름
JAR_NAME=app.jar               # 실행할 JAR 파일명
DEPLOY_PATH=/home/ubuntu/app/deploy  # 배포할 경로
LOG_PATH=/home/ubuntu/app/logs        # 로그 저장 경로
S3_BUCKET=switching-bucket-202504     # S3 버킷 이름
S3_KEY=app.zip                        # S3에 올라간 zip 파일 이름

# 포트 두 개 (Blue/Green 배포용)
PORT_A=9090
PORT_B=9091

# -----------------------
# 배포 파일 다운로드
# -----------------------
echo "⬇️ S3에서 최신 배포 파일 다운로드 !"
aws s3 cp s3://$S3_BUCKET/$S3_KEY $DEPLOY_PATH/app.zip
if [ $? -ne 0 ]; then
  echo "❗ ZIP 파일 다운로드 실패: $S3_BUCKET/$S3_KEY"
  exit 1
fi
echo "✅ ZIP 다운로드 성공"

# -----------------------
# ZIP 압축 해제
# -----------------------
echo "📦 ZIP 압축 해제"
unzip -o $DEPLOY_PATH/app.zip -d $DEPLOY_PATH
if [ $? -ne 0 ]; then
  echo "❗ ZIP 압축 해제 실패"
  exit 1
fi

# -----------------------
# JAR 파일 존재 확인
# -----------------------
if [ ! -f "$DEPLOY_PATH/$JAR_NAME" ]; then
  echo "❗ JAR 없음: $DEPLOY_PATH/$JAR_NAME"
  exit 1
fi
echo "✅ JAR 준비 완료"

# -----------------------
# 현재 실행 중인 포트 확인
# -----------------------
echo "🔍 현재 실행 중인 포트 확인"
CURRENT_PORT=$(sudo lsof -i -P -n | grep LISTEN | grep -E ":$PORT_A|:$PORT_B" | head -n1 | awk '{print $9}' | cut -d':' -f2)

# 현재 포트가 없으면 기본값으로 PORT_A 사용
if [ -z "$CURRENT_PORT" ]; then
  echo "⚠️ 실행 중인 서버 없음, $PORT_A로 시작"
  IDLE_PORT=$PORT_A
else
  if [ "$CURRENT_PORT" -eq "$PORT_A" ]; then
    IDLE_PORT=$PORT_B
    echo "✅ 현재 $PORT_A 사용 중 → 다음 포트: $PORT_B"
  else
    IDLE_PORT=$PORT_A
    echo "✅ 현재 $PORT_B 사용 중 → 다음 포트: $PORT_A"
  fi
fi

# -----------------------
# 새 버전 서버 실행
# -----------------------
echo "🚀 새 버전 서버 실행 (포트 $IDLE_PORT)"
nohup java -jar -Dserver.port=$IDLE_PORT $DEPLOY_PATH/$JAR_NAME > $LOG_PATH/nohup-$IDLE_PORT.out 2>&1 &
NEW_PID=$!

# -----------------------
# 헬스 체크 (최대 10회, 1초 간격)
# -----------------------
echo "⏳ 헬스체크 시작 (최대 10회)"
for i in {1..10}
do
  sleep 1
  RESPONSE=$(curl -s http://localhost:$IDLE_PORT/actuator/health)
  STATUS=$(echo $RESPONSE | grep '"status":"UP"')

  if [ -n "$STATUS" ]; then
    echo "✅ 헬스체크 성공"

    # -----------------------
    # 이전 서버 종료
    # -----------------------
    if [ "$CURRENT_PORT" ]; then
      echo "🛑 기존 서버 종료 (포트 $CURRENT_PORT)"
      OLD_PID=$(sudo lsof -t -i:$CURRENT_PORT)
      if [ -n "$OLD_PID" ]; then
        kill $OLD_PID
        echo "✅ 종료 완료: PID $OLD_PID"
      fi
    fi

# -----------------------
# 헬스체크 실패 시 롤백
# -----------------------
echo "❗ 헬스체크 실패 - 배포 중단"
kill $NEW_PID
exit 1
