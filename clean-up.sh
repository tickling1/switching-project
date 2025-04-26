#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
DEPLOY_PATH=/home/ubuntu/app/deploy  # 배포할 경로

echo "🧹 Cleaning up previous deployment files..."

# 기존 app.zip 삭제
if [ -f "$DEPLOY_PATH/app.zip" ]; then
  echo "🗑 Removing existing app.zip..."
  rm -f $DEPLOY_PATH/app.zip
fi

# 기존 start.sh 삭제
if [ -f "$DEPLOY_PATH/start.sh" ]; then
  echo "🗑 Removing existing start.sh..."
  rm -f $DEPLOY_PATH/start.sh
fi

# 기존 app.jar 삭제
if [ -f "$DEPLOY_PATH/app.jar" ]; then
  echo "🗑 Removing existing app.jar..."
  rm -f $DEPLOY_PATH/app.jar
fi

echo "✅ Clean-up complete!"
