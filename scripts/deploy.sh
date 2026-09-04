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
echo "[2/3] Applying ApplicationBib manifests..."
kubectl apply -f "$ROOT_DIR/k8s/app/"

echo
echo "[3/3] Waiting for ApplicationBib rollout..."
kubectl rollout status deployment/applicationbib \
  -n default \
  --timeout=180s

echo
echo "======================================"
echo " Deployment completed successfully"
echo "======================================"

kubectl get deployment applicationbib -n default
kubectl get statefulset postgres -n default