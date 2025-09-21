# Ganadi - 꽃다발 생성 및 공유 플랫폼

### API : http://34.64.68.85:8080/swagger-ui/index.html#/

AI를 활용한 개인화된 꽃다발 생성, 친구와의 선물 공유, 주문 및 배송을 지원하는 웹 애플리케이션입니다.

## 🚀 주요 기능

### 1. 회원 관리
- **회원가입/로그인**: JWT 기반 인증
- **프로필 관리**: 개인정보 수정
- **마이페이지**: 내 정보 및 리소스 조회

### 2. 친구 관리
- **친구 요청**: 친구 요청 보내기/받기
- **친구 목록**: 친구 목록 조회 및 검색
- **친구 관리**: 친구 요청 수락/거절, 친구 삭제

### 3. 꽃다발 생성
- **AI 생성**: 나노바나나 모델을 활용한 꽃다발 생성
- **의미 기반**: 감정/상황에 맞는 꽃다발 생성
- **버전 관리**: 여러 버전 생성 및 관리
- **발행**: 생성된 꽃다발 발행

### 4. 선물하기
- **선물 전송**: 친구에게 꽃다발 선물
- **선물 내역**: 보낸/받은 선물 조회
- **읽음 처리**: 선물 읽음 상태 관리

### 5. 공유 링크
- **링크 생성**: 꽃다발 공유 링크 생성
- **공유 보기**: 토큰 기반 공유 콘텐츠 조회

### 6. 아카이빙
- **아카이브 조회**: 내가 만든 꽃다발 + 받은 꽃다발
- **상세 보기**: 이미지, 꽃말, 생성날짜 등 상세 정보

### 7. 주문/결제
- **주문 생성**: 꽃다발 주문 (모의)
- **주문 조회**: 주문 내역 조회
- **배송 정보**: 받는 사람 정보, 배송지, 배송일시

### 8. 알림 시스템
- **알림 조회**: 알림 목록 조회
- **읽음 처리**: 개별/전체 읽음 처리
- **실시간 알림**: 친구 요청, 선물 수신 등

## 🛠 기술 스택

- **Backend**: Spring Boot 3.5.5, Java 21
- **Database**: PostgreSQL 15
- **Cache**: Redis 7
- **Authentication**: JWT
- **API Documentation**: Springdoc OpenAPI 3 (Swagger)
- **Build Tool**: Gradle
- **Containerization**: Docker, Docker Compose

## 📁 프로젝트 구조

```
src/main/java/com/ganadi/palmful/
├── config/                 # 설정 클래스
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   └── FlowerDataLoader.java
├── controller/             # REST API 컨트롤러
│   ├── AuthController.java
│   ├── UserController.java
│   ├── BouquetController.java
│   ├── BouquetGenerationController.java
│   ├── FlowerController.java
│   ├── FriendController.java
│   ├── GiftController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── ShareLinkController.java
│   └── NotificationController.java
├── service/                # 비즈니스 로직
│   ├── UserService.java
│   ├── BouquetService.java
│   ├── BouquetGenerationService.java
│   ├── FlowerService.java
│   ├── FriendService.java
│   ├── GiftService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── ShareLinkService.java
│   └── NotificationService.java
├── repository/             # 데이터 접근 계층
│   ├── UserRepository.java
│   ├── BouquetRepository.java
│   ├── BouquetGenerationRepository.java
│   ├── FlowerRepository.java
│   ├── FriendRequestRepository.java
│   ├── FriendshipRepository.java
│   ├── GiftRepository.java
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   ├── ShareLinkRepository.java
│   └── NotificationRepository.java
├── entity/                 # JPA 엔티티
│   ├── User.java
│   ├── Bouquet.java
│   ├── BouquetGeneration.java
│   ├── BouquetFlower.java
│   ├── Flower.java
│   ├── FriendRequest.java
│   ├── Friendship.java
│   ├── Gift.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── ShareLink.java
│   └── Notification.java
├── dto/                    # 데이터 전송 객체
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── UserResponse.java
│   ├── UserUpdateRequest.java
│   ├── BouquetRequest.java
│   ├── BouquetResponse.java
│   ├── GenerationRequest.java
│   ├── GenerationResponse.java
│   ├── FlowerResponse.java
│   ├── FriendRequestDto.java
│   ├── FriendResponse.java
│   ├── GiftRequest.java
│   ├── GiftResponse.java
│   ├── CartItemRequest.java
│   ├── CartItemUpdateRequest.java
│   ├── CartItemResponse.java
│   ├── OrderRequest.java
│   ├── OrderResponse.java
│   ├── ShareRequest.java
│   ├── ShareResponse.java
│   └── NotificationResponse.java
└── common/                 # 공통 클래스
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
```

## 🚀 실행 방법

### 1. Docker Compose로 실행 (권장)

```bash
# 1. 프로젝트 클론
git clone <repository-url>
cd Ganadi

# 2. 꽃 이미지 파일 준비
# src/main/resources/static/images/flowers/ 디렉토리에 다음 PNG 파일들을 배치:
# - forsythia.png (개나리)
# - gerbera.png (거베라)
# - lilyvalley.png (은방울꽃)
# - tulip.png (튤립)
# - forgetmenot.png (물망초)
# - rose.png (장미)
# - lily.png (백합)
# - carnation.png (카네이션)
# - sunflower.png (해바라기)

# 3. Docker Compose로 실행
docker-compose up -d

# 4. 애플리케이션 확인
# - API 서버: http://localhost:8080
# - Swagger UI: http://localhost:8080/swagger-ui/index.html
# - Health Check: http://localhost:8080/actuator/health
```

