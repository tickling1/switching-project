#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
DEPLOY_PATH=/home/ubuntu/app/deploy  # 배포할 경로

# -----------------------
# 디렉토리 존재 여부 확인
# -----------------------
if [ ! -d "$DEPLOY_PATH" ]; then
  echo "❗ Directory $DEPLOY_PATH does not exist."
  exit 1
fi

echo "🧹 Cleaning up previous deployment files..."

# -----------------------
# 기존 app.zip 삭제
# -----------------------
if [ -f "$DEPLOY_PATH/app.zip" ]; then
  echo "🗑 Removing existing app.zip..."
  rm -f $DEPLOY_PATH/app.zip
  echo "✅ Removed app.zip"
else
  echo "⚠️ No app.zip found."
fi

# -----------------------
# 기존 start.sh 삭제
# -----------------------
if [ -f "$DEPLOY_PATH/start.sh" ]; then
  echo "🗑 Removing existing start.sh..."
  rm -f $DEPLOY_PATH/start.sh
  echo "✅ Removed start.sh"
else
  echo "⚠️ No start.sh found."
fi

# -----------------------
# 기존 app.jar 삭제
# -----------------------
if [ -f "$DEPLOY_PATH/app.jar" ]; then
  echo "🗑 Removing existing app.jar..."
  rm -f $DEPLOY_PATH/app.jar
  echo "✅ Removed app.jar"
else
  echo "⚠️ No app.jar found."
fi

echo "✅ Clean-up complete!"
