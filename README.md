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
- Docker & Docker Compose
- Gradle 8.5+ (또는 포함된 Gradle Wrapper 사용)

### Docker로 PostgreSQL과 Redis 실행

프로젝트 루트에서 다음 명령어를 실행하세요:

```bash
# PostgreSQL과 Redis 컨테이너 시작
docker-compose up -d

# 컨테이너 상태 확인
docker-compose ps

# 로그 확인
docker-compose logs -f

# 컨테이너 중지
docker-compose down

# 컨테이너 중지 및 데이터 삭제
docker-compose down -v
```

#### 데이터베이스 접속 정보

- **PostgreSQL**
  - Host: `localhost`
  - Port: `5432`
  - Database: `dating`
  - Username: `postgres`
  - Password: `postgres`

- **Redis**
  - Host: `localhost`
  - Port: `6379`

### 환경 변수 설정 (선택사항)

기본값으로 Docker Compose 설정과 동일하게 구성되어 있습니다.
필요시 환경 변수로 오버라이드할 수 있습니다:

```bash
# Database
export DATABASE_URL=jdbc:postgresql://localhost:5432/dating
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres

# Redis
export REDIS_HOST=localhost
export REDIS_PORT=6379

# JWT
export JWT_SECRET=your-secret-key-min-256-bits
export JWT_ACCESS_TOKEN_VALIDITY=1800000
export JWT_REFRESH_TOKEN_VALIDITY=1209600000

# FCM (Optional)
export FCM_CREDENTIALS_PATH=firebase-credentials.json
```

또는 `src/main/resources/application-local.yml` 파일을 생성하여 설정할 수 있습니다:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dating
    username: postgres
    password: postgres

  data:
    redis:
      host: localhost
      port: 6379

jwt:
  secret: your-local-secret-key-min-256-bits
```

### 실행

```bash
# 1. Docker 컨테이너 시작
docker-compose up -d

# 2. 애플리케이션 빌드
./gradlew build

# 3. 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 실행
java -jar build/libs/dating-app-0.0.1-SNAPSHOT.jar
```

애플리케이션이 `http://localhost:8080`에서 실행됩니다.

### 개발 워크플로우

```bash
# 1. Docker 컨테이너 시작
docker-compose up -d

# 2. 개발 모드로 실행 (코드 변경 시 자동 재시작)
./gradlew bootRun

# 3. 작업 완료 후 컨테이너 정리
docker-compose down
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

## API 사용 예제

### 1. 회원가입

```bash
curl -X POST http://localhost:8080/api/v1/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678"
  }'
```

### 2. 로그인

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

응답에서 받은 `accessToken`을 저장하세요.

### 3. 프로필 생성

```bash
curl -X POST http://localhost:8080/api/v1/profiles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "nickname": "홍길동",
    "birthDate": "1995-03-15",
    "gender": "MALE",
    "bio": "안녕하세요!",
    "location": "서울"
  }'
```

### 4. 추천 후보 조회

```bash
curl -X GET http://localhost:8080/api/v1/matches/candidates \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
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

## 데이터베이스 관리

### PostgreSQL 직접 접속

```bash
# Docker 컨테이너를 통해 접속
docker exec -it dating-postgres psql -U postgres -d dating

# 또는 로컬 psql 클라이언트 사용
psql -h localhost -p 5432 -U postgres -d dating
```

### 유용한 SQL 명령어

```sql
-- 모든 테이블 목록
\dt

-- 특정 테이블 구조 확인
\d users

-- 사용자 수 확인
SELECT COUNT(*) FROM users;
```

### Redis 직접 접속

```bash
# Docker 컨테이너를 통해 접속
docker exec -it dating-redis redis-cli

# 또는 로컬 redis-cli 사용
redis-cli -h localhost -p 6379
```

### 유용한 Redis 명령어

```bash
# 모든 키 확인
KEYS *

# 특정 패턴의 키 확인
KEYS refresh:*

# 키의 값 확인
GET refresh:1

# TTL 확인
TTL refresh:1
```

## 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 실행
./gradlew test --tests "com.dating.auth.*"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

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

Railway 대시보드에서 환경 변수를 설정하세요:
- `DATABASE_URL`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`

## 문제 해결

### PostgreSQL 연결 실패

```bash
# 컨테이너 상태 확인
docker-compose ps

# PostgreSQL 로그 확인
docker-compose logs postgres

# 컨테이너 재시작
docker-compose restart postgres
```

### Redis 연결 실패

```bash
# Redis 로그 확인
docker-compose logs redis

# 컨테이너 재시작
docker-compose restart redis
```

### 포트 충돌

이미 5432 또는 6379 포트를 사용 중인 경우:

```bash
# 사용 중인 포트 확인
lsof -i :5432
lsof -i :6379

# docker-compose.yml에서 포트 변경
# 예: "5433:5432"로 변경 후 애플리케이션 설정도 함께 변경
```

## 라이선스

MIT License
