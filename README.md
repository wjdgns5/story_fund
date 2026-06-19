# StoryFund

홈페이지
https://mieotwd.shop

API 문서 (Swagger)
https://mieotwd.shop/swagger-ui.html


> 유료 게시판 플랫폼 — JWT 인증 · 카카오 소셜 로그인 · Toss 결제
>
> 신입 개발자 포트폴리오 프로젝트

---

## 📌 프로젝트 소개

StoryFund 는 창작자가 게시글을 작성하고 유료로 설정할 수 있는 게시판 플랫폼이에요.
독자는 코인을 충전해서 유료 게시글을 열람할 수 있어요.

### 핵심 기능

```
✅ 이메일 회원가입 + 이메일 인증 (Redis TTL)
✅ JWT Access Token + Refresh Token 인증
✅ 카카오 OAuth2 소셜 로그인
✅ 게시판 CRUD + 소프트 삭제 + 페이지네이션 + 검색
✅ 유료 게시글 잠금 / 코인 차감 열람 (서버 검증)
✅ Toss Payments 코인 충전 결제 (금액 위변조 방지)
✅ 마이페이지 (내 정보, 게시글, 결제/열람 내역)
✅ Swagger API 문서
```

---

## 🛠️ 기술 스택

### Backend

| 기술                 | 버전   | 용도                       |
| -------------------- | ------ | -------------------------- |
| Spring Boot          | 4.0.6  | 웹 애플리케이션 프레임워크 |
| Spring Security      | 7.x    | 인증/인가, JWT 필터        |
| Spring Data JPA      | 4.x    | ORM, DB 접근               |
| Spring Data Redis    | 4.x    | 이메일 인증 코드 캐시      |
| Spring Mail          | 4.x    | Gmail SMTP 이메일 발송     |
| Spring OAuth2 Client | 4.x    | 카카오 소셜 로그인         |
| jjwt                 | 0.12.6 | JWT 생성/검증              |
| springdoc-openapi    | 2.8.8  | Swagger API 문서           |
| MySQL                | 8.0    | 메인 데이터베이스          |
| Redis                | 7.x    | 이메일 인증 TTL 저장       |
| Lombok               | -      | 코드 간소화                |
| Java                 | 21     | 언어                       |
| Gradle               | -      | 빌드 도구                  |

### Frontend

| 패키지                         | 버전   | 용도                       |
| ------------------------------ | ------ | -------------------------- |
| react                          | 19.2.x | SPA 프레임워크             |
| react-dom                      | 19.2.x | React DOM 렌더링           |
| vite                           | 8.0.x  | 빌드 도구 / 개발 서버      |
| react-router-dom               | 7.17.x | 클라이언트 라우팅          |
| axios                          | 1.17.x | HTTP 클라이언트 + 인터셉터 |
| @tosspayments/tosspayments-sdk | 2.7.x  | Toss 결제 위젯             |

설치 명령어

```bash
npm install react react-dom react-router-dom axios @tosspayments/tosspayments-sdk
```

> Vite 프로젝트 생성 시 react, react-dom, vite 는 기본 포함돼요.
> 추가로 설치한 패키지는 `react-router-dom`, `axios`, `@tosspayments/tosspayments-sdk` 3개예요.

### 외부 서비스

| 서비스        | 용도             |
| ------------- | ---------------- |
| Kakao OAuth2  | 소셜 로그인      |
| Toss Payments | 결제 위젯        |
| Gmail SMTP    | 이메일 인증 발송 |

---

## 📁 프로젝트 구조

### Backend

```
api/src/main/
├── java/com/storyfund/api/
│   ├── config/
│   │   ├── AppConfig.java                # BCrypt 설정
│   │   ├── GlobalExceptionHandler.java   # 전역 예외 처리
│   │   ├── RedisConfig.java              # Redis 직렬화 설정
│   │   ├── SecurityConfig.java           # Spring Security 7 설정
│   │   └── SwaggerConfig.java            # Swagger API 문서 설정
│   │
│   ├── controller/
│   │   ├── BoardController.java          # 게시판 API (/api/boards)
│   │   ├── MyPageController.java         # 마이페이지 API (/api/users)
│   │   ├── PaymentController.java        # 결제 API (/api/payments)
│   │   └── UserController.java           # 인증 API (/api/auth)
│   │
│   ├── dto/                              # 요청/응답 데이터 클래스
│   ├── entity/                           # User, Board, Payment, UnlockHistory
│   ├── repository/                       # JPA Repository
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java  # JWT 요청 필터
│   │   └── JwtTokenProvider.java         # JWT 생성/검증
│   └── service/                          # 비즈니스 로직
│
└── resources/
    └── application.yml                   # 환경변수 매핑 설정

api/
├── .env                                  # 실제 환경변수 값 (Git 제외)
└── .gitignore
```

