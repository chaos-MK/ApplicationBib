#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SECRETS_FILE="$ROOT_DIR/secrets.env"

NAMESPACE="default"
SECRET_NAME="gitlab-registry-secret"

if [[ ! -f "$SECRETS_FILE" ]]; then
  echo "ERROR: secrets file not found: $SECRETS_FILE" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$SECRETS_FILE"

: "${GITLAB_REGISTRY:?GITLAB_REGISTRY is required}"
: "${GITLAB_REGISTRY_USER:?GITLAB_REGISTRY_USER is required}"
: "${GITLAB_REGISTRY_PASSWORD:?GITLAB_REGISTRY_PASSWORD is required}"

echo "======================================"
echo " GitLab Registry Secret Rotation"
echo "======================================"

echo
echo "[1/3] Checking Kubernetes..."
kubectl cluster-info >/dev/null
echo "✓ Kubernetes reachable"

echo
echo "[2/3] Updating registry secret..."

kubectl create secret docker-registry "$SECRET_NAME" \
  --namespace "$NAMESPACE" \
  --docker-server="$GITLAB_REGISTRY" \
  --docker-username="$GITLAB_REGISTRY_USER" \
  --docker-password="$GITLAB_REGISTRY_PASSWORD" \
  --dry-run=client \
  -o yaml |
kubectl apply -f -

echo "✓ Registry secret updated"

echo
echo "[3/3] Verifying secret..."

SECRET_TYPE="$(kubectl get secret "$SECRET_NAME" \
  --namespace "$NAMESPACE" \
  -o jsonpath='{.type}')"

if [[ "$SECRET_TYPE" != "kubernetes.io/dockerconfigjson" ]]; then
  echo "ERROR: unexpected secret type: $SECRET_TYPE" >&2
  exit 1
fi

echo "✓ Secret type: $SECRET_TYPE"

echo
echo "======================================"
echo " Registry secret rotation completed"
echo "======================================"
