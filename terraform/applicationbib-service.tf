resource "kubernetes_service" "applicationbib" {
  metadata {
    name      = "applicationbib"
    namespace = "default"

    labels = {
      app = "applicationbib"
    }
  }

  spec {
    selector = {
      app = "applicationbib"
    }

    port {
      name        = "http"
      port        = 8080
      target_port = 8080
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }

  depends_on = [
    kubernetes_namespace.applicationbib
  ]

  lifecycle {
    ignore_changes = [
      wait_for_load_balancer
    ]
  }
}