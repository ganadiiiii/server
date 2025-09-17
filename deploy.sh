#!/bin/bash

# Ganadi GCE 배포 스크립트
set -e

echo "🚀 Ganadi GCE 배포 시작..."

# 환경 변수 확인
if [ ! -f "env.production" ]; then
    echo "❌ env.production 파일이 없습니다. 먼저 환경 변수를 설정해주세요."
    exit 1
fi

# Docker 이미지 빌드
echo "📦 Docker 이미지 빌드 중..."
docker build -t ganadi-app:latest .

# 기존 컨테이너 정리
echo "🧹 기존 컨테이너 정리 중..."
docker-compose down --remove-orphans || true

# 서비스 시작
echo "🔄 서비스 시작 중..."
docker-compose up -d

# 서비스 상태 확인
echo "⏳ 서비스 상태 확인 중..."
sleep 30

# Health check
echo "🏥 Health check 수행 중..."
if docker-compose ps | grep -q "Up (healthy)"; then
    echo "✅ 모든 서비스가 정상적으로 실행 중입니다!"
    
    # 서비스 상태 출력
    echo ""
    echo "📊 서비스 상태:"
    docker-compose ps
    
    echo ""
    echo "🌐 접속 정보:"
    echo "- API 서버: http://$(curl -s ifconfig.me):80"
    echo "- Swagger UI: http://$(curl -s ifconfig.me):80/swagger-ui/"
    echo "- Health Check: http://$(curl -s ifconfig.me):80/actuator/health"
    
else
    echo "❌ 일부 서비스가 정상적으로 시작되지 않았습니다."
    echo "📋 로그 확인:"
    docker-compose logs --tail=50
    exit 1
fi

echo "🎉 배포 완료!"
