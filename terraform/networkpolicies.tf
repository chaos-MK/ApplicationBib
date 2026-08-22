resource "kubernetes_manifest" "applicationbib_netpol" {
  manifest = yamldecode(file("${path.module}/applicationbib-netpol.yaml"))

  depends_on = [
    kubernetes_namespace.applicationbib
  ]
}

resource "kubernetes_manifest" "postgres_netpol" {
  manifest = yamldecode(file("${path.module}/postgres-netpol.yaml"))

  depends_on = [
    kubernetes_namespace.applicationbib
  ]
}

resource "kubernetes_manifest" "vault_server_netpol" {
  manifest = yamldecode(file("${path.module}/vault-server-netpol.yaml"))

  depends_on = [
    kubernetes_namespace.vault
  ]
}
