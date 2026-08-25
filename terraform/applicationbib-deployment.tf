resource "kubernetes_deployment_v1" "applicationbib" {
  metadata {
    name      = "applicationbib"
    namespace = "default"

    labels = {
      app = "applicationbib"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "applicationbib"
      }
    }

    template {
      metadata {
        labels = {
          app = "applicationbib"
        }

        annotations = {
          "vault.hashicorp.com/agent-inject"                        = "true"
          "vault.hashicorp.com/agent-inject-secret-firebase.json"   = "secret/data/applicationbib/firebase"
          "vault.hashicorp.com/agent-inject-template-firebase.json" = <<-EOT
            {{- with secret "secret/data/applicationbib/firebase" -}}
            {{ index .Data.data "service-account.json" }}
            {{- end }}
          EOT
          "vault.hashicorp.com/role"                                = "applicationbib"
        }
      }

      spec {
        service_account_name            = "applicationbib-sa"
        automount_service_account_token = true
        enable_service_links            = false

        image_pull_secrets {
          name = "gitlab-registry-secret"
        }

        container {
          name              = "applicationbib"
          image             = "registry.gitlab.com/khalilmohamed798/applicationbib:f88e6244"
          image_pull_policy = "Always"

          env {
            name  = "SPRING_PROFILES_ACTIVE"
            value = "k8s"
          }

          env {
            name  = "FIREBASE_CREDENTIALS"
            value = "/vault/secrets/firebase.json"
          }

          port {
            container_port = 8080
            protocol       = "TCP"
          }

          readiness_probe {
            http_get {
              path   = "/actuator/health"
              port   = 8080
              scheme = "HTTP"
            }

            initial_delay_seconds = 15
            period_seconds        = 10
            failure_threshold     = 3
            success_threshold     = 1
            timeout_seconds       = 1
          }

          resources {
            requests = {
              cpu               = "250m"
              memory            = "384Mi"
              ephemeral-storage = "256Mi"
            }

            limits = {
              cpu               = "1"
              memory            = "768Mi"
              ephemeral-storage = "512Mi"
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
        }

        volume {
          name = "tmp"

          empty_dir {}
        }
      }
    }
  }

  depends_on = [
    kubernetes_namespace.applicationbib
  ]

  lifecycle {
    ignore_changes = [
      metadata[0].labels,
      wait_for_rollout,
      spec[0].template[0].metadata[0].annotations["kubectl.kubernetes.io/restartedAt"]
    ]
  }

}