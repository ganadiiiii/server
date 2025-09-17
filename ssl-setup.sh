#!/bin/bash

# SSL 인증서 설정 스크립트
set -e

echo "🔒 SSL 인증서 설정 시작..."

# 환경 변수 확인
if [ ! -f "env.production" ]; then
    echo "❌ env.production 파일이 없습니다."
    exit 1
fi

# 환경 변수 로드
source env.production

if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
    echo "❌ DOMAIN과 EMAIL 환경 변수를 설정해주세요."
    exit 1
fi

echo "📋 도메인: $DOMAIN"
echo "📧 이메일: $EMAIL"

# 1. HTTP로 먼저 서비스 시작
echo "🌐 HTTP 서비스 시작 중..."
docker-compose up -d nginx

# 2. SSL 인증서 발급
echo "🔐 SSL 인증서 발급 중..."
docker-compose run --rm certbot

# 3. Nginx 설정 업데이트 (도메인 반영)
echo "⚙️ Nginx 설정 업데이트 중..."
sed -i "s/your-domain.com/$DOMAIN/g" nginx.conf

# 4. 서비스 재시작
echo "🔄 서비스 재시작 중..."
docker-compose restart nginx

echo "✅ SSL 설정 완료!"
echo "🌐 HTTPS 접속: https://$DOMAIN"
