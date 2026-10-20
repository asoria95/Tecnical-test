#!/usr/bin/env bash
# Deploy the banking microservices to the EC2 instance provisioned by Terraform.
#
# Prerequisites:
#   - terraform apply must have been run from the infra/ directory
#   - SSH key must be at ~/.ssh/id_rsa (or override with SSH_KEY env var)
#   - rsync must be available locally
#
# Usage:
#   ./deploy.sh                           # use default SSH key
#   SSH_KEY=~/.ssh/my-key ./deploy.sh     # use a custom SSH key

set -euo pipefail

SSH_KEY="${SSH_KEY:-$HOME/.ssh/id_rsa}"
REMOTE_DIR="/app"
SSH_OPTS="-i ${SSH_KEY} -o StrictHostKeyChecking=no -o ConnectTimeout=15"

# ── 1. Read Elastic IP from Terraform output ──────────────────────────────────
echo "Reading instance IP from Terraform..."
INSTANCE_IP=$(cd "$(dirname "$0")/infra" && terraform output -raw instance_public_ip)

if [[ -z "${INSTANCE_IP}" ]]; then
  echo "ERROR: Could not read instance_public_ip from Terraform output."
  echo "Run: cd infra && terraform apply"
  exit 1
fi

echo "Target: ubuntu@${INSTANCE_IP}"

# ── 2. Wait for SSH to be available ──────────────────────────────────────────
echo "Waiting for SSH to be ready..."
for i in $(seq 1 20); do
  if ssh ${SSH_OPTS} "ubuntu@${INSTANCE_IP}" "echo ok" &>/dev/null; then
    break
  fi
  echo "  Attempt ${i}/20 — retrying in 5s..."
  sleep 5
done

# ── 3. Sync project files to the VM ──────────────────────────────────────────
echo "Syncing project files..."
rsync -az --delete \
  --exclude='.git' \
  --exclude='**/build/' \
  --exclude='**/.gradle/' \
  --exclude='infra/.terraform/' \
  --exclude='infra/terraform.tfstate*' \
  -e "ssh ${SSH_OPTS}" \
  "$(dirname "$0")/" \
  "ubuntu@${INSTANCE_IP}:${REMOTE_DIR}/"

# ── 4. Build images and start containers ─────────────────────────────────────
echo "Building and starting containers..."
ssh ${SSH_OPTS} "ubuntu@${INSTANCE_IP}" bash -s <<'REMOTE'
set -euo pipefail
cd /app
docker compose up --build -d
echo "Waiting for services to be up..."
sleep 10
docker compose ps
REMOTE

# ── 5. Print service URLs ─────────────────────────────────────────────────────
echo ""
echo "Deployment complete."
echo ""
echo "  customer-service : http://${INSTANCE_IP}:8081/clientes"
echo "  account-service  : http://${INSTANCE_IP}:8080/cuentas"
echo ""
echo "  SSH              : ssh -i ${SSH_KEY} ubuntu@${INSTANCE_IP}"
echo "  Logs             : ssh -i ${SSH_KEY} ubuntu@${INSTANCE_IP} 'cd /app && docker compose logs -f'"
