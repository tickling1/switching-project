#!/bin/bash
DEPLOY_PATH=/home/ubuntu/app/deploy

echo "🧹 Cleaning up previous deployment files..."

# 자기 자신 삭제
if [ -f "$DEPLOY_PATH/clean-up.sh" ]; then
  rm -f $DEPLOY_PATH/clean-up.sh
  echo "🗑 Removed old clean-up.sh"
fi

# 나머지 파일 정리
rm -f $DEPLOY_PATH/app.jar
rm -f $DEPLOY_PATH/app.zip
rm -f $DEPLOY_PATH/start.sh
rm -f $DEPLOY_PATH/appspec.yml
echo "✅ Clean-up complete!"
