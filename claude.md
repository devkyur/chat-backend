# Dating App Backend

## 기술 스택
- Java 17 + Spring Boot 3.x + Gradle (Kotlin DSL)
- Spring Data JPA + QueryDSL
- Spring WebSocket + STOMP
- Spring Security + JWT
- PostgreSQL + Redis
- 배포: Railway

## 프로젝트 구조

```
src/main/java/com/dating/
├── auth/           # 인증 (login, signup, token)
├── user/           # 사용자 계정
├── profile/        # 프로필 관리
├── match/          # 매칭 시스템
├── chat/           # 실시간 채팅
├── notification/   # 푸시 알림 (FCM)
└── common/         # 공통 (config, exception, security, util)

각 모듈 내부:
├── controller/
├── service/
├── repository/
├── domain/         # Entity
└── dto/
```

## 핵심 규칙

### 계층 분리
- **Controller**: Request/Response 처리만. 로직 금지
- **Service**: 비즈니스 로직, 트랜잭션
- **Repository**: DB 접근만

### Entity
- `@Setter` 금지 → `@Builder` 또는 생성자 사용
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
- 상태 변경은 비즈니스 메서드로

### DTO
- 재사용 금지. 용도별 분리 필수
- 네이밍: `Create___Request`, `Update___Request`, `___Response`

### JPA
- 연관관계: 무조건 `FetchType.LAZY`
- N+1 해결: fetch join, @BatchSize(100), @EntityGraph
- 동적 쿼리: QueryDSL

### 예외 처리
- `BusinessException` + `ErrorCode` enum
- `@RestControllerAdvice`로 전역 처리
- 응답: `{ "success": bool, "data": T, "error": { "code", "message" } }`

### JWT
- Access Token: 30분 (클라이언트 메모리)
- Refresh Token: 14일 (Redis 저장)
- 로그아웃 시 Redis에서 삭제

### Redis 용도
| Key Pattern | 용도 | TTL |
|-------------|------|-----|
| `refresh:{userId}` | Refresh Token | 14일 |
| `online:{userId}` | 온라인 상태 | 5분 |
| `chat:room:{roomId}` | Pub/Sub 채널 | - |
| `chat:read:{roomId}:{userId}` | 읽음 위치 | 30일 |

### 채팅 아키텍처
```
Flutter ←WebSocket(STOMP)→ Spring Boot ←Redis Pub/Sub→ Scale Out
                                ↓
                           PostgreSQL (메시지 저장)
```
- 연결: `/ws/chat`
- 구독: `/topic/chat/{roomId}`
- 발행: `/app/chat/{roomId}/send`

### 이미지 업로드
- Presigned URL 방식 (Cloudflare R2)
- 서버는 URL만 저장, Flutter가 R2에 직접 업로드

## API 규칙

### Base
- URL: `/api/v1/...`
- 인증: `Authorization: Bearer {accessToken}`

### 주요 엔드포인트
```
POST   /auth/signup, /auth/login, /auth/refresh, /auth/logout
GET    /profiles/me
PATCH  /profiles/me
GET    /matches/candidates
POST   /matches/{id}/like, /matches/{id}/pass
GET    /chat/rooms
GET    /chat/rooms/{roomId}/messages
```

### 응답 형식
```json
{ "success": true, "data": {}, "error": null }
{ "success": false, "data": null, "error": { "code": "U001", "message": "..." } }
```

## 테스트
- Service: `@ExtendWith(MockitoExtension.class)` + Mockito
- Repository: `@DataJpaTest`

## Git 커밋
```
feat: 기능 추가
fix: 버그 수정
refactor: 리팩토링
test: 테스트
docs: 문서
```

## Frontend 연동
- Flutter (Riverpod + Dio + web_socket_channel) (나중에 Vue.js로 변경 가능성 있음)
- FCM으로 푸시 알림
- 401 응답 시 `/auth/refresh` 호출 → 실패 시 재로그인
