#!/bin/bash
echo "Checking for existing deploy.sh file..."

# 기존 deploy.sh 파일이 존재하면 삭제
if [ -f /home/ubuntu/app/deploy/deploy.sh ]; then
    echo "Found existing deploy.sh, removing it..."
    rm -f /home/ubuntu/app/deploy/deploy.sh
else
    echo "No deploy.sh found, proceeding..."
fi
