#!/bin/bash
# -----------------------
# 환경 설정
# -----------------------
DEPLOY_PATH=/home/ubuntu/app/deploy  # 배포할 경로
echo "🧹 Cleaning up previous deployment files..."

# app.jar 삭제
if [ -f "$DEPLOY_PATH/app.jar" ]; then
  rm -f $DEPLOY_PATH/app.jar
  echo "✅ Removed app.jar"
fi

# app.zip 삭제
if [ -f "$DEPLOY_PATH/app.zip" ]; then
  rm -f $DEPLOY_PATH/app.zip
  echo "✅ Removed app.zip"
fi

# start.sh는 삭제하지 않음 → 새 버전이 AfterInstall에서 덮어쓰기
echo "✅ Clean-up complete!"

