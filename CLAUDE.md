# Bootpay Android SDK - Claude Code 지침

## 프로젝트 구조
- `core/` - Bootpay Android 라이브러리 (Maven Central에 배포)
- `app/` - 샘플 앱 및 테스트용

## 배포 프로세스

### 버전 업그레이드 및 배포 시 필수 작업

1. **버전 업그레이드**
   - `publish.gradle` 파일의 `PUBLISH_VERSION` 수정

2. **CHANGELOG.md 업데이트** (필수!)
   - 새 버전 섹션을 파일 최상단에 추가
   - 형식:
     ```markdown
     ## X.Y.Z
     * 변경사항 1
     * 변경사항 2
     ```

3. **Git 커밋 & 푸시**
   - 변경 파일들과 함께 커밋
   - 커밋 메시지에 버전 정보 포함

4. **Maven Central 배포**
   - `./gradlew :core:publishToMavenLocal` 로 로컬 빌드
   - Central Portal API로 업로드:
     ```bash
     TOKEN=$(echo -n "username:password" | base64)
     curl -X POST 'https://central.sonatype.com/api/v1/publisher/upload?name=bootpay-android-VERSION&publishingType=AUTOMATIC' \
       -H "Authorization: UserToken $TOKEN" \
       -F 'bundle=@ZIP_FILE_PATH'
     ```

### 주요 파일 위치
- 버전 정보: `publish.gradle` → `PUBLISH_VERSION`
- 변경 이력: `CHANGELOG.md`
- Sonatype 인증: `local.properties` → `ossrhToken`, `ossrhTokenPassword`

## 코드 스타일
- Java 8 호환성 유지
- AndroidX 사용
- 기존 코드 패턴 따르기

## 결제 API
- 권장: `Bootpay.init(Activity)` - Activity 기반 결제
- Deprecated: `Bootpay.init(FragmentManager)` - Dialog 기반 결제
