# Azure Networking Migration

## Current local architecture

The existing ApplicationBib deployment runs on Minikube.

The local networking model is based on the Kubernetes cluster network, Kubernetes Services, NGINX Ingress, and the Cloudflare Quick Tunnel.

```text
Internet / User
       |
       v
Cloudflare Quick Tunnel
       |
       v
NGINX Ingress Controller
       |
       +----------------------+
       |                      |
       v                      v
ApplicationBib Service    Frontend Service
       |                      |
       v                      v
Spring Boot               Ritual Growth
       |
       v
PostgreSQL
```

The Cloudflare Quick Tunnel provides external access to the local Minikube ingress.

---

## Azure target architecture

The Azure migration replaces the Minikube networking environment with Azure Virtual Network networking and AKS.

```text
Internet
    |
    v
Azure public endpoint / Load Balancer
    |
    v
NGINX Ingress Controller
    |
    +--------------------------+
    |                          |
    v                          v
ApplicationBib Service    Ritual Growth Service
    |                          |
    v                          v
Spring Boot                Next.js / React
    |
    v
Azure PostgreSQL
```

The Azure networking infrastructure is isolated inside an Azure Virtual Network.

---

## Azure Virtual Network

The migration defines:

```text
VNet:
10.20.0.0/16
```

The planned AKS subnet is:

```text
AKS subnet:
10.20.0.0/22
```

Conceptually:

```text
+------------------------------------------------+
| Azure VNet                                     |
| 10.20.0.0/16                                   |
|                                                |
|  +------------------------------------------+  |
|  | AKS Subnet                               |  |
|  | 10.20.0.0/22                             |  |
|  |                                          |  |
|  |  AKS Nodes                               |  |
|  |   |                                      |  |
|  |   +-- ApplicationBib                     |  |
|  |   +-- Ritual Growth                      |  |
|  |   +-- NGINX Ingress                      |  |
|  |   +-- Monitoring                         |  |
|  |   +-- Other AKS workloads                |  |
|  +------------------------------------------+  |
+------------------------------------------------+
```

This replaces the networking environment provided by Minikube.

---

## AKS networking

The target Kubernetes platform is Azure Kubernetes Service (AKS).

The planned AKS networking configuration uses:

```text
Azure CNI
+
Overlay networking
```

The configured Kubernetes service CIDR is:

```text
10.30.0.0/16
```

The configured pod CIDR is:

```text
10.244.0.0/16
```

The Kubernetes DNS service IP is:

```text
10.30.0.10
```

These values are migration-target configuration values and have not been deployed.

---

## Network security group

The Azure migration includes a Network Security Group (NSG) associated with the AKS networking environment.

Conceptually:

```text
Internet
    |
    v
Azure networking
    |
    v
NSG
    |
    v
AKS subnet
    |
    v
AKS workloads
```

The NSG provides Azure-level network filtering.

Kubernetes NetworkPolicies remain responsible for Kubernetes workload-level traffic restrictions.

---

## Kubernetes NetworkPolicies

The migration retains NetworkPolicies from the existing Kubernetes architecture.

### ApplicationBib

The backend allows:

```text
ApplicationBib
    |
    +--> PostgreSQL :5432
    |
    +--> DNS :53
    |
    +--> HTTPS :443
```

### PostgreSQL

PostgreSQL accepts:

```text
ApplicationBib
       |
       | TCP 5432
       v
PostgreSQL
```

Monitoring access to the PostgreSQL exporter is allowed from the monitoring namespace.

The existing local NetworkPolicies remain untouched.

The Azure manifests are migration-only versions.

---

## Ingress

NGINX Ingress remains the Kubernetes ingress layer.

The target traffic flow is:

```text
Internet
    |
    v
Azure public endpoint / Load Balancer
    |
    v
NGINX Ingress Controller
    |
    +-----------------------------+
    |                             |
    | /api/*                      | /*
    v                             v
ApplicationBib                 Ritual Growth
Service :8080                  Service :3000
```

This preserves the same-origin frontend/API architecture.

---

## Same-origin API design

The frontend uses:

```text
NEXT_PUBLIC_API_BASE_URL=/api
```

Therefore the target public application endpoint is conceptually:

```text
https://<AZURE_APPLICATION_HOST>/
```

Frontend:

```text
https://<AZURE_APPLICATION_HOST>/
```

Backend API:

```text
https://<AZURE_APPLICATION_HOST>/api/*
```

NGINX performs the routing.

This avoids requiring a separate public API hostname and reduces CORS complexity.

`<AZURE_APPLICATION_HOST>` is a placeholder. No production domain is assumed.

---

## TLS

The target ingress uses HTTPS.

The ingress configuration references:

```text
applicationbib-tls
```

as the Kubernetes TLS Secret.

The Azure migration does not claim that a production certificate has been issued.

Certificate provisioning is a deployment-time requirement.

---

