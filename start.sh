#!/bin/bash

# -----------------------
# 환경 설정
# -----------------------
APP_NAME=study-app                    # 애플리케이션 이름
JAR_NAME=app.jar                      # 실행할 JAR 파일명
DEPLOY_PATH=/home/ubuntu/app/deploy    # 배포할 경로
LOG_PATH=/home/ubuntu/app/logs          # 로그 저장 경로
S3_BUCKET=switching-bucket-202504       # S3 버킷 이름
S3_KEY=app.zip                         # S3에 올라간 zip 파일 이름

PORT_A=9090
PORT_B=9091
HEALTH_CHECK_URL=http://localhost     # 헬스체크 URL (뒤에 포트 붙일 거야)

# -----------------------
# 로그 디렉토리 생성
# -----------------------
mkdir -p $LOG_PATH

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
  echo "❗ JAR 파일이 존재하지 않습니다: $DEPLOY_PATH/$JAR_NAME"
  exit 1
fi
echo "✅ JAR 준비 완료"

# -----------------------
# 현재 실행 중인 포트 확인
# -----------------------
echo "🔍 현재 실행 중인 포트 확인"
EXIST_PID_A=$(lsof -i :$PORT_A -t)
EXIST_PID_B=$(lsof -i :$PORT_B -t)

if [ -z "$EXIST_PID_A" ]; then
  CURRENT_PORT=$PORT_A
else
  CURRENT_PORT=$PORT_B
fi

echo "✅ 현재 $((CURRENT_PORT == PORT_A ? PORT_B : PORT_A)) 사용 중 → 다음 포트: $CURRENT_PORT"

# -----------------------
# 서버 실행
# -----------------------
echo "🚀 새 버전 서버 실행 (포트 $CURRENT_PORT)"
nohup java -jar -Dserver.port=$CURRENT_PORT $DEPLOY_PATH/$JAR_NAME > $LOG_PATH/nohup_$CURRENT_PORT.log 2>&1 &

# -----------------------
# 헬스 체크
# -----------------------
echo "⏳ 헬스체크 시작 (최대 10회)"
for attempt in {1..10}
do
  sleep 5
  RESPONSE=$(curl -s "$HEALTH_CHECK_URL:$CURRENT_PORT/actuator/health" | grep '"status":"UP"')

  if [ -n "$RESPONSE" ]; then
    echo "✅ 헬스체크 성공!"

    # 이전 버전 종료
    if [ "$CURRENT_PORT" -eq "$PORT_A" ]; then
      OLD_PORT=$PORT_B
    else
      OLD_PORT=$PORT_A
    fi

    OLD_PID=$(lsof -i :$OLD_PORT -t)
    if [ -n "$OLD_PID" ]; then
      echo "🛑 이전 서버 종료 (포트 $OLD_PORT, PID $OLD_PID)"
      kill -15 $OLD_PID
    fi

    echo "🎉 배포 완료!"
    exit 0
  else
    echo "⌛ 헬스체크 대기 중... (시도 $attempt/10)"
  fi
done

echo "❗ 헬스체크 실패, 서버 배포 중단"
exit 1
