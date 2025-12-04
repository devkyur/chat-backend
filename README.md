# Dating App Backend

Spring Boot 기반 데이팅 앱 백엔드 API

## 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Gradle (Kotlin DSL)**
- **Spring Data JPA** + **QueryDSL**
- **Spring WebSocket** + **STOMP**
- **Spring Security** + **JWT**
- **PostgreSQL**
- **Redis**
- **Firebase Cloud Messaging (FCM)**

## 프로젝트 구조

```
src/main/java/com/dating/
├── auth/           # 인증 (회원가입, 로그인, 토큰 관리)
├── user/           # 사용자 계정 관리
├── profile/        # 프로필 관리
├── match/          # 매칭 시스템 (like, pass)
├── chat/           # 실시간 채팅 (WebSocket)
├── notification/   # 푸시 알림 (FCM)
└── common/         # 공통 모듈
    ├── config/     # 설정 (Security, Redis, WebSocket, QueryDSL)
    ├── exception/  # 예외 처리
    ├── security/   # JWT 인증
    └── util/       # 유틸리티
```

## 시작하기

### 사전 요구사항

- Java 17+
- PostgreSQL 12+
- Redis 6+
- Gradle 8.5+

### 데이터베이스 설정

```sql
CREATE DATABASE dating_app;
```

### 환경 변수 설정

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/dating_app
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-secret-key-min-256-bits
JWT_ACCESS_TOKEN_VALIDITY=1800000
JWT_REFRESH_TOKEN_VALIDITY=1209600000

# FCM (Optional)
FCM_CREDENTIALS_PATH=firebase-credentials.json
```

또는 `src/main/resources/application-local.yml` 파일을 생성하여 설정할 수 있습니다.

### 실행

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/dating-app-0.0.1-SNAPSHOT.jar
```

## API 엔드포인트

### 인증 (Auth)

```
POST   /api/v1/auth/signup      # 회원가입
POST   /api/v1/auth/login       # 로그인
POST   /api/v1/auth/refresh     # 토큰 갱신
POST   /api/v1/auth/logout      # 로그아웃
```

### 프로필 (Profile)

```
POST   /api/v1/profiles         # 프로필 생성
GET    /api/v1/profiles/me      # 내 프로필 조회
GET    /api/v1/profiles/{id}    # 프로필 조회
PATCH  /api/v1/profiles/me      # 프로필 수정
```

### 매칭 (Match)

```
GET    /api/v1/matches/candidates     # 추천 후보 조회
POST   /api/v1/matches/{id}/like      # 좋아요
POST   /api/v1/matches/{id}/pass      # 패스
GET    /api/v1/matches                # 매칭 목록
```

### 채팅 (Chat)

```
GET    /api/v1/chat/rooms                    # 채팅방 목록
POST   /api/v1/chat/rooms?matchId={id}       # 채팅방 생성
GET    /api/v1/chat/rooms/{id}/messages      # 메시지 조회

# WebSocket
CONNECT /ws/chat                              # WebSocket 연결
SUBSCRIBE /topic/chat/{roomId}                # 채팅방 구독
SEND /app/chat/{roomId}/send                  # 메시지 전송
```

### 알림 (Notification)

```
POST   /api/v1/notifications/tokens    # FCM 토큰 등록
DELETE /api/v1/notifications/tokens    # FCM 토큰 삭제
```

## API 응답 형식

### 성공

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

### 실패

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "U001",
    "message": "User not found"
  }
}
```

## 인증

모든 API 요청은 JWT 토큰을 통해 인증됩니다 (인증 엔드포인트 제외).

```
Authorization: Bearer {accessToken}
```

## Redis 사용

| Key Pattern | 용도 | TTL |
|-------------|------|-----|
| `refresh:{userId}` | Refresh Token 저장 | 14일 |
| `online:{userId}` | 온라인 상태 | 5분 |
| `chat:room:{roomId}` | Pub/Sub 채널 | - |
| `chat:read:{roomId}:{userId}` | 읽음 위치 | 30일 |

## 배포

### Railway

```bash
# Railway CLI 설치
npm install -g @railway/cli

# 로그인
railway login

# 프로젝트 초기화
railway init

# 배포
railway up
```

환경 변수는 Railway 대시보드에서 설정하세요.

## 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.dating.auth.*"
```

## 라이선스

MIT License
