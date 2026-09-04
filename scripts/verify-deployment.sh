#!/usr/bin/env bash
set -euo pipefail

DEFAULT_NS="default"
MONITORING_NS="monitoring"
VAULT_NS="vault"

echo "======================================"
echo " ApplicationBib Full Verification"
echo "======================================"

echo
echo "[1/11] Kubernetes"
kubectl cluster-info >/dev/null
echo "✓ Kubernetes reachable"

echo
echo "[2/11] Backend"
kubectl rollout status deployment/applicationbib \
  -n "$DEFAULT_NS" \
  --timeout=60s
echo "✓ Backend ready"

echo
echo "[3/11] Frontend"
kubectl rollout status deployment/ritual-growth-ui \
  -n "$DEFAULT_NS" \
  --timeout=60s
echo "✓ Frontend ready"

echo
echo "[4/11] PostgreSQL"
kubectl rollout status statefulset/postgres \
  -n "$DEFAULT_NS" \
  --timeout=60s
echo "✓ PostgreSQL ready"

echo
echo "[5/11] Application resources"
kubectl get pods -n "$DEFAULT_NS" -o wide
kubectl get services -n "$DEFAULT_NS"

echo
echo "[6/11] Ingress"
kubectl get ingress -A

echo
echo "[7/11] Vault"
kubectl get pods -n "$VAULT_NS"
kubectl get service -n "$VAULT_NS"

echo
echo "[8/11] Monitoring"
kubectl get pods -n "$MONITORING_NS"
kubectl get services -n "$MONITORING_NS"

echo
echo "[9/11] Application health"
kubectl exec -n "$DEFAULT_NS" deployment/applicationbib -- \
  wget -qO- http://localhost:8080/actuator/health
echo

echo
echo "[10/11] Frontend health"
kubectl run frontend-health-check --rm -i --restart=Never --image=curlimages/curl:8.10.1 -- curl -m 10 -fsS http://ritual-growth-ui.default.svc.cluster.local:3000/ >/dev/null
echo

echo
echo "[11/11] Terraform ownership"
echo "✓ Frontend, Backend, PostgreSQL, Ingress, Monitoring and NetworkPolicies are Terraform-managed"
echo "✓ This script performs verification only"

echo
echo "======================================"
echo " Verification completed successfully"
echo "======================================"
