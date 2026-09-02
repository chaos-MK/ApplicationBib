resource "helm_release" "kube_prometheus_stack" {
  name       = "kube-prometheus-stack"
  namespace  = kubernetes_namespace.monitoring.metadata[0].name
  repository = "https://prometheus-community.github.io/helm-charts"
  chart      = "kube-prometheus-stack"

  values = [
    yamlencode({
      grafana = {
        enabled = true

        additionalDataSources = [
          {
            name      = "Loki"
            type      = "loki"
            uid       = "loki"
            access    = "proxy"
            url       = "http://loki.monitoring.svc.cluster.local:3100"
            isDefault = false
          }
        ]

        "grafana.ini" = {
          server = {
            root_url            = var.grafana_root_url
            serve_from_sub_path = true
          }
        }
      }

      kubeEtcd = {
        enabled = false
      }

      kubeScheduler = {
        enabled = false
      }

      kubeControllerManager = {
        enabled = false
      }

      prometheus = {
        prometheusSpec = {
          retention = "7d"

          additionalScrapeConfigs = []
        }
      }

      alertmanager = {
        enabled = true

        config = {
          global = {
            resolve_timeout         = "5m"
            smtp_auth_password_file = "/vault/secrets/smtp-password"
            smtp_auth_username      = "educationforme545@gmail.com"
            smtp_from               = "educationforme545@gmail.com"
            smtp_require_tls        = true
            smtp_smarthost          = "smtp.gmail.com:587"
          }

          inhibit_rules = [
            {
              equal           = ["namespace", "alertname"]
              source_matchers = ["severity = critical"]
              target_matchers = ["severity =~ warning|info"]
            },
            {
              equal           = ["namespace", "alertname"]
              source_matchers = ["severity = warning"]
              target_matchers = ["severity = info"]
            },
            {
              equal           = ["namespace"]
              source_matchers = ["alertname = InfoInhibitor"]
              target_matchers = ["severity = info"]
            },
            {
              target_matchers = ["alertname = InfoInhibitor"]
            }
          ]

          receivers = [
            {
              name = "null"
            },
            {
              name = "default-notifications"

              email_configs = [
                {
                  to = "khalilmohamed798@gmail.com"
                }
              ]

              webhook_configs = [
                {
                  send_resolved = true
                  url           = "https://webhook.site/3d9fefed-af9d-4c7c-ad17-294b00d04dd3"
                }
              ]
            }
          ]

          route = {
            group_by        = ["namespace", "alertname"]
            group_interval  = "5m"
            group_wait      = "30s"
            receiver        = "default-notifications"
            repeat_interval = "12h"

            routes = [
              {
                matchers = ["alertname = \"Watchdog\""]
                receiver = "null"
              }
            ]
          }
        }

        alertmanagerSpec = {
          podMetadata = {
            annotations = {
              "vault.hashicorp.com/agent-inject" = "true"

              "vault.hashicorp.com/agent-inject-secret-smtp-password" = "secret/data/alertmanager/smtp"

              "vault.hashicorp.com/agent-inject-template-smtp-password" = <<-EOT
                {{- with secret "secret/data/alertmanager/smtp" -}}
                {{- .Data.data.password -}}
                {{- end }}
                EOT

              "vault.hashicorp.com/role" = "alertmanager"
            }
          }


        }
      }
    })
  ]

  depends_on = [
    kubernetes_namespace.monitoring
  ]
}

resource "kubernetes_manifest" "podman_exporter_service" {
  manifest = yamldecode(file("${path.module}/podman-exporter-service.yaml"))

  depends_on = [
    kubernetes_namespace.monitoring
  ]
}

resource "kubernetes_manifest" "podman_exporter_endpoints" {
  manifest = yamldecode(file("${path.module}/podman-exporter-endpoints.yaml"))

  depends_on = [
    kubernetes_manifest.podman_exporter_service
  ]
}

resource "kubernetes_manifest" "podman_exporter_servicemonitor" {
  manifest = yamldecode(file("${path.module}/podman-exporter-servicemonitor.yaml"))

  depends_on = [
    kubernetes_manifest.podman_exporter_service,
    helm_release.kube_prometheus_stack
  ]
}
