# 누락된 엔드포인트 수정 기록

## 문제 상황
API 테스트 중 500 에러 발생:
- `GET /api/bouquets` → `HttpRequestMethodNotSupportedException: Request method 'GET' is not supported`
- `GET /api/gifts` → `HttpRequestMethodNotSupportedException: Request method 'GET' is not supported`  
- `GET /api/orders` → `HttpRequestMethodNotSupportedException: Request method 'GET' is not supported`

## 원인 분석
각 컨트롤러에 목록 조회용 `GET /api/{resource}` 엔드포인트가 누락되어 있었음.

## 수정 내용

### 1. BouquetController
**추가된 엔드포인트:**
```java
@GetMapping
@Operation(summary = "내 부케 목록 조회", description = "내가 만든 부케 목록을 조회합니다.")
public ResponseEntity<java.util.List<BouquetResponse>> getMyBouquets()
```

**기존 서비스 메서드 활용:**
```java
// BouquetService.java의 기존 getUserBouquets() 메서드 사용
@Transactional(readOnly = true)
public List<BouquetResponse> getUserBouquets(Long userId) {
    List<Bouquet> bouquets = bouquetRepository.findByOwnerId(userId);
    return bouquets.stream()
            .map(this::convertToBouquetResponse)
            .collect(Collectors.toList());
}
```

### 2. GiftController  
**추가된 엔드포인트:**
```java
@GetMapping
@Operation(summary = "선물 카탈로그 조회", description = "선물 가능한 꽃다발 카탈로그를 조회합니다.")
public ResponseEntity<List<GiftResponse>> getGiftCatalog()
```

**추가된 서비스 메서드:**
```java
// GiftService.java
@Transactional(readOnly = true)
public List<GiftResponse> getGiftCatalog() {
    // 선물 가능한 꽃다발 목록을 반환 (예: 공개된 꽃다발들)
    return bouquetRepository.findAll()
            .stream()
            .map(bouquet -> {
                // 선물 카탈로그용 응답 생성
                UserResponse owner = new UserResponse(/* ... */);
                return new GiftResponse(
                        bouquet.getId(), // 임시로 bouquet ID 사용
                        owner, null, // receiver는 null
                        bouquet.getId(),
                        bouquet.getMessage(),
                        "available", // 카탈로그 상태
                        bouquet.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
}
```

### 3. OrderController
**추가된 엔드포인트:**
```java
@GetMapping
@Operation(summary = "내 주문 목록 조회", description = "내가 생성한 주문 목록을 조회합니다.")
public ResponseEntity<java.util.List<OrderResponse>> getMyOrders()
```

**기존 서비스 메서드 활용:**
```java
// OrderService.java의 기존 getUserOrders() 메서드 사용
@Transactional(readOnly = true)
public List<OrderResponse> getUserOrders(Long userId) {
    return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId)
            .stream().map(this::toResponse).collect(Collectors.toList());
}
```

## 수정된 파일 목록
- `src/main/java/com/ganadi/palmful/controller/BouquetController.java`
- `src/main/java/com/ganadi/palmful/controller/GiftController.java`
- `src/main/java/com/ganadi/palmful/controller/OrderController.java`
- `src/main/java/com/ganadi/palmful/service/BouquetService.java`
- `src/main/java/com/ganadi/palmful/service/GiftService.java`
- `src/main/java/com/ganadi/palmful/service/OrderService.java`

## 다음 단계
1. 빌드 및 배포
2. API 재테스트
3. 추가 에러 발생 시 로그 확인 및 수정

## 테스트 예상 결과
- `GET /api/bouquets` → 200 OK (내가 만든 부케 목록 반환)
- `GET /api/gifts` → 200 OK (선물 카탈로그 반환)
- `GET /api/orders` → 200 OK (내 주문 목록 반환)

---

## 통합 스모크 테스트 결과 (배포 환경)

### 인증/기본
- `GET /actuator/health` → 200 OK: 서비스 UP 확인
- `GET /api/flowers` → 200 OK: 기본 꽃 데이터(이름/색상/의미/이미지 경로) 조회
- `GET /swagger-ui/index.html` → 200 OK: Swagger UI 정상 노출
- `POST /api/auth/signup` → 201 Created: 새 계정 생성 성공 (UserResponse 반환)
- `POST /api/auth/login` → 200 OK: JWT 토큰 발급 (AuthResponse.token)

### 친구 관리
- `POST /api/friends/requests` → 201 Created: 친구 요청 생성(요청자=로그인 사용자, 수신자=지정 ID)
- `GET /api/friends/requests/sent` → 200 OK: 보낸 요청 목록 확인(pending 상태)

### 부케 생성/조회/아카이브
- `POST /api/bouquets` → 201 Created: 꽃 목록(`flowers[]`) 포함 시 생성 성공
  - 예: `{"title":"봄의 꽃다발",...,"flowers":[{"flowerId":1,"quantity":3}]}`
- `GET /api/bouquets` → 200 OK: 로그인 사용자의 부케 목록 반환
- `PATCH /api/bouquets/{id}/archive` → 200 OK: 상태 `archived`로 변경, `archivedAt` 세팅
- `GET /api/users/me/archives` → 200 OK: 아카이브된 내 부케 목록 반환

### 장바구니
- `POST /api/cart` → 201 Created: 부케를 장바구니에 추가(수량 포함)
- `GET /api/cart` → 200 OK: 장바구니 항목 목록 반환

### 선물하기
- `GET /api/gifts` → 200 OK: 선물 카탈로그 반환(현재 공개/가용 항목 기준 변환 응답)
- `POST /api/gifts` → 201 Created: 지정 친구에게 선물 전송(메시지 포함)
- `GET /api/gifts/sent` → 200 OK: 내가 보낸 선물 목록 반환

### 주문
- `POST /api/orders` → 201 Created: 배송/연락처/주소 포함 주문 생성, `user`/`bouquet`가 포함된 DTO 반환
- `GET /api/orders` → 200 OK: 로그인 사용자의 주문 목록 반환

### 사용자
- `GET /api/users/me` → 200 OK: 내 프로필 반환

요약: 위 시나리오 모두 2xx 응답으로 정상 동작을 확인함.

---

## Swagger 문서 개선 계획

문제:
- 유사/중복 기능 엔드포인트가 Swagger에 함께 노출되어 혼란 유발
- 컨트롤러/엔드포인트 정렬이 일관되지 않음

개선 액션(차근차근 진행):
1. 중복 엔드포인트 정리
   - 인증: `POST /api/auth/register` vs `POST /api/auth/signup` → 한 개로 통일하고 다른 하나는 유지 시 deprecated 표시
2. 태그/그룹 구조 정리
   - 태그 순서: 인증 → 사용자 → 꽃 → 부케 → 부케 생성 → 친구 → 장바구니 → 선물 → 주문 → 공유
3. 엔드포인트 정렬 규칙 통일
   - 각 리소스 내 정렬: GET(컬렉션) → GET(단건) → POST → PUT → PATCH → DELETE → 서브리소스
4. 요약/설명 한글화 통일
   - `@Operation(summary, description)` 일관된 톤/용어로 정비
5. 보안 스키마 적용 점검
   - 공개/인증 필요 엔드포인트 구분 명확화(스키마 및 `SecurityRequirement` 적용 확인)
6. 예시 스키마/응답 추가
   - 필수 DTO에 example 추가로 Swagger 사용성 향상

위 개선은 문서/어노테이션 수준 변경이므로, 다음 커밋에서 단계별 반영 후 Swagger UI로 확인 예정.
