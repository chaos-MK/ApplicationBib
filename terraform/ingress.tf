resource "helm_release" "ingress_nginx" {
  name       = "ingress-nginx"
  namespace  = "ingress-nginx"
  repository = "https://kubernetes.github.io/ingress-nginx"
  chart      = "ingress-nginx"
  timeout    = 600
  wait       = true

  create_namespace = true

  values = [
    yamlencode({
      controller = {
        service = {
          type = "NodePort"
        }
        config = {
          "worker-processes" = "2"
        }

        resources = {
          requests = {
            cpu    = "100m"
            memory = "90Mi"
          }

          limits = {
            cpu    = "500m"
            memory = "256Mi"
          }
        }
      }
    })
  ]
}

resource "kubernetes_ingress_v1" "applicationbib" {
  metadata {
    name      = "applicationbib-ingress"
    namespace = "default"
    annotations = {
      "nginx.ingress.kubernetes.io/rewrite-target" = "/$2"
    }
  }

  spec {
    ingress_class_name = "nginx"

    rule {
      http {
        path {
          path      = "/app(/|$)(.*)"
          path_type = "ImplementationSpecific"

          backend {
            service {
              name = "applicationbib"
              port { number = 8080 }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_ingress_v1" "grafana" {
  metadata {
    name      = "grafana-ingress"
    namespace = "monitoring"
  }

  spec {
    ingress_class_name = "nginx"

    rule {
      http {
        path {
          path      = "/grafana"
          path_type = "Prefix"

          backend {
            service {
              name = "kube-prometheus-stack-grafana"

              port {
                number = 80
              }
            }
          }
        }
      }
    }
  }
}