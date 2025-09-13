#!/bin/bash

# GCE 인스턴스 시작 스크립트
set -e

# 로그 파일 설정
exec > >(tee -a /var/log/ganadi-startup.log)
exec 2>&1

echo "🚀 Ganadi 서버 시작 스크립트 실행 중..."

# 시스템 업데이트
apt-get update
apt-get upgrade -y

# Docker 설치
echo "🐳 Docker 설치 중..."
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
usermod -aG docker $USER

# Docker Compose 설치
echo "🐳 Docker Compose 설치 중..."
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# GCR 인증 설정
echo "🔐 GCR 인증 설정 중..."
gcloud auth configure-docker --quiet

# 애플리케이션 디렉토리 생성
mkdir -p /opt/ganadi
cd /opt/ganadi

# 환경 변수 설정
cat > .env << EOF
# Database configuration
DATABASE_URL=jdbc:postgresql://postgres:5432/ganadi
DATABASE_USERNAME=ganadi
DATABASE_PASSWORD=ganadi123

# JWT configuration
JWT_SECRET=production_jwt_secret_key_2024_change_me_in_production
JWT_EXPIRATION_SECONDS=3600

# CORS configuration
CORS_ORIGINS=https://your-frontend-domain.com,http://localhost:3000

# Spring profiles
SPRING_PROFILES_ACTIVE=prod
EOF

# Docker Compose 파일 생성
cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15-alpine
    container_name: ganadi-postgres
    environment:
      POSTGRES_DB: ganadi
      POSTGRES_USER: ganadi
      POSTGRES_PASSWORD: ganadi123
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - ganadi-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ganadi -d ganadi"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Spring Boot Application
  app:
    image: gcr.io/PROJECT_ID/ganadi-app:latest
    container_name: ganadi-app
    env_file:
      - .env
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - ganadi-network
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

volumes:
  postgres_data:

networks:
  ganadi-network:
    driver: bridge
EOF

# PROJECT_ID를 실제 프로젝트 ID로 교체
PROJECT_ID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
sed -i "s/PROJECT_ID/$PROJECT_ID/g" docker-compose.yml

# 로그 디렉토리 생성
mkdir -p /opt/ganadi/logs

# Docker Compose로 서비스 시작
echo "🚀 Ganadi 서비스 시작 중..."
docker-compose up -d

# 서비스 상태 확인
echo "📊 서비스 상태 확인 중..."
sleep 30
docker-compose ps

# 헬스체크
echo "🔍 헬스체크 실행 중..."
for i in {1..10}; do
    if curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 서비스가 정상적으로 시작되었습니다!"
        break
    else
        echo "⏳ 서비스 시작 대기 중... ($i/10)"
        sleep 10
    fi
done

# 자동 재시작 설정
echo "🔄 자동 재시작 설정 중..."
cat > /etc/systemd/system/ganadi.service << EOF
[Unit]
Description=Ganadi Application
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/opt/ganadi
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

systemctl enable ganadi.service

echo "🎉 Ganadi 서버 설정 완료!"
echo "📚 API 문서: http://$(curl -s ifconfig.me):8080/swagger-ui/index.html"
echo "🔍 헬스체크: http://$(curl -s ifconfig.me):8080/actuator/health"
