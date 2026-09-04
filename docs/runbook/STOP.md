# 1. Stop the Cloudflare Quick Tunnel
sudo pkill -f "cloudflared tunnel --url" || true

# 2. Stop Minikube
sudo minikube stop

# 3. Stop the Podman exporter
sudo podman stop podman-exporter
