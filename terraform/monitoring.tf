resource "helm_release" "kube_prometheus_stack" {
  name       = "kube-prometheus-stack"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  repository = "https://prometheus-community.github.io/helm-charts"
  chart      = "kube-prometheus-stack"
  values = [
    yamlencode({
      grafana = {
        enabled = true
        "grafana.ini" = {
          server = {
            root_url            = "http://192.168.49.2:31573/grafana/"
            serve_from_sub_path = true
          }
        }
      }
      prometheus = {
        prometheusSpec = {
          retention = "7d"
        }
      }
      alertmanager = {
        enabled = true
      }
    })
  ]
  depends_on = [
    kubernetes_namespace.monitoring
  ]
}
resource "kubernetes_manifest" "podman_exporter_service" {
  manifest = yamldecode(file("${path.module}/podman-exporter-service.yaml"))

  depends_on = [
    kubernetes_namespace.monitoring
  ]
}

resource "kubernetes_manifest" "podman_exporter_endpoints" {
  manifest = yamldecode(file("${path.module}/podman-exporter-endpoints.yaml"))

  depends_on = [
    kubernetes_manifest.podman_exporter_service
  ]
}

resource "kubernetes_manifest" "podman_exporter_servicemonitor" {
  manifest = yamldecode(file("${path.module}/podman-exporter-servicemonitor.yaml"))

  depends_on = [
    kubernetes_manifest.podman_exporter_service,
    helm_release.kube_prometheus_stack
  ]
}
