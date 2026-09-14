# Azure Monitoring and Observability Migration

## Current local architecture

The existing ApplicationBib environment uses a Kubernetes-based observability stack.

The main components are:

- Spring Boot Actuator / Micrometer
- Prometheus Server
- kube-state-metrics
- PostgreSQL Exporter
- Grafana
- Grafana dashboards
- Alertmanager
- Loki
- Grafana Alloy

The existing local monitoring stack remains untouched.

---

## Current metrics architecture

### Application metrics

The Spring Boot backend exposes:

```text
/actuator/prometheus
```

The application instrumentation is provided by Micrometer / Spring Boot Actuator.

```text
Spring Boot
    |
    v
Micrometer / Prometheus instrumentation
    |
    v
/actuator/prometheus
    |
    v
Prometheus Server
    |
    v
Grafana
```

The Prometheus instrumentation and Prometheus Server are separate components.

---

## Kubernetes metrics

The current environment uses kube-state-metrics.

```text
Kubernetes API
       |
       v
kube-state-metrics
       |
       v
Prometheus Server
       |
       v
Grafana
```

This provides Kubernetes object and workload state metrics such as:

- pod state
- deployment state
- replica state
- readiness
- restart information
- resource state

---

## PostgreSQL metrics

The current environment uses a PostgreSQL exporter.

```text
PostgreSQL
    |
    v
PostgreSQL Exporter
    |
    v
Prometheus Server
    |
    v
Grafana
```

The existing exporter remains part of the local architecture.

---

## Current logging architecture

Container logs are collected through Grafana Alloy.

```text
Kubernetes Containers
        |
        v
Grafana Alloy
        |
        v
Loki
        |
        v
Grafana
```

Loki is used for logs.

Prometheus is used for metrics.

These are separate telemetry paths.

---

# Azure target architecture

The Azure migration uses Azure-native monitoring where appropriate.

The main target components are:

- Azure Monitor
- Log Analytics Workspace
- Azure Monitor managed Prometheus
- Azure Monitor Workspace
- Container Insights
- Azure Monitor Alerts

The target AKS infrastructure enables Azure monitoring capabilities.

---

## Azure metrics architecture

The target architecture is:

```text
AKS Workloads
      |
      v
Azure Monitor managed Prometheus
      |
      v
Azure Monitor Workspace
      |
      v
Azure Monitor dashboards / queries
```

Application and Kubernetes metrics can therefore be collected without requiring the same standalone Prometheus Server architecture used locally.

---

## Application metrics migration

Current:

```text
Spring Boot
    |
    v
Micrometer
    |
    v
/actuator/prometheus
    |
    v
Prometheus Server
    |
    v
Grafana
```

Azure target:

```text
Spring Boot
    |
    v
Micrometer / Actuator
    |
    v
Prometheus-compatible metrics
    |
    v
Azure Monitor managed Prometheus
    |
    v
Azure Monitor Workspace
```

The application's instrumentation remains conceptually the same.

The main change is the metrics collection and storage platform.

---

## Kubernetes metrics migration

Current:

```text
Kubernetes API
      |
      v
kube-state-metrics
      |
      v
Prometheus Server
      |
      v
Grafana
```

Azure target:

```text
AKS
 |
 v
Azure Monitor
 |
 +--> Container Insights
 |
 +--> Managed Prometheus
 |
 v
Azure monitoring
```

Azure-native monitoring reduces the need to reproduce the complete local kube-prometheus-stack.

---

## Frontend monitoring

The frontend does not require a custom `/metrics` endpoint.

Its infrastructure monitoring can continue through Kubernetes/AKS telemetry.

Relevant metrics include:

- CPU
- memory
- pod state
- readiness
- container restarts
- workload state
- request/error-related metrics where available

The migration does not invent a frontend application metrics endpoint.

---

## PostgreSQL monitoring migration

Current:

```text
PostgreSQL
    |
    v
PostgreSQL Exporter
    |
    v
Prometheus
    |
    v
Grafana
```

Azure target:

```text
Azure Database for PostgreSQL
            |
            v
Azure Monitor
            |
            +--> Metrics
            |
            +--> Logs
            |
            v
Azure monitoring
```

The PostgreSQL exporter is therefore not required for the Azure managed PostgreSQL target when Azure-native database monitoring is used.

The existing Kubernetes PostgreSQL exporter remains untouched.

---

# Logging migration

## Current logging

```text
Kubernetes Containers
        |
        v
Grafana Alloy
        |
        v
Loki
        |
        v
Grafana
```

Alloy collects container logs.

Loki stores and indexes log data for querying.

Grafana provides visualization and querying.

---

## Azure target logging

The target Azure architecture uses Azure Monitor and Log Analytics.

```text
AKS Containers / Workloads
          |
          v
Container Insights
          |
          v
Log Analytics Workspace
          |
          v
Azure Monitor
```

The Azure infrastructure defines a Log Analytics Workspace for this purpose.

The existing Loki and Alloy components remain untouched in the local project.

They are not required to be reproduced in the Azure target when Azure-native logging is selected.

---

# Alerting migration

## Current local alerting

The existing monitoring architecture uses Alertmanager.

```text
Prometheus Server
       |
       v
Alertmanager
       |
       +----> Webhook
       |
       +----> Email
```

