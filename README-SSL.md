# HTTPS/SSL 설정 가이드

## 문제 상황

프론트엔드에서 `https://localhost:8080`로 요청 시 다음 에러 발생:
```
ERR_SSL_PROTOCOL_ERROR
```

백엔드 로그:
```
java.lang.IllegalArgumentException: Invalid character found in method name
```

**원인**: 프론트엔드는 HTTPS로 요청하지만 백엔드는 HTTP만 지원

## 해결 방법

### 방법 1: HTTP 사용 (권장 - 로컬 개발)

프론트엔드에서 URL을 변경:
```javascript
// 변경 전
const API_URL = 'https://localhost:8080';

// 변경 후
const API_URL = 'http://localhost:8080';
```

### 방법 2: 백엔드에 HTTPS 설정

#### 1단계: SSL 인증서 생성

```bash
# 스크립트 실행
./generate-ssl-cert.sh

# 또는 직접 생성
keytool -genkeypair \
  -alias dating-app \
  -keyalg RSA \
  -keysize 2048 \
  -storetype PKCS12 \
  -keystore src/main/resources/ssl/keystore.p12 \
  -validity 365 \
  -storepass changeit \
  -keypass changeit \
  -dname "CN=localhost, OU=Development, O=DatingApp, L=Seoul, ST=Seoul, C=KR"
```

#### 2단계: application.yml 수정

`src/main/resources/application.yml`에서 SSL 설정 주석 해제:

```yaml
server:
  port: 8443  # HTTPS 기본 포트
  ssl:
    enabled: true
    key-store: classpath:ssl/keystore.p12
    key-store-password: changeit
    key-store-type: PKCS12
    key-alias: dating-app
```

#### 3단계: 애플리케이션 재시작

```bash
./gradlew bootRun
```

이제 `https://localhost:8443`으로 접속 가능합니다.

#### 4단계: 프론트엔드 URL 변경

```javascript
const API_URL = 'https://localhost:8443';
```

## 브라우저 경고 무시

자체 서명 인증서를 사용하므로 브라우저에서 보안 경고가 표시됩니다.

**Chrome/Edge**:
1. "고급" 클릭
2. "localhost(으)로 이동" 클릭

**Firefox**:
1. "고급..." 클릭
2. "위험을 감수하고 계속" 클릭

## 환경 변수로 제어

`.env` 또는 환경 변수:

```bash
# HTTPS 활성화
SSL_ENABLED=true
PORT=8443

# HTTP 사용 (기본값)
SSL_ENABLED=false
PORT=8080
```

## HTTP와 HTTPS 동시 지원

HTTP와 HTTPS를 모두 지원하려면 추가 설정이 필요합니다:

```java
// SecurityConfig.java 또는 별도 Configuration 클래스
@Bean
public ServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
        @Override
        protected void postProcessContext(Context context) {
            SecurityConstraint securityConstraint = new SecurityConstraint();
            securityConstraint.setUserConstraint("CONFIDENTIAL");
            SecurityCollection collection = new SecurityCollection();
            collection.addPattern("/*");
            securityConstraint.addCollection(collection);
            context.addConstraint(securityConstraint);
        }
    };

    tomcat.addAdditionalTomcatConnectors(redirectConnector());
    return tomcat;
}

private Connector redirectConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(8080);
    connector.setSecure(false);
    connector.setRedirectPort(8443);
    return connector;
}
```

## 프로덕션 환경

프로덕션에서는 다음을 권장합니다:

1. **인증 기관(CA)에서 발급한 인증서 사용**
   - Let's Encrypt (무료)
   - Cloudflare SSL
   - AWS Certificate Manager

2. **리버스 프록시 사용**
   - Nginx
   - Apache
   - Cloudflare

3. **Railway 배포 시**
   - Railway는 자동으로 HTTPS 제공
   - 애플리케이션은 HTTP로 실행하고 Railway가 HTTPS 처리

## 트러블슈팅

### 인증서 생성 실패
```bash
# keytool 위치 확인
which keytool

# Java 설치 확인
java -version
```

### 포트 충돌
```bash
# 포트 사용 확인
lsof -i :8443

# 다른 포트 사용
PORT=9443 ./gradlew bootRun
```

### 브라우저 캐시
브라우저 캐시를 지우고 다시 시도하세요.