### 2. 로컬 개발 환경

#### 사전 요구사항
- Java 21
- PostgreSQL 15
- Redis 7 (선택사항)

#### 설정

```bash
# 1. PostgreSQL 데이터베이스 생성
createdb palmful

# 2. 환경 변수 설정
export SPRING_PROFILES_ACTIVE=dev
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/palmful
export SPRING_DATASOURCE_USERNAME=bloom_user
export SPRING_DATASOURCE_PASSWORD=secret

# 3. 애플리케이션 실행
./gradlew bootRun
```

## 📚 API 문서

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `GET /api/users/me` - 내 정보 조회
- `PATCH /api/users/me` - 프로필 수정

### 친구 관리
- `GET /api/friends` - 친구 목록 조회
- `POST /api/friends/requests` - 친구 요청 보내기
- `POST /api/friends/requests/{id}/accept` - 친구 요청 수락
- `POST /api/friends/requests/{id}/deny` - 친구 요청 거절
- `DELETE /api/friends/{id}` - 친구 삭제
- `GET /api/friends/search?q={query}` - 친구 검색

### 꽃다발 관리
- `GET /api/flowers` - 꽃 목록 조회
- `POST /api/bouquets` - 꽃다발 생성
- `GET /api/bouquets/{id}` - 꽃다발 조회
- `PUT /api/bouquets/{id}` - 꽃다발 수정
- `DELETE /api/bouquets/{id}` - 꽃다발 삭제
- `PATCH /api/bouquets/{id}/archive` - 꽃다발 아카이브

### AI 꽃다발 생성
- `POST /api/bouquets/{id}/generate` - 꽃다발 생성
- `GET /api/bouquets/{id}/generations` - 생성 이력 조회
- `POST /api/bouquets/{id}/publish` - 꽃다발 발행

### 선물하기
- `GET /api/gifts` - 선물 카탈로그 조회
- `POST /api/gifts` - 선물 보내기
- `GET /api/gifts/received` - 받은 선물 조회
- `GET /api/gifts/sent` - 보낸 선물 조회
- `PATCH /api/gifts/{id}/read` - 선물 읽음 처리

### 공유 링크
- `POST /api/bouquets/{id}/share` - 공유 링크 생성
- `GET /api/share/{token}` - 공유 콘텐츠 조회

### 장바구니
- `GET /api/cart` - 장바구니 조회
- `POST /api/cart` - 장바구니 담기
- `PUT /api/cart/{id}` - 장바구니 수정
- `DELETE /api/cart/{id}` - 장바구니 삭제

### 주문
- `POST /api/orders` - 주문 생성
- `GET /api/orders/{id}` - 주문 조회
- `GET /api/orders` - 내 주문 목록

### 알림
- `GET /api/notifications` - 알림 목록 조회
- `GET /api/notifications/unread-count` - 읽지 않은 알림 수
- `PATCH /api/notifications/{id}/read` - 알림 읽음 처리
- `PATCH /api/notifications/read-all` - 모든 알림 읽음 처리

### 아카이브
- `GET /api/users/me/archives` - 내 아카이브 조회

## 🔧 개발 환경 설정

### 1. IDE 설정
- IntelliJ IDEA 또는 Eclipse
- Lombok 플러그인 설치
- Spring Boot 플러그인 설치

### 2. 데이터베이스 스키마
애플리케이션 시작 시 `ddl-auto: update` 설정으로 자동 생성됩니다.

주요 테이블:
- `users` - 사용자 정보
- `bouquets` - 꽃다발 정보
- `bouquet_generations` - AI 생성 이력
- `flowers` - 꽃 정보
- `friendships` - 친구 관계
- `friend_requests` - 친구 요청
- `gifts` - 선물 정보
- `cart_items` - 장바구니 항목
- `orders` - 주문 정보
- `share_links` - 공유 링크
- `notifications` - 알림

### 3. 환경별 설정
- `application.yml` - 기본 설정
- `application-dev.yml` - 개발 환경
- `application-docker.yml` - Docker 환경

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest

# 전체 테스트 실행
./gradlew check
```

## 🚀 개발 환경 설정

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

### 접속 정보
- **API 서버**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API 문서**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 📦 빌드 및 배포

```bash
# JAR 파일 빌드
./gradlew build

# Docker 이미지 빌드
docker build -t ganadi-app .

# Docker Compose로 배포
docker-compose up -d
```

## 🔍 모니터링

### Health Check
- `GET /actuator/health` - 애플리케이션 상태 확인

### 메트릭
- `GET /actuator/metrics` - 애플리케이션 메트릭

### 로그
- 애플리케이션 로그는 콘솔에 출력
- Docker 환경에서는 `docker-compose logs -f app`으로 확인

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 지원

문제가 발생하거나 질문이 있으시면 이슈를 생성해 주세요.

---

**Ganadi** - AI로 만드는 특별한 꽃다발 🌸
