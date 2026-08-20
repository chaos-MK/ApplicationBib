variable "grafana_root_url" {
  description = "Public URL Grafana is served on, including the /grafana sub-path. Override this per-environment (e.g. -var grafana_root_url=http://<minikube-ip>:<nodeport>/grafana/) rather than hardcoding it."
  type        = string
  default     = "http://192.168.49.2:31573/grafana/"
}
