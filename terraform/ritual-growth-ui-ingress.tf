resource "kubernetes_ingress_v1" "ritual_growth_ui" {
  metadata {
    name      = "ritual-growth-ui-ingress"
    namespace = "default"
  }

  spec {
    ingress_class_name = "nginx"

    rule {
      http {
        path {
          path      = "/"
          path_type = "Prefix"

          backend {
            service {
              name = "ritual-growth-ui"

              port {
                number = 3000
              }
            }
          }
        }
      }
    }
  }
}
