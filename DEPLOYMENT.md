# 🚀 Ganadi GCE 배포 가이드

## 📋 사전 준비

### 1. GCP 프로젝트 설정
```bash
# GCP CLI 설치 및 인증
gcloud auth login
gcloud config set project YOUR_PROJECT_ID

# 필요한 API 활성화
gcloud services enable compute.googleapis.com
gcloud services enable containerregistry.googleapis.com
```

### 2. Docker 설치 (로컬)
```bash
# macOS
brew install docker

# Ubuntu
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
```

## 🐳 로컬 테스트

### 1. Docker Compose로 로컬 테스트
```bash
# 서비스 시작
docker-compose up -d

# 로그 확인
docker-compose logs -f

# 서비스 중지
docker-compose down
```

### 2. 개별 서비스 테스트
```bash
# 데이터베이스만 시작
docker-compose up -d postgres

# 애플리케이션만 빌드 및 실행
docker build -t ganadi-app:latest .
docker run -p 8080:8080 --env-file .env ganadi-app:latest
```

## ☁️ GCE 배포

### 1. 배포 스크립트 실행
```bash
# 배포 스크립트에 실행 권한 부여
chmod +x deploy-gce.sh

# 배포 실행
./deploy-gce.sh
```

### 2. 수동 배포 (스크립트 사용 불가 시)

#### 2.1 Docker 이미지 빌드 및 푸시
```bash
# 이미지 빌드
docker build -t ganadi-app:latest .

# GCR에 태그 및 푸시
docker tag ganadi-app:latest gcr.io/YOUR_PROJECT_ID/ganadi-app:latest
docker push gcr.io/YOUR_PROJECT_ID/ganadi-app:latest
```

#### 2.2 GCE 인스턴스 생성
```bash
# 방화벽 규칙 생성
gcloud compute firewall-rules create allow-ganadi-http \
    --allow tcp:80,tcp:443,tcp:8080,tcp:22 \
    --source-ranges 0.0.0.0/0 \
    --target-tags ganadi-server

# 인스턴스 생성
gcloud compute instances create ganadi-server \
    --zone=asia-northeast3-a \
    --machine-type=e2-medium \
    --image-family=ubuntu-2004-lts \
    --image-project=ubuntu-os-cloud \
    --boot-disk-size=20GB \
    --tags=ganadi-server \
    --metadata-from-file startup-script=startup-script.sh
```

## 🔧 배포 후 관리

### 1. 서비스 상태 확인
```bash
# SSH 접속
gcloud compute ssh ganadi-server --zone=asia-northeast3-a

# 컨테이너 상태 확인
sudo docker ps

# 애플리케이션 로그 확인
sudo docker logs ganadi-app

# 헬스체크
curl http://localhost:8080/actuator/health
```

### 2. 서비스 재시작
```bash
# 애플리케이션만 재시작
sudo docker restart ganadi-app

# 전체 서비스 재시작
cd /opt/ganadi
sudo docker-compose restart
```

### 3. 로그 확인
```bash
# 실시간 로그 확인
sudo docker logs -f ganadi-app

# 시스템 로그 확인
sudo journalctl -u ganadi.service -f
```

## 🔄 업데이트 배포

### 1. 새 버전 배포
```bash
# 코드 변경 후
./deploy-gce.sh
```

### 2. 수동 업데이트
```bash
# SSH 접속
gcloud compute ssh ganadi-server --zone=asia-northeast3-a

# 새 이미지 풀
cd /opt/ganadi
sudo docker-compose pull app

# 서비스 재시작
sudo docker-compose up -d
```

## 🛠️ 문제 해결

### 1. 서비스가 시작되지 않는 경우
```bash
# 컨테이너 로그 확인
sudo docker logs ganadi-app

# 데이터베이스 연결 확인
sudo docker logs ganadi-postgres

# 네트워크 확인
sudo docker network ls
sudo docker network inspect ganadi_ganadi-network
```

### 2. 포트 충돌
```bash
# 포트 사용 확인
sudo netstat -tlnp | grep :8080

# 프로세스 종료
sudo kill -9 <PID>
```

### 3. 디스크 공간 부족
```bash
# 디스크 사용량 확인
df -h

# Docker 정리
sudo docker system prune -a
```

## 📊 모니터링

### 1. 헬스체크 엔드포인트
- **애플리케이션**: `http://YOUR_IP:8080/actuator/health`
- **API 문서**: `http://YOUR_IP:8080/swagger-ui/index.html`

### 2. 로그 모니터링
```bash
# 실시간 로그 모니터링
sudo tail -f /opt/ganadi/logs/ganadi.log

# 시스템 리소스 모니터링
htop
```

## 🔒 보안 설정

### 1. 방화벽 규칙
```bash
# 특정 IP만 허용
gcloud compute firewall-rules create allow-ganadi-restricted \
    --allow tcp:8080 \
    --source-ranges YOUR_IP/32 \
    --target-tags ganadi-server
```

### 2. SSL 인증서 설정 (선택사항)
```bash
# Let's Encrypt 인증서 발급
sudo apt install certbot
sudo certbot certonly --standalone -d your-domain.com
```

## 💰 비용 최적화

### 1. 인스턴스 크기 조정
```bash
# 더 작은 인스턴스로 변경
gcloud compute instances set-machine-type ganadi-server \
    --machine-type e2-micro \
    --zone asia-northeast3-a
```

### 2. 자동 종료 설정
```bash
# 스케줄된 종료 설정
gcloud compute instances add-metadata ganadi-server \
    --metadata shutdown-script='sudo shutdown -h 22:00'
```