Prometheus generates alert conditions.

Alertmanager handles alert routing and notification delivery.

Prometheus does not directly send the notifications.

---

## Azure target alerting

Azure-native alerting replaces the Alertmanager architecture.

```text
Azure Monitor
      |
      v
Azure Monitor Alert Rule
      |
      v
Action Group
      |
      +----> Email
      |
      +----> Webhook
```

This provides the Azure equivalent of the notification path.

Alertmanager is therefore not required for the Azure-native monitoring design.

The existing Alertmanager remains untouched in the local project.

---

# Monitoring architecture comparison

| Capability | Local implementation | Azure target |
|---|---|---|
| Application metrics | Micrometer / Actuator | Micrometer / Actuator |
| Metrics collection | Prometheus Server | Managed Prometheus |
| Metrics storage | Prometheus | Azure Monitor Workspace |
| Kubernetes state | kube-state-metrics | Azure Monitor / AKS monitoring |
| PostgreSQL metrics | PostgreSQL Exporter | Azure Monitor PostgreSQL |
| Dashboards | Grafana | Azure monitoring / optional Grafana |
| Container logs | Grafana Alloy | Container Insights |
| Log storage | Loki | Log Analytics |
| Alert routing | Alertmanager | Azure Monitor Alerts / Action Groups |
| Email alerts | Alertmanager | Action Group |
| Webhook alerts | Alertmanager | Action Group |

---

# Grafana migration

The current project contains Grafana dashboards, including the seven-panel:

```text
ritual-growth-ui
```

customer dashboard.

The local Grafana deployment remains unchanged.

For Azure, there are two possible approaches:

### Azure-native

Use Azure Monitor dashboards and workbooks for Azure-native observability.

### Retained Grafana

Grafana can remain as a visualization layer if the final Azure deployment requires it.

This migration lab does not claim that a new Azure Grafana instance has been deployed.

---

# Terraform relationship

The current local monitoring stack is managed through the existing Terraform configuration.

That Terraform configuration remains untouched.

The Azure migration instead documents the Azure-native monitoring architecture.

```text
Current:

Terraform
    |
    v
Kubernetes monitoring stack
    |
    +--> Prometheus
    +--> Grafana
    +--> Loki
    +--> Alloy
    +--> Alertmanager
```

Azure target:

```text
Azure CLI / Azure infrastructure configuration
              |
              v
AKS monitoring integration
              |
              +--> Azure Monitor
              +--> Managed Prometheus
              +--> Log Analytics
              +--> Azure Monitor Alerts
```

No Azure Terraform implementation is introduced.

---

# Observability trust boundaries

The monitoring architecture crosses several boundaries.

```text
+-----------------------------+
| Application / AKS           |
|                             |
| Spring Boot                 |
| Frontend                    |
| Kubernetes workloads        |
+--------------+--------------+
               |
               | telemetry
               v
+-----------------------------+
| Azure Monitoring            |
|                             |
| Managed Prometheus          |
| Container Insights          |
| Azure Monitor               |
+--------------+--------------+
               |
               v
+-----------------------------+
| Monitoring Data             |
|                             |
| Metrics                     |
| Logs                        |
| Alerts                      |
+-----------------------------+
```

Monitoring data may contain operational information and application logs and should therefore be protected as an operationally sensitive asset.

---

# Security considerations

### Monitoring endpoints

Application metrics endpoints should not be unnecessarily exposed to the public Internet.

### Log sensitivity

Application logs must not contain credentials, Firebase service-account private keys, database passwords, or other secrets.

### Monitoring access

Access to Azure monitoring resources should follow least-privilege principles.

### Alert endpoints

Webhook URLs and notification configuration should be treated as sensitive configuration.

### Separation of telemetry

Metrics and logs remain conceptually separate:

```text
Metrics --> Managed Prometheus
Logs    --> Log Analytics
Alerts  --> Azure Monitor Alerts
```

---

# What changes

The main observability migration is:

```text
Prometheus Server
       ↓
Azure Monitor managed Prometheus
```

```text
Loki + Alloy
       ↓
Container Insights + Log Analytics
```

```text
Alertmanager
       ↓
Azure Monitor Alerts + Action Groups
```

```text
PostgreSQL Exporter
       ↓
Azure Database for PostgreSQL monitoring
```

---

# What does not change

The migration does not modify:

- Spring Boot application code
- Micrometer instrumentation
- existing Prometheus configuration
- existing Grafana dashboards
- existing Loki configuration
- existing Alloy configuration
- existing Alertmanager configuration
- existing Terraform
- existing Kubernetes manifests

All existing local observability components remain available for the original Minikube environment.

---

# Current status

- Azure Monitor architecture documented.
- Managed Prometheus architecture documented.
- Log Analytics architecture documented.
- Container Insights architecture documented.
- Azure Monitor Alerts architecture documented.
- Alertmanager-to-Azure-alerting migration documented.
- PostgreSQL monitoring migration documented.
- Frontend infrastructure monitoring migration documented.
- Existing Prometheus/Grafana/Loki/Alloy/Alertmanager stack remains untouched.
- No Azure monitoring resources have been deployed.
- No Azure metrics have been collected.
- No Azure alerts have been configured.
