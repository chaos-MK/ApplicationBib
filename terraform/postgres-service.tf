resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres"
    namespace = "default"
    labels = {
      app = "postgres"
    }
  }
  spec {
    cluster_ip = "None"
    selector = {
      app = "postgres"
    }
    port {
      name        = "postgres"
      port        = 5432
      target_port = 5432
      protocol    = "TCP"
    }
    port {
      name        = "metrics"
      port        = 9187
      target_port = 9187
      protocol    = "TCP"
    }
    type = "ClusterIP"
  }
  wait_for_load_balancer = false
}
