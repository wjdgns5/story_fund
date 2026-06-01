# StoryFund API

> Spring Boot 4.0.6 기반 유료 게시판 플랫폼
> JWT 인증 · 카카오 소셜 로그인 · Toss 결제

---

## 🛠️ 기술 스택

| 영역 | 기술 |
|---|---|
| Backend | Spring Boot 4.0.6, Spring Security 7, Spring Data JPA |
| 인증 | JWT (jjwt 0.12.6), OAuth2 (카카오) |
| Database | MySQL 8.0, Redis 7 |
| 이메일 | Gmail SMTP |
| 결제 | Toss Payments |
| 빌드 | Gradle |

---

## ⚙️ 로컬 실행 방법

### 사전 준비

```
- Java 21
- MySQL 8.0
- Redis (WSL + Ubuntu 권장)
- IntelliJ IDEA
```

---

### 1. 프로젝트 클론

```bash
git clone https://github.com/본인계정/storyfund-api.git
cd storyfund-api
```

---

### 2. MySQL 데이터베이스 생성

MySQL Workbench 또는 터미널에서 실행

```sql
CREATE DATABASE storyfund_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

---

### 3. Redis 실행

Ubuntu 터미널에서 실행

```bash
sudo service redis-server start
redis-cli ping
# PONG 이 나오면 정상
```

---

### 4. application.yml 설정

`src/main/resources/application.yml.example` 파일을 복사해서
`application.yml` 을 만들고 값을 채워주세요.

```bash
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

---

### 5. 환경변수 설정

아래 두 가지 방법 중 하나를 선택해주세요.

---

## 🔐 환경변수 설정 방법

### 방법 1 — IntelliJ Run Configuration (권장)

**Step 1.** IntelliJ 상단 메뉴에서

```
Run → Edit Configurations
```

**Step 2.** 왼쪽 목록에서 `ApiApplication` 선택

**Step 3.** `Environment variables` 항목 오른쪽 아이콘 클릭

```
□ Environment variables  [         ] 📋
                                      ↑
                                  이 아이콘 클릭
```

**Step 4.** `+` 버튼으로 아래 변수들 추가

| Name | Value |
|---|---|
| `JWT_SECRET` | 32자 이상 임의의 문자열 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `MAIL_USERNAME` | Gmail 주소 |
| `MAIL_PASSWORD` | Gmail 앱 비밀번호 16자리 |
| `KAKAO_CLIENT_ID` | 카카오 REST API 키 |
| `KAKAO_CLIENT_SECRET` | 카카오 Client Secret |

**Step 5.** `OK` → `Apply` → `OK`

---

### 방법 2 — .env 파일 사용

**Step 1.** 프로젝트 루트에 `.env` 파일 생성

```
api/                   ← 프로젝트 루트
├── src/
├── build.gradle
├── .gitignore
└── .env               ← 여기에 만들어요
```

**Step 2.** `.env` 파일에 아래 내용 입력

```
JWT_SECRET=32자이상임의의문자열을입력해주세요abcdefghijklmn
DB_PASSWORD=MySQL_비밀번호
MAIL_USERNAME=본인Gmail주소@gmail.com
MAIL_PASSWORD=Gmail앱비밀번호16자리
KAKAO_CLIENT_ID=카카오REST_API키
KAKAO_CLIENT_SECRET=카카오Client_Secret
```

**Step 3.** IntelliJ 에서 .env 파일 읽기 설정

```
Run → Edit Configurations
→ ApiApplication
→ Environment variables 오른쪽 아이콘 클릭
→ "Load from file" 아이콘 클릭  (폴더 모양)
→ .env 파일 선택
→ OK
```

> ⚠️ `.env` 파일은 `.gitignore` 에 등록되어 있어요.
> 절대 GitHub 에 올리지 마세요.

---

## 🔑 환경변수 발급 방법

### JWT_SECRET

32자 이상의 임의 문자열이면 돼요.

