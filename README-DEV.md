# 🌸 Ganadi - 개발 서버 가이드

## 🚀 빠른 시작

### 1. 데이터베이스 설정
```bash
# PostgreSQL 설치 (macOS)
brew install postgresql
brew services start postgresql

# 데이터베이스 생성
createdb ganadi
```

### 2. 환경 변수 설정
```bash
# env.example을 참고하여 환경 변수 설정
cp env.example .env
# .env 파일을 편집하여 실제 값으로 변경
```

### 3. 서버 시작
```bash
# 개발 서버 시작 (권장)
./start-dev.sh

# 또는 직접 실행
./gradlew bootRun
```

## 🌐 접속 정보

- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API 문서**: http://localhost:8080/v3/api-docs

## 🔧 개발 도구

### API 테스트
```bash
# Swagger UI 사용 (권장)
# 브라우저에서 http://localhost:8080/swagger-ui/index.html 접속

# 또는 curl로 테스트
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123","firstName":"홍","lastName":"길동"}'
```

### 데이터베이스 확인
```bash
# PostgreSQL 접속
psql -d ganadi

# 테이블 목록 확인
\dt

# 사용자 데이터 확인
SELECT * FROM users;
```

## 🛠️ 문제 해결

### 포트 충돌
```bash
# 8080 포트 사용 중인 프로세스 확인
lsof -i :8080

# 프로세스 종료
kill -9 <PID>
```

### 데이터베이스 연결 오류
```bash
# PostgreSQL 상태 확인
brew services list | grep postgresql

# PostgreSQL 재시작
brew services restart postgresql
```

### CORS 오류
- `application.yml`의 `cors.allowed-origins`에 프론트엔드 URL 추가
- 브라우저 개발자 도구에서 네트워크 탭 확인

## 📱 프론트엔드 연동

### CORS 설정
현재 다음 오리진들이 허용되어 있습니다:
- `http://localhost:3000` (React 기본)
- `http://localhost:3001` (대체 포트)
- `http://127.0.0.1:3000` (로컬호스트)

### JWT 토큰 사용
```javascript
// 로그인 후 토큰 저장
const response = await fetch('/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ email, password })
});
const { token } = await response.json();

// API 요청 시 토큰 포함
fetch('/api/bouquets', {
  headers: { 'Authorization': `Bearer ${token}` }
});
```

## 🔄 자동 재시작

개발 중 코드 변경 시 자동으로 재시작됩니다:
- Java 파일 변경 시 자동 재컴파일
- `application.yml` 변경 시 자동 재시작
- 정적 리소스 변경 시 자동 반영

## 📊 로그 확인

개발 모드에서는 상세한 로그가 출력됩니다:
- SQL 쿼리 로그
- Spring Security 디버그 로그
- 애플리케이션 디버그 로그

로그 레벨 조정은 `application.yml`의 `logging.level` 섹션에서 가능합니다.
