resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres"
    namespace = "default"
  }

  spec {
    cluster_ip = "None"

    selector = {
      app = "postgres"
    }

    port {
      port        = 5432
      target_port = 5432
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }

  wait_for_load_balancer = false
}
