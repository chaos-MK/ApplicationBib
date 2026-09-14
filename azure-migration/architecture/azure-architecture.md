# ApplicationBib — Azure Cloud Architecture

> **Status:** Azure migration design / implementation-ready configuration.  
> This Azure environment has **not been deployed** because the Azure subscription is currently disabled.  
> The existing ApplicationBib Minikube/Terraform implementation remains unchanged.

## 1. Objective

This directory represents the additional configuration and design required to understand how ApplicationBib could move from its existing cloud-native Kubernetes environment to Microsoft Azure.

The purpose is to learn and document:

- which existing components map to Azure services
- which Kubernetes configuration must change
- which security controls must be replaced or adapted
- how Azure networking and identity differ from the local environment
- how container images move from the existing registry to Azure Container Registry
- how secrets move from HashiCorp Vault to Azure Key Vault
- how monitoring responsibilities map to Azure-native services

This is an **additive migration exercise**. It does not replace or modify the existing project.

---

## 2. Architecture Boundaries

### Existing implementation — unchanged

```text
ApplicationBib/
├── terraform/       # Existing local infrastructure — DO NOT MODIFY
├── k8s/             # Existing local Kubernetes — DO NOT MODIFY
├── src/             # Application source — DO NOT MODIFY
└── ...
```

### Azure migration workspace

```text
ApplicationBib/
└── azure-migration/
    ├── architecture/
    ├── cli/
    ├── kubernetes/
    ├── scripts/
    └── docs/
```

Everything under `azure-migration/` exists specifically for the Azure migration exercise.

---

# 3. Target Azure Environment

## Target Region

- Region: **West Europe**
- Resource Group: `applicationbib-azure-rg`

## Target Architecture

```text
Internet
    │
    ▼
Azure networking / ingress
    │
    ▼
Azure Kubernetes Service (AKS)
    │
    ├── ApplicationBib frontend
    │
    ├── ApplicationBib backend
    │
    └── PostgreSQL
            │
            └── PostgreSQL metrics / monitoring
```

Supporting Azure services:

```text
Azure Container Registry
Azure Key Vault
Azure Monitor
Log Analytics Workspace
Azure Entra ID / Azure RBAC
Azure VNet
Network Security Groups
AKS Workload Identity
```

---

# 4. Azure Networking

```text
Azure VNet
└── AKS subnet
    └── AKS networking
```

Network security is provided through:

```text
Azure VNet
    │
    ├── Subnets
    │
    └── Network Security Groups
```

NSGs control traffic at the Azure network layer.

Kubernetes `NetworkPolicy` remains relevant inside AKS for pod-to-pod traffic.

Therefore the migration has **two networking/security layers**:

```text
Azure network layer
    ↓
VNet / subnet / NSG
    ↓
AKS
    ↓
Kubernetes network layer
    ↓
NetworkPolicy
    ↓
Pods / Services
```

---

# 5. Identity and Secrets

The existing local architecture uses:

```text
ApplicationBib
    ↓
Vault Agent Injector
    ↓
HashiCorp Vault
```

The Azure migration replaces this with:

```text
AKS Workload Identity
        ↓
Microsoft Entra ID
        ↓
Azure RBAC
        ↓
Azure Key Vault
        ↓
Key Vault CSI Driver
        ↓
Application / PostgreSQL / monitoring workloads
```

The migration therefore replaces the local Vault-based secret-injection mechanism with Azure-native identity and secret management.

No secret values are stored in the migration repository.

---

# 6. Container Images

Existing local architecture:

```text
GitLab Container Registry
        ↓
Kubernetes
        ↓
ApplicationBib
```

Azure target:

```text
Container Image
        ↓
Azure Container Registry
        ↓
AKS
        ↓
ApplicationBib
```

AKS is configured to authenticate to ACR using Azure identity/RBAC rather than storing registry credentials directly in application manifests.

---

# 7. Storage

The existing local environment uses Kubernetes storage/PVCs.

The Azure migration evaluates the corresponding Azure-managed storage model.

```text
Local Kubernetes
    ↓
PersistentVolume / PVC
```

becomes conceptually:

```text
AKS
    ↓
Azure-managed storage
    ↓
Persistent workload data
```

The exact Azure storage implementation depends on the workload requirements.

---

# 8. Monitoring and Observability

The existing local environment contains:

```text
kube-prometheus-stack
├── Prometheus
├── Grafana
└── Alertmanager

Loki
Grafana Alloy
```

The Azure migration evaluates Azure-native observability rather than automatically reproducing the entire local monitoring stack.

Primary Azure targets:

```text
AKS
 │
 ├── Azure Monitor
 │
 └── Log Analytics Workspace
```

For Kubernetes/container observability:

```text
AKS
    ↓
Azure Monitor / Container Insights
    ↓
Log Analytics
```

Prometheus/Grafana/Loki/Alloy should only be reproduced in Azure where there is a specific technical requirement to retain them.

The objective is to understand the difference between:

```text
Cloud-native self-managed observability
```

and:

```text
Azure-managed observability
```

rather than deploying duplicate monitoring systems unnecessarily.

---

# 9. Identity Migration

Existing cloud-native identity:

