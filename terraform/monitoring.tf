resource "helm_release" "kube_prometheus_stack" {
  name       = "kube-prometheus-stack"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  repository = "https://prometheus-community.github.io/helm-charts"
  chart      = "kube-prometheus-stack"

  values = [
    yamlencode({
      grafana = {
        enabled = true
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
