#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
APP_NAME=study-app
JAR_NAME=app.jar
DEPLOY_PATH=/home/ubuntu/app/deploy
LOG_PATH=/home/ubuntu/app/logs
S3_BUCKET=switching-bucket-202504
S3_KEY=app.zip  # 이제 zip 파일을 다운로드

echo "⬇️ S3에서 최신 배포 파일 다운로드"
aws s3 cp s3://$S3_BUCKET/$S3_KEY $DEPLOY_PATH/app.zip

if [ $? -ne 0 ]; then
  echo "❗ ZIP 파일 다운로드 실패 - S3에 파일이 없습니다: $S3_BUCKET/$S3_KEY"
  exit 1
fi

echo "✅ ZIP 파일 다운로드 성공: $S3_KEY"

echo "📦 ZIP 파일 풀기"
unzip -o $DEPLOY_PATH/app.zip -d $DEPLOY_PATH  # -o는 기존 파일 덮어쓰기를 허용

if [ $? -ne 0 ]; then
  echo "❗ ZIP 파일 압축 해제 실패"
  exit 1
fi

if [ ! -f "$DEPLOY_PATH/$JAR_NAME" ]; then
  echo "❗ JAR 파일이 존재하지 않습니다 (경로: $DEPLOY_PATH/$JAR_NAME)"
  exit 1
fi

echo "✅ JAR 파일 압축 해제 완료: $JAR_NAME"

echo "🛑 기존 프로세스 종료 (있다면)"
PID=$(pgrep -f "java.*$JAR_NAME")
if [ -n "$PID" ]; then
  kill -9 $PID
  echo "✅ 프로세스 종료 완료 (PID: $PID)"
else
  echo "ℹ️ 종료할 프로세스가 없습니다"
fi

echo "🚀 새 버전 실행"
nohup java -jar $DEPLOY_PATH/$JAR_NAME > $LOG_PATH/app.log 2>&1 &

NEW_PID=$(pgrep -f "java.*$JAR_NAME" | head -n 1)
if [ -n "$NEW_PID" ]; then
  echo "✅ 새 버전 실행 완료 (PID: $NEW_PID)"
else
  echo "❗ 실행 실패 - 프로세스가 시작되지 않았습니다"
  exit 1
fi
