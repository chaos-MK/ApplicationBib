resource "kubernetes_manifest" "vault_dashboard" {
  manifest = yamldecode(
    file("${path.module}/../k8s/monitoring/dashboards/vault-dashboard-configmap.yaml")
  )

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}
