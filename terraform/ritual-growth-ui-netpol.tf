resource "kubernetes_manifest" "ritual_growth_ui_netpol" {
  manifest = yamldecode(file("${path.module}/ritual-growth-ui-netpol.yaml"))

  depends_on = [
    kubernetes_deployment_v1.ritual_growth_ui
  ]
}
