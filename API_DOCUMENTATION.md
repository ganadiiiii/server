# Ganadi API 문서

## 📋 개요

현재 Ganadi 서버에 구현된 모든 API 엔드포인트의 상세 명세서입니다. 모든 API는 JWT 기반 인증을 사용하며, Swagger UI에서 확인 가능합니다.

**Base URL**: `http://localhost:8080/api`  
**Swagger UI**: `http://localhost:8080/swagger-ui.html`  
**API Docs**: `http://localhost:8080/v3/api-docs`

---

## 🔐 1. 인증 (Authentication)

### 1.1 회원가입
```http
POST /api/auth/signup
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "홍",
  "lastName": "길동"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "홍",
  "lastName": "길동",
  "provider": "local",
  "createdAt": "2024-01-01T00:00:00"
}
```

### 1.2 로그인
```http
POST /api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "홍",
    "lastName": "길동",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

### 1.3 인증 확인 (테스트용)
```http
GET /api/auth
```

**Response (200 OK):**
```
Hello Auth
```

---

## 👤 2. 사용자 관리 (User Management)

### 2.1 내 정보 조회
```http
GET /api/users/me
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "홍",
  "lastName": "길동",
  "provider": "local",
  "createdAt": "2024-01-01T00:00:00"
}
```

### 2.2 프로필 수정
```http
PATCH /api/users/me
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "김",
  "lastName": "철수",
  "email": "newemail@example.com"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "email": "newemail@example.com",
  "firstName": "김",
  "lastName": "철수",
  "provider": "local",
  "createdAt": "2024-01-01T00:00:00"
}
```

### 2.3 내 부케 목록
```http
GET /api/users/me/bouquets
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "사랑의 꽃다발",
    "mood": "romantic",
    "occasion": "anniversary",
    "size": "medium",
    "message": "사랑해요",
    "status": "active",
    "previewUrl": "https://example.com/bouquet1.jpg",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "archivedAt": null
  }
]
```

### 2.4 내 아카이브
```http
GET /api/users/me/archives
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "title": "아카이브된 꽃다발",
    "mood": "romantic",
    "occasion": "anniversary",
    "size": "medium",
    "message": "사랑해요",
    "status": "archived",
    "previewUrl": "https://example.com/bouquet1.jpg",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "archivedAt": "2024-01-02T00:00:00"
  }
]
```

### 2.5 내 주문 목록 (모의)
```http
GET /api/users/me/orders
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "content": [],
  "pageable": {
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 0,
  "totalPages": 0,
  "last": true,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": true,
    "sorted": false,
    "unsorted": true
  },
  "numberOfElements": 0,
  "first": true,
  "empty": true
}
```

---

## 🌸 3. 꽃 관리 (Flower Management)

### 3.1 꽃 목록 조회
```http
GET /api/flowers
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "개나리",
    "colorHex": "#FFD700",
    "meaning": "희망과 기쁨",
    "assetUrl": "/images/flowers/forsythia.png"
  },
  {
    "id": 2,
    "name": "거베라",
    "colorHex": "#FF4500",
    "meaning": "순수한 사랑",
    "assetUrl": "/images/flowers/gerbera.png"
  },
  {
    "id": 3,
    "name": "은방울꽃",
    "colorHex": "#F0FFF0",
    "meaning": "순결과 행복",
    "assetUrl": "/images/flowers/lilyvalley.png"
  },
  {
    "id": 4,
    "name": "튤립",
    "colorHex": "#FF6347",
    "meaning": "완벽한 사랑",
    "assetUrl": "/images/flowers/tulip.png"
  },
  {
    "id": 5,
    "name": "물망초",
    "colorHex": "#ADD8E6",
    "meaning": "나를 잊지 말아요",
    "assetUrl": "/images/flowers/forgetmenot.png"
  },
  {
    "id": 6,
    "name": "장미",
    "colorHex": "#FF69B4",
    "meaning": "사랑과 열정",
    "assetUrl": "/images/flowers/rose.png"
  },
  {
    "id": 7,
    "name": "백합",
    "colorHex": "#FFFFFF",
    "meaning": "순수와 깨끗함",
    "assetUrl": "/images/flowers/lily.png"
  },
  {
    "id": 8,
    "name": "카네이션",
    "colorHex": "#FF1493",
    "meaning": "어머니의 사랑",
    "assetUrl": "/images/flowers/carnation.png"
  },
  {
    "id": 9,
    "name": "해바라기",
    "colorHex": "#FFD700",
    "meaning": "행복과 긍정",
    "assetUrl": "/images/flowers/sunflower.png"
  }
]
```

### 3.2 꽃 상세 조회
```http
GET /api/flowers/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "개나리",
  "colorHex": "#FFD700",
  "meaning": "희망과 기쁨",
  "assetUrl": "/images/flowers/forsythia.png"
}
```

---

## 💐 4. 꽃다발 관리 (Bouquet Management)

### 4.1 꽃다발 생성
```http
POST /api/bouquets
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요",
  "status": "draft",
  "previewUrl": null,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "archivedAt": null
}
```

### 4.2 꽃다발 조회
```http
GET /api/bouquets/{id}
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요",
  "status": "active",
  "previewUrl": "https://example.com/bouquet1.jpg",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "archivedAt": null
}
```

### 4.3 꽃다발 수정
```http
PUT /api/bouquets/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "수정된 제목",
  "mood": "happy",
  "occasion": "birthday",
  "size": "large",
  "message": "수정된 메시지"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "수정된 제목",
  "mood": "happy",
  "occasion": "birthday",
  "size": "large",
  "message": "수정된 메시지",
  "status": "active",
  "previewUrl": "https://example.com/bouquet1.jpg",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T12:00:00",
  "archivedAt": null
}
```

### 4.4 꽃다발 삭제
```http
DELETE /api/bouquets/{id}
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