### Frontend

```
storyfund-front/
├── src/
│   ├── api/
│   │   └── axios.js                  # Axios 인스턴스 + 인터셉터
│   │
│   ├── pages/
│   │   ├── LoginPage.jsx             # 로그인 (일반 + 카카오)
│   │   ├── SignupPage.jsx            # 회원가입 + 이메일 인증
│   │   ├── BoardListPage.jsx         # 게시글 목록 + 검색
│   │   ├── BoardDetailPage.jsx       # 게시글 상세 + 유료 잠금
│   │   ├── BoardCreatePage.jsx       # 게시글 작성
│   │   ├── BoardEditPage.jsx         # 게시글 수정
│   │   ├── PaymentPage.jsx           # 코인 충전 (Toss 위젯)
│   │   ├── PaymentSuccessPage.jsx    # 결제 완료
│   │   ├── PaymentFailPage.jsx       # 결제 실패
│   │   ├── MyPage.jsx                # 마이페이지
│   │   └── OAuthCallbackPage.jsx     # 카카오 로그인 콜백
│   │
│   ├── App.jsx                       # 라우팅 설정
│   └── main.jsx                      # 앱 진입점
│
├── public/
│   └── image/kakao.png               # 카카오 로그인 버튼 아이콘
│
├── .env                               # 실제 환경변수 값 (Git 제외)
├── .gitignore
└── package.json
```

---

## ⚙️ 로컬 실행 방법

### 사전 준비

```
- Java 21
- MySQL 8.0
- Redis (WSL + Ubuntu 권장)
- Node.js 18+
- IntelliJ IDEA
```

---

### 1. 프로젝트 클론

```bash
git clone https://github.com/본인계정/storyfund.git
cd storyfund
```

---

### 2. MySQL 데이터베이스 생성

```sql
CREATE DATABASE storyfund_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

---

### 3. Redis 실행 (WSL Ubuntu)

```bash
sudo service redis-server start
redis-cli ping   # PONG 확인
```

---

## 🔐 백엔드 환경변수 설정 (.env)

`api/api/` 루트에 `.env` 파일을 생성해주세요. (build.gradle 이 있는 폴더)

```
api/api/
├── .env          ← 여기
├── build.gradle
└── src/
```

### .env 내용

```env
# JWT
JWT_SECRET=storyfund-secret-key-must-be-at-least-32-characters-long

# MySQL
DB_URL=jdbc:mysql://localhost:3306/storyfund_db?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_NAME=root
DB_PASSWORD=본인_MySQL_비밀번호

# Gmail SMTP
MAIL_ID=본인Gmail주소@gmail.com
MAIL_PASSWORD=Gmail앱비밀번호16자리

# 카카오 소셜 로그인
KAKAO_CLIENT_ID=카카오_REST_API_키
KAKAO_CLIENT_SECRET=카카오_Client_Secret

# Toss Payments
TOSS_SECRET_KEY=test_sk_...

# 프론트엔드 주소 (CORS, 카카오 리다이렉트에 사용)
FRONT_URL=http://localhost:5173
```

### application.yml 과의 매핑

`.env` 의 값은 `application.yml` 에서 `${변수명}` 형태로 참조돼요.

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_NAME}
    password: ${DB_PASSWORD}

  mail:
    username: ${MAIL_ID}
    password: ${MAIL_PASSWORD}

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: ${KAKAO_CLIENT_ID}
            client-secret: ${KAKAO_CLIENT_SECRET}

jwt:
  secret: ${JWT_SECRET}

toss:
  secret-key: ${TOSS_SECRET_KEY}

app:
  front-url: ${FRONT_URL:http://localhost:5173} # 기본값 포함
```

### IntelliJ 에서 .env 읽는 법

