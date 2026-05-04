## Unreleased
- example: 결제/인증 payload 예제를 client_key 기준으로 전환하고 local.properties production fallback을 유지
- legacy application_id/private_key 토큰 헬퍼는 호환용으로 유지

### 5.1.1
- `BootpayUrlHelper` URL 라우팅 개선 (나이스페이 앱카드 관련)
  - `isSpecialCase()`에 `kakaobank://`, `monimopay://`, `smcard://` 스킴 추가
    - 기존: 매칭되는 분기가 없어 `shouldOverrideUrlLoading`이 false를 반환 → 버튼 눌러도 무반응
  - 삼성카드 mPOCKET 미설치 + 삼성 모니모 설치 상태에서 `mpocket.online.ansimclick://`를 모니모로 자동 라우팅
    - mPOCKET 고정 지정으로 결제가 끊기던 문제 해결
  - `getIntentWithPackage()`에 패키지 매핑 추가:
    - `kakaobank://` → `com.kakaobank.channel`
    - `monimopay://`, `smcard://` → `net.ib.android.smcard`
  - `startGooglePlay()`에 Android 표준 `browser_fallback_url` (S.browser_fallback_url) 지원 추가
  - 모든 `context.startActivity()`에 `FLAG_ACTIVITY_NEW_TASK` 보강

### 5.1.0
- webview CDN URL을 5.3.0으로 업데이트
- client_key 인증 방식 추가 (기존 application_id 방식과 병행 지원)
- minSdk 16 → 24로 상향 (Android 7.0+)
- Java 호환성 1.8 → 11로 업데이트
- Android Gradle Plugin 8.2.2 → 8.5.0 업데이트
- Gradle 8.5 → 8.7 업데이트
- androidx.appcompat alpha → 1.7.0 stable로 변경

## 5.0.0
* android 35 support
* js version 5.1.4 적용

## 4.9.1
* 앱 스키마 추가 


## 4.9.0
* 위젯준비, 본인인증 닫기 옵션 추가 

## 4.4.3
* ssl error 개선 

## 4.4.2
* direct app card 호출 
* intent scheme added
* js version update

## 4.4.1
* extra field added
* intent scheme bug fixed

## 4.4.0
* js version update 
* 이벤트 못받는 현상, 결제 후 흰 화면 현상 bug fixed 

## 4.3.9
* webapp의 경우 redirect 모드일 때 창이 안닫히는 버그 수정 

## 4.3.4
* open_type popup 결제요청시 웹앱에서 웹뷰 닫히는 현상 개선 

## 4.3.3
* publish.gradle update 

## 4.3.2
* domeStrage 옵션 활성화 - 비인증 정기결제 관련 버그 개선 

## 4.3.1
* metadata 송신 버그 수정

## 4.3.0
* escrow 옵션 추가 

## 4.2.9
* 4.2.8에 영향받은 popup 뷰 이벤트 누락되지 않도록 개선 

## 4.2.8
* bootpay webview pause 관련 버그 패치 

## 4.2.7
* 카드자동결제 결제수단 등록 후 조건부적 안닫히는 현상 개선

## 4.2.6
* 본인인증 age_limit default 값 0 으로 셋팅
* 카드자동결제 요청시 100원 결제 옵션 extra.subscribe_test_payment 추가
 

## 4.2.5
* bootpay js 4.2.6 update 

## 4.2.4
* resume, pause시에 웹뷰 코드 추가 

## 4.2.3
* close 이벤트 전달시 data 를 더 이상 전달하지 않도록 변경 

## 4.2.2
* js 4.2.2 업데이트 
* 외부앱 실행시 new task 추가 

## 4.2.1
* methods 적용 안되는 버그 수정 

## 4.2.0
* js 4.2.0 업데이트 
* progress bar 추가
* 앱스키마 추가 

## 4.0.8
* 비밀번호 간편결제 추가 
* bootpay js 4.0.7 적용 

## 4.0.7
* redirect cancel시 close 실행 
* 네이버페이 뒤로가기 버튼 제거 

## 4.0.6
* bootpay js 4.0.6 적용 
* openType redirect default 적용 

## 4.0.2
* metadata 송신 버그 수정 

## 4.0.1
* payload params -> metadata renamed 

## 4.0.0
* bootpay js major update   
