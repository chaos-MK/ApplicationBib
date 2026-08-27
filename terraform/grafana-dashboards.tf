resource "kubernetes_config_map" "podman_dashboard" {
  metadata {
    name      = "podman-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "podman.json" = file("${path.module}/../k8s/monitoring/dashboards/podman.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "applicationbib_dashboard" {
  metadata {
    name      = "applicationbib-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "applicationbib.json" = file("${path.module}/../k8s/monitoring/dashboards/applicationbib.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "loki_dashboard" {
  metadata {
    name      = "loki-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "loki.json" = file("${path.module}/../k8s/monitoring/dashboards/loki.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "postgresql_dashboard" {
  metadata {
    name      = "postgresql-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "postgresql.json" = file("${path.module}/../k8s/monitoring/dashboards/postgresql.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "ritual_growth_ui_dashboard" {
  metadata {
    name      = "ritual-growth-ui-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "ritual-growth-ui.json" = file("${path.module}/../k8s/monitoring/dashboards/ritual-growth-ui-dashboard.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}

resource "kubernetes_config_map" "vault_dashboard" {
  metadata {
    name      = "vault-dashboard"
    namespace = "monitoring"

    labels = {
      grafana_dashboard = "1"
    }
  }

  data = {
    "vault.json" = file("${path.module}/../k8s/monitoring/dashboards/vault.json")
  }

  depends_on = [
    helm_release.kube_prometheus_stack
  ]
}
