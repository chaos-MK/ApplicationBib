resource "kubernetes_manifest" "vault_dashboard" {
  manifest = yamldecode(
    file("${path.module}/../k8s/monitoring/dashboards/vault-dashboard-configmap.yaml")
  )

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "ritual_growth_ui_dashboard" {
  metadata {
    name      = "ritual-growth-ui-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "ritual-growth-ui.json" = file("${path.module}/../k8s/monitoring/dashboards/ritual-growth-ui-dashboard.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}
