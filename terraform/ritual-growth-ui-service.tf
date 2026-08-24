resource "kubernetes_service" "ritual_growth_ui" {
  metadata {
    name      = "ritual-growth-ui"
    namespace = "default"

    labels = {
      app = "ritual-growth-ui"
    }
  }

  spec {
    selector = {
      app = "ritual-growth-ui"
    }

    port {
      name        = "http"
      port        = 3000
      target_port = 3000
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }

  lifecycle {
    ignore_changes = [
      wait_for_load_balancer
    ]
  }
}