### 4.5 꽃다발 아카이브
```http
POST /api/bouquets/{id}/archive
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요",
  "status": "archived",
  "previewUrl": "https://example.com/bouquet1.jpg",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T12:00:00",
  "archivedAt": "2024-01-01T12:00:00"
}
```

---

## 🤖 5. AI 꽃다발 생성 (Bouquet Generation)

### 5.1 꽃다발 생성
```http
POST /api/bouquets/{id}/generate
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "prompt": "사랑스러운 분위기의 꽃다발",
  "seed": 12345,
  "paramsJson": "{\"steps\": 20, \"guidance\": 7.5}"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "version": 1,
  "model": "stable-diffusion",
  "prompt": "사랑스러운 분위기의 꽃다발",
  "seed": 12345,
  "previewUrl": "https://cdn.mock/1/1.png",
  "status": "generated",
  "createdAt": "2024-01-01T00:00:00"
}
```

### 5.2 생성 이력 조회
```http
GET /api/bouquets/{id}/generations
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "version": 1,
    "model": "stable-diffusion",
    "prompt": "사랑스러운 분위기의 꽃다발",
    "seed": 12345,
    "previewUrl": "https://cdn.mock/1/1.png",
    "status": "generated",
    "createdAt": "2024-01-01T00:00:00"
  },
  {
    "id": 2,
    "version": 2,
    "model": "stable-diffusion",
    "prompt": "더 화려한 꽃다발",
    "seed": 67890,
    "previewUrl": "https://cdn.mock/1/2.png",
    "status": "generated",
    "createdAt": "2024-01-01T01:00:00"
  }
]
```

### 5.3 꽃다발 발행
```http
POST /api/bouquets/{id}/publish?generationId={generationId}
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요",
  "status": "active",
  "previewUrl": "https://cdn.mock/1/1.png",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T02:00:00",
  "archivedAt": null
}
```

---

## 👥 6. 친구 관리 (Friend Management)

### 6.1 친구 목록 조회
```http
GET /api/friends
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "email": "friend@example.com",
    "firstName": "김",
    "lastName": "친구",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 6.2 친구 요청 보내기
```http
POST /api/friends/requests
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "addresseeId": 2,
  "message": "친구가 되고 싶어요!"
}
```

**Response (201 Created):**
```
(응답 본문 없음)
```

### 6.3 친구 요청 수락
```http
POST /api/friends/requests/{requestId}/accept
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

### 6.4 친구 요청 거절
```http
POST /api/friends/requests/{requestId}/deny
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

### 6.5 친구 삭제
```http
DELETE /api/friends/{friendUserId}
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

### 6.6 친구 검색
```http
GET /api/friends/search?q=검색어
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "email": "friend@example.com",
    "firstName": "김",
    "lastName": "친구",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 6.7 받은 친구 요청 목록
```http
GET /api/friends/requests/received
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "requesterId": 3,
    "addresseeId": 1,
    "message": "친구가 되고 싶어요!",
    "status": "pending",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 6.8 보낸 친구 요청 목록
