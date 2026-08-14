resource "kubernetes_namespace" "applicationbib" {
  metadata {
    name = "applicationbib"
  }
}
resource "kubernetes_namespace" "monitoring" {
  metadata {
    name = "monitoring"
  }
}