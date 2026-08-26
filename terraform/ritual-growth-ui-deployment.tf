resource "kubernetes_deployment_v1" "ritual_growth_ui" {
  metadata {
    name      = "ritual-growth-ui"
    namespace = "default"

    labels = {
      app = "ritual-growth-ui"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "ritual-growth-ui"
      }
    }

    template {
      metadata {
        labels = {
          app = "ritual-growth-ui"
        }
      }

      spec {
        automount_service_account_token = false
        enable_service_links            = false

        image_pull_secrets {
          name = "gitlab-registry-secret"
        }

        container {
          name              = "ritual-growth-ui"
          image             = "registry.gitlab.com/khalilmohamed798/ritual-growth-ui:9bee2edd"
          image_pull_policy = "Always"

          port {
            container_port = 3000
            protocol       = "TCP"
          }

          startup_probe {
            http_get {
              path   = "/"
              port   = 3000
              scheme = "HTTP"
            }

            initial_delay_seconds = 5
            period_seconds        = 5
            failure_threshold     = 12
            timeout_seconds       = 3
          }

          readiness_probe {
            http_get {
              path   = "/"
              port   = 3000
              scheme = "HTTP"
            }

            period_seconds    = 10
            failure_threshold = 3
            success_threshold = 1
            timeout_seconds   = 2
          }

          liveness_probe {
            http_get {
              path   = "/"
              port   = 3000
              scheme = "HTTP"
            }

            period_seconds    = 15
            failure_threshold = 3
            timeout_seconds   = 2
          }

          resources {
            requests = {
              cpu               = "100m"
              memory            = "192Mi"
              ephemeral-storage = "128Mi"
            }

            limits = {
              cpu               = "500m"
              memory            = "512Mi"
              ephemeral-storage = "256Mi"
            }
          }

          security_context {
            allow_privilege_escalation = false
            read_only_root_filesystem  = true
            run_as_non_root            = true
            run_as_user                = 10001
            run_as_group               = 10001

            capabilities {
              drop = ["ALL"]
            }
          }

          volume_mount {
            name       = "tmp"
            mount_path = "/tmp"
          }

          volume_mount {
            name       = "next-cache"
            mount_path = "/app/.next/cache"
          }
        }

        volume {
          name = "tmp"

          empty_dir {}
        }

        volume {
          name = "next-cache"

          empty_dir {}
        }
      }
    }
  }

  lifecycle {
    ignore_changes = [
      metadata[0].labels,
      wait_for_rollout,
      spec[0].template[0].metadata[0].annotations["kubectl.kubernetes.io/restartedAt"]
    ]
  }
}
