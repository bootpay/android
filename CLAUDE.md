# Bootpay Android Core SDK

`io.github.bootpay:android` (Maven Central, Sonatype Central Portal).

## 배포 시 버전 동기화 체크리스트 (CRITICAL)

패키지 버전과 **런타임 VERSION 상수**가 어긋나면 webview/analytics 에 옛 버전이 보고된다. 한 곳만 올리면 안 된다.

| 파일 | 상수 | 비고 |
|------|------|------|
| `publish.gradle` | `PUBLISH_VERSION` | Maven 배포 버전 |
| `core/src/main/java/kr/co/bootpay/android/constants/BootpayBuildConfig.java` | `VERSION` | webview `setVersion()` / analytics 송신 값 |
| `CHANGELOG.md` | — | 새 버전 항목 추가 |

CDN URL 변경 시 추가:
- `core/src/main/java/kr/co/bootpay/android/constants/BootpayConstant.java` → `CDN_URL` (현재 `https://webview.bootpay.co.kr/5.3.0/`)

## 배포 절차

`./deploy.sh` 가 다음을 자동 처리:
1. `core:publishReleasePublicationToLocalRepoRepository` 로 publication 빌드
2. zip bundle 생성
3. Sonatype Central Portal 에 bearer token 으로 업로드
4. VALIDATED 상태일 때 자동 publish 트리거

이후 수동:
- `git tag <version> && git push origin <version>`

## 환경 기본값

`BootpayConstant.ENVIRONMENT_MODE` 기본값은 `"production"`. `Bootpay.setEnvironmentMode("development" | "stage" | "production")` 으로 런타임 토글.