```
예시: storyfund-jwt-secret-key-please-change-this-in-production
```

---

### DB_PASSWORD

MySQL 설치 시 설정한 root 비밀번호예요.

---

### MAIL_USERNAME / MAIL_PASSWORD (Gmail 앱 비밀번호)

```
1. https://myaccount.google.com 접속
2. 보안 → 2단계 인증 → 사용 설정 (필수)
3. 보안 → 2단계 인증 → 앱 비밀번호
4. 앱 이름: storyfund → 생성
5. 16자리 비밀번호 복사 (공백 제거하고 입력)
```

> ⚠️ 일반 Gmail 비밀번호가 아니에요. 앱 비밀번호를 따로 발급해야 해요.

---

### KAKAO_CLIENT_ID / KAKAO_CLIENT_SECRET

```
1. https://developers.kakao.com 접속
2. 내 애플리케이션 → 애플리케이션 추가
3. 앱 키 탭 → REST API 키 복사  (KAKAO_CLIENT_ID)
4. 카카오 로그인 → 활성화 ON
5. Redirect URI 등록: http://localhost:8080/api/auth/kakao
6. 보안 → Client Secret → 코드 생성 후 복사  (KAKAO_CLIENT_SECRET)
7. 동의항목 → 이메일 필수 동의 설정
```

---

## 📋 API 목록

### 인증

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| POST | `/api/auth/signup` | 회원가입 | 불필요 |
| POST | `/api/auth/login` | 로그인 | 불필요 |
| POST | `/api/auth/refresh` | 토큰 갱신 | 불필요 |
| POST | `/api/auth/logout` | 로그아웃 | 필요 |
| GET | `/api/auth/kakao` | 카카오 로그인 | 불필요 |
| POST | `/api/auth/emails/send` | 인증 코드 발송 | 불필요 |
| POST | `/api/auth/emails/verify` | 인증 코드 확인 | 불필요 |

### 게시판

| Method | URL | 설명 | 인증 |
|---|---|---|---|
| GET | `/api/boards` | 게시글 목록 | 불필요 |
| GET | `/api/boards/{id}` | 게시글 상세 | 불필요 |
| POST | `/api/boards` | 게시글 작성 | 필요 |
| PUT | `/api/boards/{id}` | 게시글 수정 | 필요 |
| DELETE | `/api/boards/{id}` | 게시글 삭제 | 필요 |

---

## 🚀 개발 시작 전 체크리스트

```
□ Ubuntu 터미널에서 Redis 시작
  sudo service redis-server start
  redis-cli ping → PONG 확인

□ IntelliJ 환경변수 설정 확인

□ Spring Boot 서버 실행
  Started ApiApplication in x.xxx seconds 확인
```

---

## 📁 프로젝트 구조

```
src/main/java/com/storyfund/api/
├── config/
│   ├── AppConfig.java          # BCrypt 설정
│   ├── RedisConfig.java        # Redis 직렬화 설정
│   ├── SecurityConfig.java     # Spring Security 설정
│   └── GlobalExceptionHandler.java
├── controller/
│   ├── UserController.java     # 인증 API
│   └── BoardController.java    # 게시판 API
├── service/
│   ├── UserService.java
│   ├── BoardService.java
│   ├── EmailService.java
│   └── KakaoService.java
├── repository/
│   ├── UserRepository.java
│   └── BoardRepository.java
├── entity/
│   ├── User.java
│   └── Board.java
├── dto/
│   └── ...
└── security/
    ├── JwtTokenProvider.java
    └── JwtAuthenticationFilter.java
```

---

## ⚠️ 주의사항

```
- application.yml 은 .gitignore 에 등록되어 있어요
- .env 파일은 절대 GitHub 에 올리지 마세요
- 키가 노출됐다면 즉시 재발급 해주세요
  - Gmail 앱 비밀번호 재발급
  - 카카오 Client Secret 재발급
```