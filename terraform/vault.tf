resource "kubernetes_namespace" "vault" {
  metadata {
    name = "vault"
  }
}

resource "kubernetes_manifest" "vault_servicemonitor" {
  manifest = yamldecode(file("${path.module}/vault-servicemonitor.yaml"))

  depends_on = [
    kubernetes_namespace.vault,
    kubernetes_namespace.monitoring,
    helm_release.kube_prometheus_stack
  ]
}
