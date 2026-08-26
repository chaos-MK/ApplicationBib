resource "kubernetes_manifest" "ritual_growth_ui_servicemonitor" {
  manifest = {
    apiVersion = "monitoring.coreos.com/v1"
    kind       = "ServiceMonitor"

    metadata = {
      name      = "ritual-growth-ui"
      namespace = "monitoring"

      labels = {
        release = "kube-prometheus-stack"
      }
    }

    spec = {
      namespaceSelector = {
        matchNames = ["default"]
      }

      selector = {
        matchLabels = {
          app = "ritual-growth-ui"
        }
      }

      endpoints = [
        {
          port     = "http"
          path     = "/api/metrics"
          interval = "15s"
        }
      ]
    }
  }
}
