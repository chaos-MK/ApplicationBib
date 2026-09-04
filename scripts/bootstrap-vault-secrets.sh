#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SECRETS_FILE="$ROOT_DIR/secrets.env"

if [[ ! -f "$SECRETS_FILE" ]]; then
  echo "ERROR: secrets file not found: $SECRETS_FILE" >&2
  exit 1
fi

# shellcheck disable=SC1090
source "$SECRETS_FILE"

: "${POSTGRES_USERNAME:?POSTGRES_USERNAME is required}"
: "${POSTGRES_PASSWORD:?POSTGRES_PASSWORD is required}"
: "${ALERTMANAGER_SMTP_USERNAME:?ALERTMANAGER_SMTP_USERNAME is required}"
: "${ALERTMANAGER_SMTP_PASSWORD:?ALERTMANAGER_SMTP_PASSWORD is required}"
: "${FIREBASE_SERVICE_ACCOUNT_JSON:?FIREBASE_SERVICE_ACCOUNT_JSON is required}"

VAULT_NAMESPACE="vault"

echo "======================================"
echo " Vault Secrets Bootstrap"
echo "======================================"

echo
echo "[1/4] Checking Kubernetes..."
kubectl cluster-info >/dev/null
echo "✓ Kubernetes reachable"

echo
echo "[2/4] Checking Vault..."
kubectl get pod -n "$VAULT_NAMESPACE" vault-0 \
  --no-headers >/dev/null
echo "✓ Vault pod found"

echo
echo "[3/4] Checking Vault..."

if ! kubectl exec -n "$VAULT_NAMESPACE" vault-0 -- vault status >/dev/null 2>&1; then
  echo "ERROR: Vault is not reachable."
  echo "Make sure Vault is running and unsealed."
  exit 1
fi

SEALED="$(kubectl exec -n "$VAULT_NAMESPACE" vault-0 -- vault status -format=json | sed -n 's/.*"sealed":[[:space:]]*\(true\|false\).*/\1/p')"

if [[ "$SEALED" != "false" ]]; then
  echo "ERROR: Vault is sealed."
  exit 1
fi

echo "✓ Vault reachable and unsealed"

echo
echo "[4/4] Writing secrets..."

kubectl exec -i -n "$VAULT_NAMESPACE" vault-0 --   vault kv put -cas=1 secret/applicationbib/db   username="$POSTGRES_USERNAME"   password="$POSTGRES_PASSWORD"

echo "✓ PostgreSQL credentials written"

kubectl exec -i -n "$VAULT_NAMESPACE" vault-0 --   vault kv put -cas=1 secret/applicationbib/firebase   service-account.json="$FIREBASE_SERVICE_ACCOUNT_JSON"

echo "✓ Firebase service-account credential written"

kubectl exec -i -n "$VAULT_NAMESPACE" vault-0 --   vault kv put secret/alertmanager/smtp   username="$ALERTMANAGER_SMTP_USERNAME"   password="$ALERTMANAGER_SMTP_PASSWORD"

echo "✓ Alertmanager SMTP credentials written"

echo
echo "======================================"
echo " Vault bootstrap completed"
echo "======================================"