```
Run → Edit Configurations → ApiApplication
→ Environment variables 오른쪽 폴더 아이콘 클릭
→ "Load from file" 선택
→ .env 파일 선택 → OK
```

> 또는 Environment variables 칸에 9개 변수를 직접 입력해도 돼요.

| 변수명                | 설명                                     |
| --------------------- | ---------------------------------------- |
| `JWT_SECRET`          | JWT 서명 키 (32자 이상)                  |
| `DB_URL`              | MySQL 접속 URL                           |
| `DB_NAME`             | MySQL 사용자명                           |
| `DB_PASSWORD`         | MySQL 비밀번호                           |
| `MAIL_ID`             | Gmail 주소                               |
| `MAIL_PASSWORD`       | Gmail 앱 비밀번호 (16자리)               |
| `KAKAO_CLIENT_ID`     | 카카오 REST API 키                       |
| `KAKAO_CLIENT_SECRET` | 카카오 Client Secret                     |
| `TOSS_SECRET_KEY`     | Toss 테스트/운영 시크릿 키               |
| `FRONT_URL`           | 프론트엔드 주소 (CORS, OAuth 리다이렉트) |

---

## 🔐 프론트엔드 환경변수 설정 (.env)

`storyfund-front/` 루트에 `.env` 파일을 생성해주세요. (package.json 이 있는 폴더)

```
storyfund-front/
├── .env          ← 여기
├── package.json
└── src/
```

### .env 내용

```env
VITE_API_URL=http://localhost:8080
VITE_KAKAO_CLIENT_ID=카카오_REST_API_키
VITE_KAKAO_REDIRECT_URI=http://localhost:8080/api/auth/kakao
VITE_TOSS_CLIENT_KEY=test_ck_...
```

> Vite 는 `VITE_` 로 시작하는 변수만 코드에서 읽을 수 있어요.
> 코드에서는 `import.meta.env.VITE_API_URL` 형태로 사용해요.

| 변수명                    | 설명                                  |
| ------------------------- | ------------------------------------- |
| `VITE_API_URL`            | 백엔드 API 주소 (axios baseURL)       |
| `VITE_KAKAO_CLIENT_ID`    | 카카오 REST API 키 (백엔드와 동일 키) |
| `VITE_KAKAO_REDIRECT_URI` | 카카오 인가 코드를 받을 백엔드 URL    |
| `VITE_TOSS_CLIENT_KEY`    | Toss 테스트/운영 클라이언트 키        |

### npm install

```bash
cd storyfund-front
npm install
```

`package.json` 에 명시된 패키지 전체가 설치돼요.

```json
{
  "dependencies": {
    "@tosspayments/tosspayments-sdk": "^2.7.1",
    "axios": "^1.17.0",
    "react": "^19.2.6",
    "react-dom": "^19.2.6",
    "react-router-dom": "^7.17.0"
  }
}
```

---

## 🚀 서버 실행

**백엔드**

```
IntelliJ 에서 ApiApplication.java → Run (▶)
Started ApiApplication in x.xxx seconds 확인
```

**프론트엔드**

```bash
cd storyfund-front
npm run dev
```

브라우저에서 `http://localhost:5173` 접속

---

## 🔑 외부 서비스 키 발급 방법

### Gmail 앱 비밀번호

```
1. https://myaccount.google.com 접속
2. 보안 → 2단계 인증 → 사용 설정 (필수)
3. 보안 → 2단계 인증 → 앱 비밀번호
4. 앱 이름: storyfund → 생성
5. 16자리 비밀번호 복사 (공백 제거) → MAIL_PASSWORD
```

### 카카오 REST API 키 + Client Secret

```
1. https://developers.kakao.com 접속
2. 내 애플리케이션 → 애플리케이션 추가
3. 앱 키 탭 → REST API 키 복사 → KAKAO_CLIENT_ID / VITE_KAKAO_CLIENT_ID
4. 카카오 로그인 → 활성화 ON
5. Redirect URI 등록: http://localhost:8080/api/auth/kakao
6. 동의항목 → 이메일 필수 동의
7. 보안 → Client Secret → 코드 생성 → KAKAO_CLIENT_SECRET
```

### Toss Payments 키

