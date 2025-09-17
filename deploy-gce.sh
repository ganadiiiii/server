#!/bin/bash

# Ganadi GCE 배포 스크립트
set -e

echo "🚀 Ganadi GCE 배포 시작..."

# GCE 인스턴스 정보 입력
read -p "GCE 인스턴스 이름을 입력하세요: " INSTANCE_NAME
read -p "GCE 존을 입력하세요 (예: asia-northeast3-a): " ZONE
read -p "GCE 프로젝트 ID를 입력하세요: " PROJECT_ID

echo "📋 배포 정보:"
echo "- 인스턴스: $INSTANCE_NAME"
echo "- 존: $ZONE"
echo "- 프로젝트: $PROJECT_ID"

# 1. Docker 이미지를 Container Registry에 푸시
echo "📦 Docker 이미지를 Container Registry에 푸시 중..."
docker tag ganadi-app:latest gcr.io/$PROJECT_ID/ganadi-app:latest
docker push gcr.io/$PROJECT_ID/ganadi-app:latest

# 2. GCE 인스턴스에 접속해서 배포
echo "🔄 GCE 인스턴스에 배포 중..."
gcloud compute ssh $INSTANCE_NAME --zone=$ZONE --command="
    # Docker 설치 확인
    if ! command -v docker &> /dev/null; then
        echo 'Docker 설치 중...'
        curl -fsSL https://get.docker.com -o get-docker.sh
        sh get-docker.sh
        sudo usermod -aG docker \$USER
    fi
    
    # Docker Compose 설치 확인
    if ! command -v docker-compose &> /dev/null; then
        echo 'Docker Compose 설치 중...'
        sudo curl -L \"https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-\$(uname -s)-\$(uname -m)\" -o /usr/local/bin/docker-compose
        sudo chmod +x /usr/local/bin/docker-compose
    fi
    
    # 프로젝트 디렉토리 생성
    mkdir -p ~/ganadi
    cd ~/ganadi
    
    # 환경 변수 파일 생성
    cat > env.production << 'EOF'
POSTGRES_DB=palmful
POSTGRES_USER=bloom_user
POSTGRES_PASSWORD=ganadi_secure_password_2024
SPRING_PROFILES_ACTIVE=docker
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/palmful
SPRING_DATASOURCE_USERNAME=bloom_user
SPRING_DATASOURCE_PASSWORD=ganadi_secure_password_2024
SPRING_JPA_HIBERNATE_DDL_AUTO=update
JWT_SECRET=ganadi_jwt_secret_key_256_bits_for_production_2024
JWT_EXPIRATION=86400000
APP_NAME=Ganadi
APP_VERSION=1.0.0
SERVER_PORT=8080
DOMAIN=your-domain.com
EMAIL=admin@ganadi.com
REDIS_PASSWORD=ganadi_redis_password_2024
NGINX_WORKER_PROCESSES=auto
NGINX_WORKER_CONNECTIONS=1024
EOF
    
    # Docker Compose 파일 생성
    cat > docker-compose.yml << 'EOF'
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: ganadi-postgres
    env_file:
      - env.production
    environment:
      POSTGRES_DB: \${POSTGRES_DB}
      POSTGRES_USER: \${POSTGRES_USER}
      POSTGRES_PASSWORD: \${POSTGRES_PASSWORD}
    expose:
      - \"5432\"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: [\"CMD-SHELL\", \"pg_isready -U \${POSTGRES_USER} -d \${POSTGRES_DB}\"]
      interval: 10s
      timeout: 5s
      retries: 5
    networks:
      - ganadi-network

  app:
    image: gcr.io/PROJECT_ID/ganadi-app:latest
    container_name: ganadi-app
    env_file:
      - env.production
    environment:
      SPRING_PROFILES_ACTIVE: \${SPRING_PROFILES_ACTIVE}
      SPRING_DATASOURCE_URL: \${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: \${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: \${SPRING_DATASOURCE_PASSWORD}
      SPRING_JPA_HIBERNATE_DDL_AUTO: \${SPRING_JPA_HIBERNATE_DDL_AUTO}
      JWT_SECRET: \${JWT_SECRET}
      JWT_EXPIRATION: \${JWT_EXPIRATION}
    ports:
      - \"8080:8080\"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: [\"CMD\", \"wget\", \"--no-verbose\", \"--tries=1\", \"--spider\", \"http://localhost:8080/actuator/health\"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    networks:
      - ganadi-network

volumes:
  postgres_data:

networks:
  ganadi-network:
    driver: bridge
EOF
    
    # PROJECT_ID 치환
    sed -i \"s/PROJECT_ID/$PROJECT_ID/g\" docker-compose.yml
    
    # 기존 컨테이너 정리
    docker-compose down --remove-orphans || true
    
    # 서비스 시작
    docker-compose up -d
    
    echo '✅ 배포 완료!'
    echo '🌐 접속 정보:'
    echo '- API 서버: http://EXTERNAL_IP:8080'
    echo '- Swagger UI: http://EXTERNAL_IP:8080/swagger-ui/'
    echo '- Health Check: http://EXTERNAL_IP:8080/actuator/health'
"

echo "🎉 GCE 배포 완료!"
echo "📋 다음 단계:"
echo "1. GCE 방화벽에서 8080 포트 열기"
echo "2. 외부 IP로 접속 테스트"
echo "3. SSL 설정 (선택사항)"