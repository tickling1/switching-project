#!/bin/bash

echo "🔍 Checking for existing start.sh file..."

# 이전 start.sh 파일이 있으면 삭제
if [ -f /home/ubuntu/app/deploy/start.sh ]; then
    echo "🗑 Found existing start.sh, removing it..."
    rm -f /home/ubuntu/app/deploy/start.sh
else
    echo "✅ No start.sh found, proceeding..."
fi