```
1. https://developers.tosspayments.com 접속 후 가입
2. 대시보드 → 개발 연동 → API 키
3. 테스트 시크릿 키   → TOSS_SECRET_KEY (백엔드)
4. 테스트 클라이언트 키 → VITE_TOSS_CLIENT_KEY (프론트)
```

---

## 📋 API 목록

### 인증 (Auth) — /api/auth

| Method | URL              | 설명                  | 인증 |
| ------ | ---------------- | --------------------- | ---- |
| POST   | `/signup`        | 회원가입              | ❌   |
| POST   | `/login`         | 로그인                | ❌   |
| POST   | `/refresh`       | 토큰 갱신             | ❌   |
| POST   | `/logout`        | 로그아웃              | ✅   |
| GET    | `/kakao`         | 카카오 소셜 로그인    | ❌   |
| POST   | `/emails/send`   | 이메일 인증 코드 발송 | ❌   |
| POST   | `/emails/verify` | 이메일 인증 코드 확인 | ❌   |

### 게시판 (Board) — /api/boards

| Method | URL     | 설명                           | 인증 |
| ------ | ------- | ------------------------------ | ---- |
| GET    | `/`     | 목록 조회 (페이지네이션, 검색) | ❌   |
| GET    | `/{id}` | 상세 조회 (유료 잠금 처리)     | 선택 |
| POST   | `/`     | 게시글 작성                    | ✅   |
| PUT    | `/{id}` | 게시글 수정 (작성자만)         | ✅   |
| DELETE | `/{id}` | 게시글 삭제 (소프트 삭제)      | ✅   |

### 결제 (Payment) — /api/payments

| Method | URL                 | 설명                  | 인증 |
| ------ | ------------------- | --------------------- | ---- |
| POST   | `/order`            | 결제 주문 생성        | ✅   |
| POST   | `/confirm`          | 결제 검증 + 코인 충전 | ✅   |
| POST   | `/unlock/{boardId}` | 유료 게시글 열람      | ✅   |
| GET    | `/history`          | 결제 내역 조회        | ✅   |

### 마이페이지 (MyPage) — /api/users

| Method | URL            | 설명           | 인증 |
| ------ | -------------- | -------------- | ---- |
| GET    | `/me`          | 내 정보 조회   | ✅   |
| GET    | `/me/boards`   | 내 게시글 목록 | ✅   |
| GET    | `/me/unlocked` | 열람 내역      | ✅   |

> Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🗄️ 데이터베이스 설계

| 테이블             | 설명      | 주요 컬럼                                                                |
| ------------------ | --------- | ------------------------------------------------------------------------ |
| `users`            | 회원 정보 | id, email, password, nickname, role, coin, email_verified, refresh_token |
| `boards`           | 게시글    | id, user_id(FK), title, content, is_paid, view_count, deleted_at         |
| `payments`         | 결제 내역 | id, user_id(FK), order_id, amount, coin_amount, payment_key, status      |
| `unlock_histories` | 열람 기록 | id, user_id(FK), board_id(FK), unlocked_at                               |

```
users (1) ──── (N) boards
users (1) ──── (N) payments
users (1) ──── (N) unlock_histories
boards (1) ──── (N) unlock_histories
```

---

## 🌐 HTTP 배포 전 / 배포 후 설정 가이드

로컬 개발(HTTP) 과 배포(HTTPS) 환경은 설정이 달라져야 하는 부분이 있어요.
배포 직전에 아래 항목들을 꼭 확인해주세요.

### 1. Cookie Secure 옵션

```java
// UserController.java — login(), kakaoLogin()

// 배포 전 (HTTP, 로컬)
cookie.setSecure(false);

// 배포 후 (HTTPS 필수)
cookie.setSecure(true);
```

> `Secure` 옵션이 `true` 면 HTTPS 연결에서만 Cookie 가 전송돼요.
> HTTPS 적용 전에 `true` 로 바꾸면 Cookie 가 전혀 전송되지 않아요.
> **HTTPS 인증서 적용 후에** 이 값을 변경해주세요.

---

### 2. CORS 허용 Origin

```java
// SecurityConfig.java
configuration.addAllowedOrigin(frontUrl);  // app.front-url 환경변수 사용
```

```env
# 배포 전 (.env)
FRONT_URL=http://localhost:5173

# 배포 후 (.env)
FRONT_URL=https://storyfund.com   # 실제 배포 도메인
```

