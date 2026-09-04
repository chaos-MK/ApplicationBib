#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "======================================"
echo " ApplicationBib Deployment"
echo "======================================"

echo
echo "[1/3] Checking Kubernetes..."
kubectl cluster-info >/dev/null
echo "✓ Kubernetes reachable"

echo
echo "[2/3] Applying Terraform-managed infrastructure..."
terraform -chdir="$ROOT_DIR/terraform" apply -auto-approve
echo "✓ Terraform apply completed"

echo
echo "[3/3] Waiting for application rollouts..."

kubectl rollout status deployment/applicationbib \
  -n default \
  --timeout=180s

kubectl rollout status deployment/ritual-growth-ui \
  -n default \
  --timeout=180s

kubectl rollout status statefulset/postgres \
  -n default \
  --timeout=180s

echo
echo "======================================"
echo " Deployment completed successfully"
echo "======================================"

kubectl get deployment applicationbib ritual-growth-ui -n default
kubectl get statefulset postgres -n default
