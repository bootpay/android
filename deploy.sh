#!/bin/bash
set -e

echo "🚀 Bootpay Android SDK 배포 시작..."
echo "========================================"

# 프로젝트 루트로 이동
cd "$(dirname "$0")"

# publish.gradle에서 버전 정보 읽기
PUBLISH_VERSION=$(grep "PUBLISH_VERSION = " publish.gradle | sed 's/.*PUBLISH_VERSION = '\''\(.*\)'\''/\1/')
PUBLISH_GROUP_ID=$(grep "PUBLISH_GROUP_ID = " publish.gradle | sed 's/.*PUBLISH_GROUP_ID = '\''\(.*\)'\''/\1/')
PUBLISH_ARTIFACT_ID=$(grep "PUBLISH_ARTIFACT_ID = " publish.gradle | sed 's/.*PUBLISH_ARTIFACT_ID = '\''\(.*\)'\''/\1/')

echo "📋 배포 정보:"
echo "   Group ID: $PUBLISH_GROUP_ID"
echo "   Artifact ID: $PUBLISH_ARTIFACT_ID"
echo "   Version: $PUBLISH_VERSION"
echo "========================================"

echo "📦 Step 1: 기존 빌드 정리..."
rm -rf core/build/repo
rm -f android-bundle.zip

echo "📦 Step 2: 새로운 publication 생성..."
./gradlew core:publishReleasePublicationToLocalRepoRepository

echo "📦 Step 3: 번들 생성..."
cd core/build/repo
zip -r ../../../android-bundle.zip \
  ${PUBLISH_GROUP_ID//.//}/${PUBLISH_ARTIFACT_ID}/${PUBLISH_VERSION}/
cd ../../../

echo "✅ 번들 생성 완료: $(ls -lh android-bundle.zip)"

echo "🔐 Step 4: 인증 정보 설정..."
# 자격증명은 gitignored 파일(local.properties)에서만 읽는다.
# 이 스크립트는 public 리포에 커밋되므로 값을 절대 하드코딩하지 말 것.
if [ -f local.properties ]; then
  OSSRH_USERNAME=$(grep '^ossrhToken=' local.properties | cut -d= -f2-)
  OSSRH_PASSWORD=$(grep '^ossrhTokenPassword=' local.properties | cut -d= -f2-)
fi
OSSRH_USERNAME="${OSSRH_USERNAME:-$CENTRAL_TOKEN_USERNAME}"
OSSRH_PASSWORD="${OSSRH_PASSWORD:-$CENTRAL_TOKEN_PASSWORD}"
if [ -z "$OSSRH_USERNAME" ] || [ -z "$OSSRH_PASSWORD" ]; then
  echo "❌ 자격증명이 없습니다."
  echo "   local.properties 에 ossrhToken / ossrhTokenPassword 를 설정하거나"
  echo "   CENTRAL_TOKEN_USERNAME / CENTRAL_TOKEN_PASSWORD 환경변수를 주세요."
  exit 1
fi
BEARER_TOKEN=$(printf '%s:%s' "${OSSRH_USERNAME}" "${OSSRH_PASSWORD}" | base64 | tr -d '\n')

echo "⬆️  Step 5: Central Portal에 업로드..."
DEPLOYMENT_ID=$(curl --silent --request POST \
  --header "Authorization: Bearer ${BEARER_TOKEN}" \
  --form bundle=@android-bundle.zip \
  https://central.sonatype.com/api/v1/publisher/upload | tr -d '\r\n')

# 인증 실패시 Central Portal 은 200 에 에러 JSON 을 실어 보낸다.
# 빈 값만 검사하면 {"error":{"message":"Invalid token"}} 를 배포 ID 로 오인한다.
case "$DEPLOYMENT_ID" in
    ''|*'"error"'*|*'<html'*)
        echo "❌ 업로드 실패: ${DEPLOYMENT_ID:-(빈 응답)}"
        echo "   토큰이 만료됐다면 https://central.sonatype.com/account 에서 재발급 후"
        echo "   local.properties 의 ossrhToken / ossrhTokenPassword 를 갱신하세요."
        exit 1
        ;;
esac

echo "✅ 업로드 성공!"
echo "📋 Deployment ID: $DEPLOYMENT_ID"

echo "⏳ Step 6: 배포 상태 확인 중..."
sleep 5

echo "📊 Step 7: 상태 조회..."
STATUS_RESPONSE=$(curl --silent --request POST \
  --header "Authorization: Bearer ${BEARER_TOKEN}" \
  "https://central.sonatype.com/api/v1/publisher/status?id=${DEPLOYMENT_ID}")

echo "📄 배포 상태:"
echo "$STATUS_RESPONSE" | jq .

# 상태 확인
DEPLOYMENT_STATE=$(echo "$STATUS_RESPONSE" | jq -r '.deploymentState')
echo ""
echo "========================================"
echo "🎯 현재 상태: $DEPLOYMENT_STATE"

case $DEPLOYMENT_STATE in
    "PENDING")
        echo "⏳ 검증 대기 중입니다."
        ;;
    "VALIDATING")
        echo "🔍 검증 진행 중입니다."
        ;;
    "VALIDATED")
        echo "✅ 검증 완료! 수동 배포가 필요합니다."
        echo "🚀 자동 배포를 시도합니다..."
        
        PUBLISH_RESPONSE=$(curl --silent --request POST \
          --header "Authorization: Bearer ${BEARER_TOKEN}" \
          --write-out "HTTPSTATUS:%{http_code}" \
          "https://central.sonatype.com/api/v1/publisher/deployment/${DEPLOYMENT_ID}")
        
        HTTP_STATUS=$(echo $PUBLISH_RESPONSE | grep -o "HTTPSTATUS:[0-9]*" | cut -d: -f2)
        
        if [ "$HTTP_STATUS" -eq "204" ]; then
            echo "🎉 배포 시작됨! Maven Central에 곧 반영됩니다."
        else
            echo "⚠️  수동 배포 실패. Central Portal에서 수동으로 배포하세요."
        fi
        ;;
    "PUBLISHING")
        echo "🚀 Maven Central에 배포 중입니다."
        ;;
    "PUBLISHED")
        echo "🎉 배포 완료! Maven Central에서 사용 가능합니다."
        ;;
    "FAILED")
        echo "❌ 배포 실패!"
        echo "🔍 오류 내용:"
        echo "$STATUS_RESPONSE" | jq '.errors'
        ;;
    *)
        echo "❓ 알 수 없는 상태: $DEPLOYMENT_STATE"
        ;;
esac

echo ""
echo "========================================"
echo "🌐 Central Portal 확인: https://central.sonatype.com/"
echo "📋 Deployment ID: $DEPLOYMENT_ID"
echo "🏁 스크립트 완료!"