```http
GET /api/friends/requests/sent
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "requesterId": 1,
    "addresseeId": 2,
    "message": "친구가 되고 싶어요!",
    "status": "pending",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

---

## 🎁 7. 선물하기 (Gift Management)

### 7.1 선물 보내기
```http
POST /api/gifts
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "bouquetId": 1,
  "receiverId": 2,
  "message": "생일 축하해요!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "sender": {
    "id": 1,
    "email": "sender@example.com",
    "firstName": "홍",
    "lastName": "길동",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  },
  "receiver": {
    "id": 2,
    "email": "receiver@example.com",
    "firstName": "김",
    "lastName": "친구",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  },
  "bouquetId": 1,
  "message": "생일 축하해요!",
  "status": "sent",
  "sentAt": "2024-01-01T00:00:00"
}
```

### 7.2 받은 선물 조회
```http
GET /api/gifts/received
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "sender": {
      "id": 2,
      "email": "sender@example.com",
      "firstName": "김",
      "lastName": "친구",
      "provider": "local",
      "createdAt": "2024-01-01T00:00:00"
    },
    "receiver": {
      "id": 1,
      "email": "receiver@example.com",
      "firstName": "홍",
      "lastName": "길동",
      "provider": "local",
      "createdAt": "2024-01-01T00:00:00"
    },
    "bouquetId": 1,
    "message": "생일 축하해요!",
    "status": "sent",
    "sentAt": "2024-01-01T00:00:00"
  }
]
```

### 7.3 보낸 선물 조회
```http
GET /api/gifts/sent
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "sender": {
      "id": 1,
      "email": "sender@example.com",
      "firstName": "홍",
      "lastName": "길동",
      "provider": "local",
      "createdAt": "2024-01-01T00:00:00"
    },
    "receiver": {
      "id": 2,
      "email": "receiver@example.com",
      "firstName": "김",
      "lastName": "친구",
      "provider": "local",
      "createdAt": "2024-01-01T00:00:00"
    },
    "bouquetId": 1,
    "message": "생일 축하해요!",
    "status": "sent",
    "sentAt": "2024-01-01T00:00:00"
  }
]
```

### 7.4 선물 읽음 처리
```http
PATCH /api/gifts/{id}/read
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

---

## 🔗 8. 공유 링크 (Share Link)

### 8.1 공유 링크 생성
```http
POST /api/bouquets/{id}/share?channel=instagram
Authorization: Bearer <token>
```

**Response (201 Created):**
```json
"c3c9b9d7-0078-4c91-91ec-31ba010208de"
```

### 8.2 공유 콘텐츠 조회
```http
GET /api/share/{token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "title": "사랑의 꽃다발",
  "mood": "romantic",
  "occasion": "anniversary",
  "size": "medium",
  "message": "사랑해요",
  "status": "active",
  "previewUrl": "https://example.com/bouquet1.jpg",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "archivedAt": null
}
```

---

## 🛒 9. 장바구니 (Cart)

### 9.1 장바구니 조회
```http
GET /api/cart
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "bouquetId": 1,
    "quantity": 2,
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 9.2 장바구니 담기
```http
POST /api/cart
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "bouquetId": 1,
  "quantity": 2
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "bouquetId": 1,
  "quantity": 2,
  "createdAt": "2024-01-01T00:00:00"
}
```

### 9.3 장바구니 수정
```http
PUT /api/cart/{itemId}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "quantity": 3
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "bouquetId": 1,
  "quantity": 3,
  "createdAt": "2024-01-01T00:00:00"
}
```

### 9.4 장바구니 삭제
```http
DELETE /api/cart/{itemId}
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

---

## 📦 10. 주문 (Order)

### 10.1 주문 생성
```http
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "bouquetId": 1,
  "recipientName": "김철수",
  "phone": "010-1234-5678",
  "shippingAddr": "서울시 강남구 테헤란로 123",
  "totalPrice": 50000
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "status": "pending",
  "totalPrice": 50000,
  "recipientName": "김철수",
  "phone": "010-1234-5678",
  "shippingAddr": "서울시 강남구 테헤란로 123",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "홍",
    "lastName": "길동",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  },
  "bouquet": {
    "id": 1,
    "title": "사랑의 꽃다발",
    "mood": "romantic",
    "occasion": "anniversary",
    "size": "medium",
    "message": "사랑해요",
    "status": "active",
    "previewUrl": "https://example.com/bouquet1.jpg",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "archivedAt": null
  }
}
```

### 10.2 주문 조회
```http
GET /api/orders/{id}
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "status": "pending",
  "totalPrice": 50000,
  "recipientName": "김철수",
  "phone": "010-1234-5678",
  "shippingAddr": "서울시 강남구 테헤란로 123",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "홍",
    "lastName": "길동",
    "provider": "local",
    "createdAt": "2024-01-01T00:00:00"
  },
  "bouquet": {
    "id": 1,
    "title": "사랑의 꽃다발",
    "mood": "romantic",
    "occasion": "anniversary",
    "size": "medium",
    "message": "사랑해요",
    "status": "active",
    "previewUrl": "https://example.com/bouquet1.jpg",
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00",
    "archivedAt": null
  }
}
```

