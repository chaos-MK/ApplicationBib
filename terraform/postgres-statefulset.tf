resource "kubernetes_stateful_set" "postgres" {
  metadata {
    name      = "postgres"
    namespace = "default"
  }
  spec {
    service_name = "postgres"
    replicas     = 1
    selector {
      match_labels = {
        app = "postgres"
      }
    }
    template {
      metadata {
        labels = {
          app = "postgres"
        }
        annotations = {
          "vault.hashicorp.com/agent-inject"                       = "true"
          "vault.hashicorp.com/agent-inject-secret-db-creds"       = "secret/data/applicationbib/db"
          "vault.hashicorp.com/agent-inject-template-db-creds"     = <<-EOT
            {{- with secret "secret/data/applicationbib/db" -}}
            export POSTGRES_DB="{{ .Data.data.url | regexReplaceAll ".*\\/([^\\/]+)$" "$${1}" }}"
            export POSTGRES_USER="{{ .Data.data.username }}"
            export POSTGRES_PASSWORD="{{ .Data.data.password }}"
            {{- end -}}
          EOT
          "vault.hashicorp.com/agent-inject-secret-exporter-dsn"   = "secret/data/applicationbib/db"
          "vault.hashicorp.com/agent-inject-template-exporter-dsn" = <<-EOT
            {{- with secret "secret/data/applicationbib/db" -}}
            export DATA_SOURCE_NAME="postgresql://{{ .Data.data.username }}:{{ .Data.data.password }}@localhost:5432/{{ .Data.data.url | regexReplaceAll ".*\\/([^\\/]+)$" "$${1}" }}?sslmode=disable"
            {{- end -}}
          EOT
          "vault.hashicorp.com/role"                               = "postgres"
        }
      }
      spec {
        service_account_name            = "postgres-sa"
        automount_service_account_token = true
        enable_service_links            = false
        container {
          name    = "postgres"
          image   = "docker.io/library/postgres:16"
          command = ["/bin/sh", "-c"]
          args = [
            ". /vault/secrets/db-creds && exec docker-entrypoint.sh postgres"
          ]
          port {
            container_port = 5432
            protocol       = "TCP"
          }
          security_context {
            allow_privilege_escalation = false
            capabilities {
              drop = ["ALL"]
              add  = ["CHOWN", "FOWNER", "DAC_OVERRIDE", "SETUID", "SETGID"]
            }
          }
          volume_mount {
            name       = "postgres-storage"
            mount_path = "/var/lib/postgresql/data"
          }
        }
        container {
          name    = "postgres-exporter"
          image   = "quay.io/prometheuscommunity/postgres-exporter:v0.15.0"
          command = ["/bin/sh", "-c"]
          args = [
            ". /vault/secrets/exporter-dsn && exec postgres_exporter"
          ]
          port {
            container_port = 9187
            name           = "metrics"
          }
          security_context {
            allow_privilege_escalation = false
            capabilities {
              drop = ["ALL"]
            }
          }
        }
        volume {
          name = "postgres-storage"
          persistent_volume_claim {
            claim_name = "postgres-pvc"
          }
        }
      }
    }
  }
}
