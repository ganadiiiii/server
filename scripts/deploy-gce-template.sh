#!/bin/bash
# Ganadi: 로컬 코드 변경 후 빌드 → GCR 푸시 → GCE에서 Pull/기동 → 상태 확인 템플릿
# 사용 전 아래 변수만 수정하세요.

set -euo pipefail

# ====== 설정 ======
PROJECT_ID="palmful-project"
ZONE="asia-northeast3-c"
INSTANCE="palmful-instance"
IMAGE_NAME="ganadi-app"
IMAGE_REF="gcr.io/${PROJECT_ID}/${IMAGE_NAME}:latest"
VM_DIR="/home/ericmun0206/ganadi"   # VM에서 compose/env가 존재하는 디렉터리(콘솔 SSH 사용자 기준)
HEALTH_URL="http://localhost:8080/actuator/health"
# ==================

say() { echo "[deploy] $*"; }

say "GCP 인증/프로젝트 설정 확인"
gcloud auth login --brief >/dev/null 2>&1 || true
gcloud config set project "${PROJECT_ID}"
gcloud auth configure-docker gcr.io --quiet

say "Container Registry API 활성화(최초 1회만)"
gcloud services enable containerregistry.googleapis.com >/dev/null 2>&1 || true

say "buildx 빌더 준비"
docker buildx create --use --name ganadi-builder >/dev/null 2>&1 || docker buildx use ganadi-builder

echo
say "linux/amd64 이미지 빌드 및 푸시: ${IMAGE_REF}"
docker buildx build --platform linux/amd64 -t "${IMAGE_REF}" --push .

echo
say "GCE VM(${INSTANCE})에서 compose pull/up"
gcloud compute ssh "${INSTANCE}" --zone="${ZONE}" -- -t "bash -lc '
  set -e
  cd "${VM_DIR}"
  echo "== pull 이미지 =="
  sudo docker compose -f docker-compose.gce.yml --env-file env.gce pull
  echo "== up -d =="
  sudo docker compose -f docker-compose.gce.yml --env-file env.gce up -d
  echo "== 상태 =="
  sudo docker compose -f docker-compose.gce.yml --env-file env.gce ps
  echo "== 헬스 체크 =="
  for i in 1 2 3 4 5 6 7 8 9 10; do
    if curl -sf "${HEALTH_URL}" >/dev/null; then echo "HEALTH: OK"; exit 0; fi
    echo retry:$i; sleep 3;
  done
  echo "HEALTH: FAILED"; exit 1
'"

say "배포 완료"
echo "- 외부 헬스:   http://<VM_PUBLIC_IP>:8080/actuator/health"
echo "- Swagger UI:  http://<VM_PUBLIC_IP>:8080/swagger-ui/index.html"