```text
Kubernetes ServiceAccount
        ↓
Vault Kubernetes authentication
        ↓
Vault role
        ↓
Vault secret
```

Azure target:

```text
Kubernetes ServiceAccount
        ↓
AKS Workload Identity
        ↓
Microsoft Entra ID
        ↓
User-assigned Managed Identity
        ↓
Azure RBAC
        ↓
Azure service
```

This is particularly important for the ApplicationBib backend and other workloads requiring access to Azure services.

---

# 10. Ingress and External Access

Existing local environment:

```text
Internet
    ↓
Cloudflare Quick Tunnel
    ↓
NGINX Ingress
    ↓
Kubernetes Service
    ↓
Application Pod
```

Azure migration target:

```text
Internet
    ↓
Azure networking / public endpoint
    ↓
AKS ingress / LoadBalancer
    ↓
Kubernetes Service
    ↓
Application Pod
```

The exact ingress implementation is intentionally left as a migration decision rather than assuming that the local Cloudflare/Minikube exposure model should be copied directly.

Cloudflare remains part of the existing local architecture and is **not modified by this migration exercise**.

---

# 11. Infrastructure Provisioning

The existing local infrastructure is managed with Terraform.

```text
Terraform
    ↓
Local Kubernetes / Minikube
```

For this migration exercise, Azure infrastructure is designed around **Azure CLI**.

```text
Azure CLI
    ↓
Azure Resource Group
    ↓
VNet / NSG
    ↓
AKS
    ↓
ACR
    ↓
Key Vault
    ↓
Log Analytics
    ↓
Managed identities / RBAC
```

Terraform is **not used to provision Azure infrastructure** in this migration workspace.

Ansible may optionally be used as an orchestration/configuration layer for AKS and Kubernetes workloads if it provides a meaningful automation benefit.

---

# 12. Cloud-Native → Azure Migration Mapping

| Existing cloud-native component | Azure target / migration approach |
|---|---|
| Minikube | Azure Kubernetes Service (AKS) |
| Local Kubernetes networking | Azure VNet + subnet + NSG |
| Kubernetes NetworkPolicy | Retained inside AKS |
| GitLab Container Registry | Azure Container Registry |
| Vault | Azure Key Vault |
| Vault Agent Injector | Workload Identity + Key Vault CSI |
| Vault Kubernetes authentication | AKS Workload Identity + Entra ID |
| Kubernetes ServiceAccount | ServiceAccount + Azure Workload Identity |
| Local Kubernetes storage | Azure-managed persistent storage |
| kube-prometheus-stack | Azure Monitor / managed observability where appropriate |
| Prometheus | Azure Monitor / managed Prometheus where required |
| Grafana | Azure-native or retained Grafana where justified |
| Loki + Alloy | Azure Monitor / Container Insights / Log Analytics where appropriate |
| Alertmanager | Azure-native alerting or retained Alertmanager where justified |
| Local node | AKS node pool / Azure VM SKU |
| Minikube ingress | AKS ingress / LoadBalancer |
| Local network exposure | Azure networking/public endpoint |
| Local identity model | Microsoft Entra ID + Azure RBAC |
| Terraform local infrastructure | Azure CLI-based infrastructure design |

---

# 13. What Stays Unchanged

The migration does **not** redesign the application itself.

The following remain conceptually unchanged:

```text
Next.js / React
        ↓
Spring Boot
        ↓
PostgreSQL
```

Authentication remains:

```text
Frontend
    ↓
Firebase Authentication
    ↓
Firebase ID Token
    ↓
Spring Boot
    ↓
Firebase Admin SDK
```

The application security hardening also remains relevant:

- non-root containers
- dropped Linux capabilities
- `readOnlyRootFilesystem` where supported
- resource requests/limits
- readiness probes
- Kubernetes NetworkPolicies
- image scanning
- SBOM generation
- SAST
- dependency scanning
- secrets scanning
- DAST
- Kubernetes manifest validation

The Azure migration changes the **platform around the application**, not the application's core architecture.

---

# 14. Migration Principle

The migration should be understood as:

```text
Existing ApplicationBib
        │
        │  unchanged
        ▼
Cloud-native application
        │
        │
        ▼
Azure platform adaptation
```

Rather than:

```text
Existing ApplicationBib
        ↓
Rewrite application
        ↓
New project
```

The goal is to identify the minimum infrastructure, identity, networking, storage, registry, secrets, monitoring, and deployment changes required to run the existing workload on Azure.

---

# 15. Current Status

| Area | Status |
|---|---|
| Azure migration workspace | Prepared |
| Azure CLI configuration | Prepared |
| Azure infrastructure scripts | Prepared |
| AKS manifests | Prepared |
| Azure Key Vault integration design | Prepared |
| Workload Identity design | Prepared |
| ACR integration design | Prepared |
| Azure networking design | Prepared |
| Kubernetes NetworkPolicies | Prepared |
| Azure monitoring design | Under evaluation |
| Ansible | Optional / not yet required |
| Azure resources | **Not deployed** |
| Existing Minikube infrastructure | **Unchanged** |
| Existing Terraform | **Unchanged** |

> **Important:** The Azure subscription is currently disabled, so the Azure configuration in this workspace is an implementation-ready migration exercise rather than evidence of deployed Azure resources.