---

## 🔔 11. 알림 (Notification)

### 11.1 알림 목록 조회
```http
GET /api/notifications
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "type": "FRIEND_REQUEST",
    "title": "새로운 친구 요청",
    "message": "김친구님이 친구 요청을 보냈습니다.",
    "isRead": false,
    "relatedId": 1,
    "relatedType": "FRIEND_REQUEST",
    "createdAt": "2024-01-01T00:00:00"
  }
]
```

### 11.2 읽지 않은 알림 수
```http
GET /api/notifications/unread-count
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
3
```

### 11.3 알림 읽음 처리
```http
PATCH /api/notifications/{id}/read
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

### 11.4 모든 알림 읽음 처리
```http
PATCH /api/notifications/read-all
Authorization: Bearer <token>
```

**Response (204 No Content):**
```
(응답 본문 없음)
```

---

## 📊 12. 에러 응답

모든 API는 일관된 에러 응답 형식을 사용합니다:

```json
{
  "error": "ERROR_CODE",
  "message": "에러 메시지"
}
```

### 주요 HTTP 상태 코드

- `200 OK` - 성공
- `201 Created` - 생성 성공
- `204 No Content` - 성공 (응답 본문 없음)
- `400 Bad Request` - 잘못된 요청
- `401 Unauthorized` - 인증 실패
- `403 Forbidden` - 권한 없음
- `404 Not Found` - 리소스 없음
- `409 Conflict` - 충돌 (예: 중복 이메일)
- `500 Internal Server Error` - 서버 오류

---

## 🔧 13. 인증 헤더

모든 보호된 API는 다음 헤더가 필요합니다:

```
Authorization: Bearer <your-jwt-token>
```

JWT 토큰은 로그인 API를 통해 획득할 수 있습니다.

---

## 📝 14. 사용 예제

### 14.1 전체 플로우 예제

```bash
# 1. 회원가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "firstName": "홍",
    "lastName": "길동"
  }'

# 2. 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'

# 3. 꽃다발 생성
curl -X POST http://localhost:8080/api/bouquets \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "사랑의 꽃다발",
    "mood": "romantic",
    "occasion": "anniversary",
    "size": "medium",
    "message": "사랑해요"
  }'

# 4. AI 꽃다발 생성
curl -X POST http://localhost:8080/api/bouquets/1/generate \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "prompt": "사랑스러운 분위기의 꽃다발",
    "seed": 12345
  }'

# 5. 꽃다발 발행
curl -X POST http://localhost:8080/api/bouquets/1/publish?generationId=1 \
  -H "Authorization: Bearer <token>"

# 6. 공유 링크 생성
curl -X POST http://localhost:8080/api/bouquets/1/share \
  -H "Authorization: Bearer <token>"

# 7. 선물 보내기
curl -X POST http://localhost:8080/api/gifts \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "bouquetId": 1,
    "receiverId": 2,
    "message": "생일 축하해요!"
  }'
```

---

## 🎯 15. 구현 상태 요약

### ✅ 완전 구현된 기능
- 인증 (회원가입, 로그인, JWT)
- 사용자 관리 (프로필 조회/수정, 부케 목록, 아카이브)
- 꽃 관리 (9종 꽃 데이터 + 이미지)
- 꽃다발 관리 (CRUD, 아카이브)
- AI 꽃다발 생성 (생성, 이력 조회, 발행)
- 친구 관리 (요청, 수락/거절, 검색, 목록)
- 선물하기 (보내기, 받은/보낸 목록, 읽음 처리)
- 공유 링크 (생성, 조회)
- 장바구니 (CRUD, 수정)
- 주문 (생성, 조회)
- 알림 (목록, 읽음 처리, 개수 조회)

### 🔄 부분 구현된 기능
- 주문 내역 조회 (페이징 준비됨, 실제 데이터 없음)
- 결제 연동 (모의 주문만 가능)

### 📋 구현 예정 기능
- 실제 결제 API 연동
- 배송 추적
- 고급 검색 및 필터링
- 실시간 알림 (WebSocket)
- 이미지 업로드 및 관리

---

이 API 명세서는 현재 구현된 모든 기능을 포함하고 있으며, Swagger UI에서도 동일한 정보를 확인할 수 있습니다.
