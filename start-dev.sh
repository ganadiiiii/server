#!/bin/bash

# 개발 서버 시작 스크립트
echo "🚀 Ganadi 개발 서버를 시작합니다..."

# 환경 변수 설정 (필요시 수정)
export DATABASE_URL="jdbc:postgresql://localhost:5432/ganadi"
export DATABASE_USERNAME="ganadi"
export DATABASE_PASSWORD="ganadi123"
export JWT_SECRET="dev_jwt_secret_key_2024"
export CORS_ORIGINS="http://localhost:3000,http://localhost:3001,http://127.0.0.1:3000"

# PostgreSQL 데이터베이스 확인
echo "📊 데이터베이스 연결 확인 중..."
if ! pg_isready -h localhost -p 5432 -U ganadi > /dev/null 2>&1; then
    echo "❌ PostgreSQL이 실행되지 않았습니다. 먼저 PostgreSQL을 시작해주세요."
    echo "   macOS: brew services start postgresql"
    echo "   Ubuntu: sudo systemctl start postgresql"
    exit 1
fi

echo "✅ 데이터베이스 연결 확인 완료"

# Gradle 빌드 및 실행
echo "🔨 애플리케이션 빌드 중..."
./gradlew clean build -x test

if [ $? -eq 0 ]; then
    echo "✅ 빌드 완료"
    echo "🌐 서버 시작 중... (http://localhost:8080)"
    echo "📚 Swagger UI: http://localhost:8080/swagger-ui/index.html"
    echo "🛑 서버 중지: Ctrl+C"
    echo ""
    
    # 개발 모드로 실행 (자동 재시작)
    ./gradlew bootRun --args='--spring.profiles.active=dev'
else
    echo "❌ 빌드 실패"
    exit 1
fi
