#!/usr/bin/env bash

set -euo pipefail

CLOUDFLARED="/usr/local/bin/cloudflared"
KUBECTL="/usr/local/bin/kubectl"

DEPLOYMENT="applicationbib"
NAMESPACE="default"
TUNNEL_TARGET="http://192.168.49.2:31573"
LOG_FILE="/tmp/ritual-growth-cloudflared.log"

echo "===== STARTING CLOUDFLARE QUICK TUNNEL ====="

# Stop any previous Quick Tunnel
pkill -f "cloudflared tunnel --url" 2>/dev/null || true

# Start Quick Tunnel
"$CLOUDFLARED" tunnel --url "$TUNNEL_TARGET" > "$LOG_FILE" 2>&1 &

CLOUDFLARED_PID=$!

echo "cloudflared PID: $CLOUDFLARED_PID"
echo "Waiting for Quick Tunnel URL..."

FRONTEND_ORIGIN=""

for i in {1..30}; do
    FRONTEND_ORIGIN=$(
        grep -oE 'https://[a-z0-9-]+\.trycloudflare\.com' "$LOG_FILE" 2>/dev/null \
        | head -n1 || true
    )

    if [ -n "$FRONTEND_ORIGIN" ]; then
        break
    fi

    sleep 2
done

if [ -z "$FRONTEND_ORIGIN" ]; then
    echo "ERROR: Cloudflare Quick Tunnel URL was not detected."
    echo
    echo "===== CLOUDFLARED LOG ====="
    cat "$LOG_FILE"
    exit 1
fi

echo
echo "Cloudflare URL detected:"
echo "$FRONTEND_ORIGIN"

echo
echo "===== UPDATING BACKEND FRONTEND_ORIGIN ====="

"$KUBECTL" -n "$NAMESPACE" set env \
    deployment/"$DEPLOYMENT" \
    FRONTEND_ORIGIN="$FRONTEND_ORIGIN"

echo
echo "===== WAITING FOR BACKEND ROLLOUT ====="

"$KUBECTL" -n "$NAMESPACE" rollout status \
    deployment/"$DEPLOYMENT" \
    --timeout=120s

echo
echo "===== VERIFYING RUNTIME CONFIGURATION ====="

RUNTIME_ORIGIN=$(
    "$KUBECTL" -n "$NAMESPACE" get deployment "$DEPLOYMENT" \
    -o jsonpath='{.spec.template.spec.containers[0].env[?(@.name=="FRONTEND_ORIGIN")].value}'
)

echo "Runtime FRONTEND_ORIGIN:"
echo "$RUNTIME_ORIGIN"

echo
echo "=============================================="
echo "Cloudflare Quick Tunnel is READY"
echo "Frontend URL: $FRONTEND_ORIGIN"
echo "=============================================="
