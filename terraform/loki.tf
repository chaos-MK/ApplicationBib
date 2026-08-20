resource "helm_release" "loki" {
  name       = "loki"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  repository = "https://grafana.github.io/helm-charts"
  chart      = "loki"
  version    = "6.6.4"

  values = [
    yamlencode({
      loki = {
        auth_enabled = false
        commonConfig = {
          replication_factor = 1
        }
        storage = {
          type = "filesystem"
        }
        schemaConfig = {
          configs = [
            {
              from         = "2024-01-01"
              store        = "tsdb"
              object_store = "filesystem"
              schema       = "v13"
              index = {
                prefix = "index_"
                period = "24h"
              }
            }
          ]
        }
      }
      deploymentMode = "SingleBinary"
      singleBinary = {
        replicas = 1
        resources = {
          requests = {
            cpu    = "100m"
            memory = "256Mi"
          }
          limits = {
            cpu    = "500m"
            memory = "512Mi"
          }
        }
      }
      # Disable extra components not needed for single-binary dev setup
      read         = { replicas = 0 }
      write        = { replicas = 0 }
      backend      = { replicas = 0 }
      chunksCache  = { enabled = false }
      resultsCache = { enabled = false }
      gateway      = { enabled = false }
      test         = { enabled = false }
      lokiCanary   = { enabled = false }
    })
  ]

  depends_on = [
    kubernetes_namespace.monitoring
  ]
}

resource "kubernetes_manifest" "loki_servicemonitor" {
  manifest = yamldecode(file("${path.module}/loki-servicemonitor.yaml"))

  depends_on = [
    helm_release.loki,
    helm_release.kube_prometheus_stack
  ]
}
