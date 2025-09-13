#!/bin/bash

# GCE 배포 스크립트
set -e

# 설정 변수
PROJECT_ID="your-gcp-project-id"
ZONE="asia-northeast3-a"
INSTANCE_NAME="ganadi-server"
MACHINE_TYPE="e2-medium"
IMAGE_FAMILY="ubuntu-2004-lts"
IMAGE_PROJECT="ubuntu-os-cloud"
DISK_SIZE="20GB"
TAG="ganadi-server"

# 색상 출력
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Ganadi GCE 배포를 시작합니다...${NC}"

# GCP 프로젝트 설정 확인
echo -e "${YELLOW}📋 GCP 프로젝트 설정 확인 중...${NC}"
if ! gcloud config get-value project > /dev/null 2>&1; then
    echo -e "${RED}❌ GCP 프로젝트가 설정되지 않았습니다.${NC}"
    echo "gcloud config set project $PROJECT_ID"
    exit 1
fi

CURRENT_PROJECT=$(gcloud config get-value project)
if [ "$CURRENT_PROJECT" != "$PROJECT_ID" ]; then
    echo -e "${YELLOW}⚠️  현재 프로젝트: $CURRENT_PROJECT"
    echo -e "설정된 프로젝트: $PROJECT_ID${NC}"
    read -p "계속하시겠습니까? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Docker 이미지 빌드
echo -e "${YELLOW}🐳 Docker 이미지 빌드 중...${NC}"
docker build -t ganadi-app:latest .

# Docker 이미지를 GCR에 푸시
echo -e "${YELLOW}📤 Docker 이미지를 GCR에 푸시 중...${NC}"
docker tag ganadi-app:latest gcr.io/$PROJECT_ID/ganadi-app:latest
docker push gcr.io/$PROJECT_ID/ganadi-app:latest

# GCE 인스턴스 생성 또는 업데이트
echo -e "${YELLOW}🖥️  GCE 인스턴스 설정 중...${NC}"

# 인스턴스가 존재하는지 확인
if gcloud compute instances describe $INSTANCE_NAME --zone=$ZONE > /dev/null 2>&1; then
    echo -e "${YELLOW}📝 기존 인스턴스 업데이트 중...${NC}"
    
    # 기존 인스턴스 중지
    gcloud compute instances stop $INSTANCE_NAME --zone=$ZONE
    
    # 새로운 이미지로 부팅 디스크 교체
    gcloud compute instances set-disk-auto-delete $INSTANCE_NAME --zone=$ZONE --disk=$INSTANCE_NAME-disk
    
    # 인스턴스 시작
    gcloud compute instances start $INSTANCE_NAME --zone=$ZONE
else
    echo -e "${YELLOW}🆕 새 인스턴스 생성 중...${NC}"
    
    # 방화벽 규칙 생성 (HTTP, HTTPS, SSH)
    gcloud compute firewall-rules create allow-ganadi-http \
        --allow tcp:80,tcp:443,tcp:8080,tcp:22 \
        --source-ranges 0.0.0.0/0 \
        --target-tags $TAG \
        --description "Allow HTTP, HTTPS, and SSH for Ganadi server" || true
    
    # 인스턴스 생성
    gcloud compute instances create $INSTANCE_NAME \
        --zone=$ZONE \
        --machine-type=$MACHINE_TYPE \
        --image-family=$IMAGE_FAMILY \
        --image-project=$IMAGE_PROJECT \
        --boot-disk-size=$DISK_SIZE \
        --boot-disk-type=pd-standard \
        --tags=$TAG \
        --metadata-from-file startup-script=startup-script.sh
fi

# 인스턴스 IP 주소 가져오기
echo -e "${YELLOW}🌐 인스턴스 IP 주소 확인 중...${NC}"
EXTERNAL_IP=$(gcloud compute instances describe $INSTANCE_NAME --zone=$ZONE --format='get(networkInterfaces[0].accessConfigs[0].natIP)')
INTERNAL_IP=$(gcloud compute instances describe $INSTANCE_NAME --zone=$ZONE --format='get(networkInterfaces[0].networkIP)')

echo -e "${GREEN}✅ 배포 완료!${NC}"
echo -e "${GREEN}🌐 외부 IP: $EXTERNAL_IP${NC}"
echo -e "${GREEN}🏠 내부 IP: $INTERNAL_IP${NC}"
echo -e "${GREEN}📚 API 문서: http://$EXTERNAL_IP:8080/swagger-ui/index.html${NC}"
echo -e "${GREEN}🔍 헬스체크: http://$EXTERNAL_IP:8080/actuator/health${NC}"

# SSH 접속 명령어 출력
echo -e "${YELLOW}🔑 SSH 접속:${NC}"
echo "gcloud compute ssh $INSTANCE_NAME --zone=$ZONE"

# 로그 확인 명령어 출력
echo -e "${YELLOW}📋 로그 확인:${NC}"
echo "gcloud compute ssh $INSTANCE_NAME --zone=$ZONE --command='sudo docker logs ganadi-app'"
