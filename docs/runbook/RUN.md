
# start minikube server
sudo minikube start --driver=podman --force --container-runtime=containerd
sudo podman start podman-exporter

# Unseal Vault
sudo kubectl exec -it -n vault vault-0 -- vault operator unseal
sudo kubectl exec -it -n vault vault-0 -- vault operator unseal
sudo kubectl exec -it -n vault vault-0 -- vault operator unseal

# Check everything
kubectl get pods -n vault
kubectl get pods -n default
kubectl get pods -n monitoring
kubectl get pods -n cert-manager
kubectl get pods -n ingress-nginx

# Generate new Quick Tunnel + inject runtime FRONTEND_ORIGIN
sudo ~/ApplicationBib/start-ritual-growth-tunnel.sh