## Cloudflare migration

The current local architecture uses:

```text
Cloudflare Quick Tunnel
```

This is required because the local Minikube cluster is not directly exposed to the public Internet.

The Azure environment does not require the local Cloudflare Quick Tunnel to expose AKS.

The conceptual replacement is:

```text
Current:

Internet
   |
   v
Cloudflare Quick Tunnel
   |
   v
Minikube NGINX
```

Azure:

```text
Internet
   |
   v
Azure public endpoint / Load Balancer
   |
   v
AKS NGINX Ingress
```

Cloudflare is therefore not an authentication mechanism.

Firebase Authentication remains responsible for application authentication.

---

## NodePort migration

The local environment uses Kubernetes NodePort exposure.

The Azure target does not need to expose the application through the Minikube NodePort model.

Instead:

```text
Azure public networking
        |
        v
Ingress entry point
        |
        v
NGINX Ingress
        |
        v
ClusterIP Services
```

Application services remain internal Kubernetes Services.

---

## Service model

### ApplicationBib

```text
Service:
applicationbib

Type:
ClusterIP

Port:
8080
```

### Ritual Growth

```text
Service:
ritual-growth-ui

Type:
ClusterIP

Port:
3000
```

These services are not directly exposed to the Internet.

NGINX provides the routing layer.

---

## Database networking

The target application-to-database flow is:

```text
Spring Boot
     |
     | PostgreSQL :5432
     v
Azure Database for PostgreSQL
```

The database should not be publicly exposed.

The Azure database networking design should use private connectivity where supported by the final Azure deployment configuration.

The exact private DNS and delegated-subnet configuration is intentionally left as a future deployment step rather than being invented here.

---

## Monitoring networking

Monitoring remains internal to the AKS environment.

Conceptually:

```text
Application
    |
    | metrics
    v
Azure Monitor / Managed Prometheus

Kubernetes
    |
    | telemetry
    v
Azure Monitor

PostgreSQL
    |
    | monitoring telemetry
    v
Azure Monitor
```

The migration does not expose Prometheus metrics endpoints publicly.

---

## Network trust boundaries

The main networking trust boundaries are:

```text
+----------------------+
| Internet / User      |
+----------+-----------+
           |
           | HTTPS
           v
+----------------------+
| Azure Public Edge    |
+----------+-----------+
           |
           v
+----------------------+
| AKS / NGINX Ingress  |
+----------+-----------+
           |
           v
+----------------------+
| Application Services |
+----------+-----------+
           |
           v
+----------------------+
| Application Pods     |
+----------+-----------+
           |
           | PostgreSQL
           v
+----------------------+
| Azure PostgreSQL     |
+----------------------+
```

The Azure VNet and NSG provide infrastructure-level network boundaries.

Kubernetes NetworkPolicies provide workload-level network restrictions.

---

## Migration mapping

| Current local architecture | Azure target |
|---|---|
| Minikube network | Azure VNet + AKS networking |
| Minikube node | AKS nodes |
| NodePort exposure | Azure ingress entry point |
| NGINX Ingress | NGINX Ingress on AKS |
| Kubernetes ClusterIP | Kubernetes ClusterIP |
| Local cluster network | Azure VNet |
| Local network filtering | NSG + NetworkPolicy |
| Cloudflare Quick Tunnel | Azure public endpoint / Load Balancer |
| PostgreSQL pod | Azure Database for PostgreSQL |
| Local database networking | Azure private database networking |
| Local DNS | Azure/Kubernetes DNS |
| Local external access | Azure networking |

---

## Security considerations

### Public exposure

Only the ingress entry point should be externally reachable.

Backend and frontend ClusterIP Services should remain internal.

### Application authentication

Network access does not replace application authentication.

The backend continues to validate Firebase ID tokens.

### Network segmentation

Azure VNet, NSGs, Kubernetes Services, and NetworkPolicies provide multiple layers of network isolation.

### Database protection

PostgreSQL should not be directly exposed to the Internet.

### Monitoring protection

Monitoring endpoints and telemetry systems should remain restricted to their required network paths.

---

## Current status

- Azure VNet configuration is documented.
- AKS networking configuration is documented.
- AKS subnet is defined.
- Kubernetes Service CIDR is defined.
- Kubernetes Pod CIDR is defined.
- NSG is part of the migration infrastructure.
- NGINX Ingress remains the target Kubernetes ingress layer.
- Same-origin `/api` routing is documented.
- Cloudflare Quick Tunnel is identified as a local-environment component.
- Azure public networking replaces the need for the local Quick Tunnel.
- Existing local networking remains untouched.
- No Azure networking resources have been deployed.
- No public Azure endpoint exists yet.
```

**Next:** we should handle the **monitoring migration** — specifically what happens to your current Prometheus + Grafana + Loki + Alloy + Alertmanager stack when moving to Azure Monitor / Managed Prometheus.