> 코드 수정 없이 환경변수만 바꾸면 돼요.

---

### 3. 카카오 리다이렉트 URL

```yaml
# application.yml
security:
  oauth2:
    client:
      registration:
        kakao:
          redirect-uri: http://localhost:8080/api/auth/kakao # 배포 시 도메인으로 변경
```

```
배포 후 카카오 개발자 콘솔에도 동일하게 등록 필요

https://developers.kakao.com
→ 카카오 로그인 → Redirect URI
→ https://api.storyfund.com/api/auth/kakao 추가
(기존 localhost URI 는 유지해도 무방, 같이 등록 가능)
```

---

### 4. 카카오 React 콜백 URL

```java
// UserController.kakaoLogin()
response.sendRedirect(frontUrl + "/oauth/kakao?token=" + result.getAccessToken());
```

> `frontUrl` 이 `FRONT_URL` 환경변수를 그대로 쓰기 때문에
> 위 3번 항목과 마찬가지로 `.env` 값만 바꾸면 자동 반영돼요.

---

### 5. Vite 프론트 환경변수

```env
# 배포 전 (.env)
VITE_API_URL=http://localhost:8080
VITE_KAKAO_REDIRECT_URI=http://localhost:8080/api/auth/kakao

# 배포 후 (.env.production 또는 배포 환경변수)
VITE_API_URL=https://api.storyfund.com
VITE_KAKAO_REDIRECT_URI=https://api.storyfund.com/api/auth/kakao
```

> `npm run build` 시 `.env.production` 이 있으면 자동으로 우선 적용돼요.

---

### 6. Toss Payments 키 교체

```env
# 배포 전 — 테스트 키 (가짜 결제)
TOSS_SECRET_KEY=test_sk_...
VITE_TOSS_CLIENT_KEY=test_ck_...

# 배포 후 — 운영 키 (실제 결제, 사업자 등록 + 심사 필요)
TOSS_SECRET_KEY=live_sk_...
VITE_TOSS_CLIENT_KEY=live_ck_...
```

---

### 7. JPA ddl-auto

```yaml
# 배포 전 (개발 중)
spring:
  jpa:
    hibernate:
      ddl-auto: update

# 배포 후 (운영)
spring:
  jpa:
    hibernate:
      ddl-auto: validate   # 또는 none
    show-sql: false        # 운영에서는 SQL 로그 끄기 권장
```

> `update` 를 운영에 그대로 쓰면 의도치 않은 스키마 변경이 발생할 수 있어요.
> Flyway, Liquibase 같은 마이그레이션 도구 도입을 권장해요.

---

### 8. 에러 메시지 노출

```java
// GlobalExceptionHandler.java — 이미 적용됨
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
    System.out.println("서버 에러: " + e.getMessage());  // 서버 로그에만 기록

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponseDto(500, "서버 오류가 발생했습니다."));  // 클라이언트엔 일반 메시지
}
```

> 운영 환경에서는 `System.out.println` 대신 Logback 등 로깅 프레임워크 사용을 권장해요.

---

### 📋 배포 체크리스트 요약

| 항목                | 배포 전                 | 배포 후                        |
| ------------------- | ----------------------- | ------------------------------ |
| Cookie Secure       | `false`                 | `true`                         |
| FRONT_URL           | `http://localhost:5173` | 실제 도메인 (https)            |
| 카카오 Redirect URI | localhost               | 실제 도메인 + 카카오 콘솔 등록 |
| VITE_API_URL        | localhost:8080          | 실제 API 도메인                |
| Toss 키             | test\_ 키               | live\_ 키 (심사 필요)          |
| ddl-auto            | update                  | validate / none                |
| show-sql            | true                    | false                          |
| 에러 메시지         | 콘솔 출력               | 로깅 프레임워크 권장           |

---

## 🚀 개발 시작 전 체크리스트

```
□ Ubuntu 터미널에서 Redis 시작
  sudo service redis-server start
  redis-cli ping → PONG 확인

□ api/api/.env 파일 10개 변수 설정 확인

□ storyfund-front/.env 파일 4개 변수 설정 확인

□ Spring Boot 서버 실행 확인
  Started ApiApplication in x.xxx seconds

□ React 개발 서버 실행
  npm run dev → http://localhost:5173 확인
```
