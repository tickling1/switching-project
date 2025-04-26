#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
DEPLOY_PATH=/home/ubuntu/app/deploy  # 배포할 경로

# -----------------------
# ZIP 파일 삭제 (기존 버전)
# -----------------------
if [ -f "$DEPLOY_PATH/app.zip" ]; then
  echo "🗑 기존 app.zip 파일 삭제"
  rm -f $DEPLOY_PATH/app.zip
fi

echo "🔍 Checking for existing start.sh and app.jar files..."

# 기존 start.sh 파일이 있으면 삭제
if [ -f /home/ubuntu/app/deploy/start.sh ]; then
    echo "🗑 Found existing start.sh, removing it..."
    rm -f /home/ubuntu/app/deploy/start.sh
else
    echo "✅ No start.sh found, proceeding..."
fi

# 기존 app.jar 파일이 있으면 삭제
if [ -f /home/ubuntu/app/deploy/app.jar ]; then
    echo "🗑 Found existing app.jar, removing it..."
    rm -f /home/ubuntu/app/deploy/app.jar
else
    echo "✅ No app.jar found, proceeding..."
fi


# clean-up 이후, 압축 풀린 최신 파일에 권한 부여
echo "🔒 Giving execute permission to new start.sh after deployment..."
if [ -f /home/ubuntu/app/deploy/start.sh ]; then
    chmod +x /home/ubuntu/app/deploy/start.sh
    echo "✅ Permission granted to start.sh"
else
    echo "⚠️ No start.sh found yet. Permission will be granted later after unzip."
fi