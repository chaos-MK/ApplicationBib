resource "helm_release" "alloy" {
  name       = "alloy"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  repository = "https://grafana.github.io/helm-charts"
  chart      = "alloy"

  values = [
    yamlencode({
      alloy = {
        configMap = {
          content = <<-EOT
            discovery.kubernetes "pods" {
              role = "pod"
            }

            discovery.relabel "pods" {
              targets = discovery.kubernetes.pods.targets

              rule {
                source_labels = ["__meta_kubernetes_namespace"]
                target_label  = "namespace"
              }
              rule {
                source_labels = ["__meta_kubernetes_pod_name"]
                target_label  = "pod"
              }
              rule {
                source_labels = ["__meta_kubernetes_pod_container_name"]
                target_label  = "container"
              }
            }

            loki.source.kubernetes "pods" {
              targets    = discovery.relabel.pods.output
              forward_to = [loki.write.default.receiver]
            }

            loki.write "default" {
              endpoint {
                url = "http://loki.monitoring.svc.cluster.local:3100/loki/api/v1/push"
              }
            }
          EOT
        }
      }
      controller = {
        type = "daemonset"
      }
    })
  ]

  depends_on = [
    kubernetes_namespace.monitoring,
    helm_release.loki
  ]
}
