```markdown
# ApplicationBib / Ritual Growth — Azure Migration Lab

This directory contains the **Azure migration study and deployment-preparation artifacts** for the ApplicationBib / Ritual Growth DevSecOps project.

The purpose is to understand what would need to change when migrating the existing local Kubernetes platform to Microsoft Azure.

> **Important:** This is a migration lab. The Azure environment has not been deployed. The original project remains unchanged.

---

## 1. Objective

The original project runs locally using:

- Minikube
- Kubernetes
- NGINX Ingress
- Cloudflare Quick Tunnel
- Spring Boot
- Next.js / React
- PostgreSQL
- HashiCorp Vault
- Vault Agent Injector
- Prometheus
- Grafana
- Loki
- Grafana Alloy
- Alertmanager
- Terraform
- GitLab CI/CD
- GitLab Container Registry
- Podman / Buildah / Skopeo

The migration study maps these components to Azure equivalents where appropriate.

---

## 2. Azure Target

The target architecture uses:

- Azure Kubernetes Service (AKS)
- Azure Container Registry (ACR)
- Azure Virtual Network
- Network Security Groups
- Azure Key Vault
- Microsoft Entra ID
- AKS Workload Identity
- Managed identities
- Azure Database for PostgreSQL Flexible Server
- Azure Monitor
- Azure Monitor Workspace
- Managed Prometheus
- Log Analytics Workspace
- Container Insights
- Azure Monitor Alerts / Action Groups
- NGINX Ingress

---

## 3. Important: This Does Not Modify the Original Project

The original project remains untouched.

The Azure migration artifacts are isolated under:

```text
azure-migration/
```

The following existing project areas are not modified by this migration lab:

```text
terraform/
k8s/
src/
Dockerfile
.gitlab-ci.yml
```

The separate Ritual Growth frontend repository is also not modified.

---

## 4. Current Status

### Local Application

Implemented and operational locally:

- [x] Spring Boot backend
- [x] Next.js / React frontend
- [x] PostgreSQL
- [x] Kubernetes / Minikube
- [x] NGINX Ingress
- [x] Cloudflare Quick Tunnel
- [x] HashiCorp Vault
- [x] Vault Agent Injector
- [x] Prometheus
- [x] Grafana
- [x] Loki
- [x] Grafana Alloy
- [x] Alertmanager
- [x] Terraform-managed infrastructure
- [x] GitLab CI/CD
- [x] Security scanning
- [x] Container hardening
- [x] Kubernetes security validation
- [x] OWASP ZAP

### Azure Migration

Prepared as a learning/migration exercise:

- [x] Azure architecture documentation
- [x] Azure networking design
- [x] AKS design
- [x] ACR design
- [x] Key Vault design
- [x] Workload Identity design
- [x] Managed identity design
- [x] PostgreSQL migration design
- [x] Azure monitoring design
- [x] Azure ingress design
- [x] Azure Kubernetes manifests
- [x] Azure CLI preparation scripts
- [x] Frontend migration documentation

### Azure Deployment

- [ ] Azure AKS deployed
- [ ] Azure ACR deployed
- [ ] Azure Key Vault deployed
- [ ] Azure PostgreSQL deployed
- [ ] Azure monitoring deployed
- [ ] Application deployed to AKS
- [ ] Production secrets migrated
- [ ] Production database migrated

These remain intentionally undeployed.

---

# 5. Why Azure Is Not Deployed

The Azure subscription currently available for the learning environment is disabled/read-only for resource creation.

Therefore this repository does not claim that Azure resources have been provisioned.

The migration artifacts are instead used to demonstrate:

1. architecture analysis
2. cloud-service mapping
3. identity migration
4. secret-management migration
5. networking migration
6. database migration
7. monitoring migration
8. Kubernetes migration
9. container-registry migration
10. security considerations

---

# 6. Directory Structure

```text
azure-migration/
│
├── architecture/
│   ├── azure-architecture.md
│   ├── azure-migration-overview.md
│   │
│   ├── frontend/
│   │   └── azure-frontend-migration.md
│   │
│   ├── ingress.md
│   ├── registry.md
│   ├── database.md
│   ├── secrets.md
│   ├── networking.md
│   ├── monitoring.md
│   └── identity.md
│
├── cli/
│   ├── azure.env
│   ├── azure-infra.sh
│   └── keyvault-secrets.sh
│
├── kubernetes/
│   ├── app/
│   ├── monitoring/
│   ├── namespace/
│   ├── networking/
│   └── secrets/
│
├── scripts/
│
└── docs/
```

---

# 7. Architecture Documentation

## Azure Migration Overview

`architecture/azure-migration-overview.md`

Contains the complete target architecture and explains:

- runtime traffic
- Firebase authentication
- Key Vault
- Workload Identity
- PostgreSQL
- ACR
- AKS
- monitoring
- logging
- alerting
- CI/CD
- trust boundaries
- migration sequence

---

## Frontend Migration

`architecture/frontend/azure-frontend-migration.md`

Documents:

- Next.js deployment
- frontend Service
- same-origin `/api`
- Firebase Web SDK
- build-time Firebase configuration
- Azure frontend exposure

The frontend repository itself is not modified.

---

## Ingress

`architecture/ingress.md`

Documents:

```text
Internet
   |
   v
Azure public endpoint
   |
   v
NGINX Ingress
   |
   +---- / ------> Frontend
   |
   +---- /api ---> Backend
```

---

## Container Registry

`architecture/registry.md`

Maps:

```text
GitLab Container Registry
        |
        v
Azure Container Registry
```

Target images:

```text
applicationbibacr.azurecr.io/applicationbib:<tag>

applicationbibacr.azurecr.io/ritual-growth-ui:<tag>
```

---

## Database

`architecture/database.md`

Maps:

```text
PostgreSQL StatefulSet
        |
        v
Azure Database for PostgreSQL Flexible Server
```

The managed PostgreSQL service is the intended Azure production target.

---

## Secrets

`architecture/secrets.md`

Maps:

```text
HashiCorp Vault
      |
      v
Vault Agent Injector
```

to:

```text
Azure Key Vault
      |
      v
Secrets Store CSI Driver
      |
      v
AKS Workload Identity
```

The Firebase Web configuration remains a build-time CI/CD configuration and is not moved to Key Vault.

---

## Networking

`architecture/networking.md`

Documents the target:

```text
Azure VNet
10.20.0.0/16
```

with:

```text
AKS subnet
10.20.0.0/22
```

and the planned Azure networking model.

---

## Monitoring

`architecture/monitoring.md`

Maps the local monitoring stack to Azure-native monitoring:

```text
Prometheus
      |
      v
Managed Prometheus
```

and:

```text
Loki / Alloy
      |
      v
Container Insights
      |
      v
Log Analytics
```

---

## Identity

`architecture/identity.md`

Documents:

- Entra ID
- AKS managed identity
- Workload Identity
- federated credentials
- Azure RBAC
- ACR access
- Key Vault access

---

# 8. Kubernetes Migration Artifacts

The Kubernetes directory contains **Azure-target examples**.

These files are not automatically deployed.

They exist to demonstrate the Kubernetes changes required for the migration.

---

## Application

```text
kubernetes/app/
```

Contains target manifests for:

- frontend Deployment
- frontend Service
- backend Service
- PostgreSQL StatefulSet/PVC examples

The PostgreSQL Kubernetes manifests are included as migration-learning artifacts.

For a real Azure deployment using Azure Database for PostgreSQL Flexible Server, the PostgreSQL StatefulSet would not be deployed.

---

## Networking

```text
kubernetes/networking/
```

Contains:

- ApplicationBib Ingress
- backend NetworkPolicy
- PostgreSQL NetworkPolicy

The target ingress uses:

```text
<AZURE_APPLICATION_HOST>
```

as a placeholder.

No real production domain is assumed.

---

## Secrets

```text
kubernetes/secrets/
```

Contains:

- ApplicationBib ServiceAccount
- PostgreSQL ServiceAccount
- ApplicationBib SecretProviderClass
- PostgreSQL SecretProviderClass

These demonstrate Azure Workload Identity + Key Vault integration.

---

# 9. Frontend Architecture

The frontend remains:

```text
Next.js
+
React
+
Firebase Web SDK
```

The Azure target uses:

```text
GitLab CI/CD
      |
      v
Buildah
      |
      v
Dockerfile ARG
      |
      v
Dockerfile ENV
      |
      v
npm run build
      |
      v
Next.js image
```

Firebase Web configuration is therefore still supplied during the image build.

There is intentionally no:

```text
Azure Key Vault
      |
      v
Frontend Firebase configuration
```

dependency.

---

# 10. Authentication Architecture

The application continues to use Firebase Authentication.

Runtime:

```text
User
 |
 v
Next.js / React
 |
 v
Firebase Authentication
 |
 v
Firebase ID Token
 |
 v
Backend
 |
 v
Firebase Admin SDK
 |
 v
Token verification
```

The backend then accesses PostgreSQL.

Azure does not replace Firebase as the application's authentication provider.

---

# 11. Secret Architecture

The backend's sensitive Firebase Admin credential moves conceptually from:

```text
Vault
   |
   v
Vault Agent Injector
   |
   v
Backend Pod
```

to:

```text
Azure Key Vault
   |
   v
Secrets Store CSI Driver
   |
   v
Backend Pod
```

Access is authorized through:

```text
AKS Workload Identity
        |
        v
ApplicationBib Managed Identity
        |
        v
Key Vault
```

---

# 12. Database Architecture

Current:

```text
Spring Boot
     |
     v
PostgreSQL StatefulSet
     |
     v
PVC
```

Azure target:

```text
Spring Boot
     |
     v
Azure Database for PostgreSQL
Flexible Server
```

The managed database is preferred because Azure takes responsibility for infrastructure-level database operations such as managed availability and backups according to the selected service configuration.

Those capabilities have not been deployed or configured as part of this lab.

---

# 13. Monitoring Architecture

The application exposes:

```text
/actuator/prometheus
```

The current local architecture uses Prometheus.

The Azure target uses Managed Prometheus.

The distinction remains:

```text
Application
    |
    v
Prometheus instrumentation
    |
    v
Metrics endpoint
    |
    v
Managed Prometheus
```

Logs use a separate pipeline:

```text
Kubernetes containers
       |
       v
Container Insights
       |
       v
Log Analytics
```

Metrics and logs are therefore not treated as the same telemetry type.

---

# 14. CI/CD Security

The existing security pipeline remains part of the project.

Important controls include:

```text
Gitleaks
Semgrep
SonarQube
Snyk
JUnit / Mockito
Hadolint
Syft
Trivy
kubeconform
kube-score
OWASP ZAP
```

The Azure migration does not remove these controls.

The target deployment path becomes:

```text
GitLab
   |
   v
Security Gates
   |
   v
Build
   |
   v
Container Image
   |
   v
ACR
   |
   v
AKS
```

---

# 15. Azure Identity Model

The target identity model avoids long-lived credentials where possible.

### AKS

AKS uses a managed identity for Azure resource operations.

### ApplicationBib

```text
applicationbib-azure-sa
        |
        v
AKS Workload Identity
        |
        v
ApplicationBib Managed Identity
        |
        v
Key Vault
```

### PostgreSQL workload

A separate identity is planned for the PostgreSQL workload where required by the migration design.

This keeps application and database permissions separated.

---

# 16. Security Principles

The migration follows these principles:

### Least privilege

Each workload should receive only the Azure permissions it requires.

### No secrets in Git

Sensitive credentials must not be committed to repositories.

### Workload identity

Kubernetes workloads should use federated identity instead of long-lived Azure client secrets.

### Private database

PostgreSQL should not be publicly exposed.

### Container hardening

Existing non-root and capability restrictions should be preserved.

### Network segmentation

Azure networking and Kubernetes NetworkPolicies should limit unnecessary communication.

### Immutable images

Production deployments should use immutable image tags rather than relying on `latest`.

### Security gates

Existing CI security controls should remain part of the deployment lifecycle.

---

# 17. What Is Not Being Migrated Directly

Some local components are intentionally replaced by Azure-managed capabilities.

### Cloudflare Quick Tunnel

Used locally for temporary external access.

Azure target:

```text
Azure public endpoint / Load Balancer
```

### HashiCorp Vault

Used locally for application secrets.

Azure target:

```text
Azure Key Vault
```

### PostgreSQL StatefulSet

Used locally.

Azure target:

```text
Azure Database for PostgreSQL Flexible Server
```

### Loki / Alloy

Used locally.

Azure target:

```text
Container Insights
+
Log Analytics
```

### Prometheus / Alertmanager

Used locally.

Azure target:

```text
Managed Prometheus
+
Azure Monitor Alerts
+
Action Groups
```

---

# 18. What Remains the Same

The migration does not fundamentally change the application architecture.

The following remain conceptually unchanged:

- Next.js / React frontend
- Spring Boot backend
- Firebase Authentication
- Firebase Web SDK
- Firebase Admin SDK
- REST API
- `/api` backend routing
- PostgreSQL data model
- containerized application workloads
- Kubernetes deployment model
- NGINX Ingress
- GitLab CI/CD
- security scanning
- container hardening
- Kubernetes security validation

The main change is the infrastructure platform surrounding the application.

---

# 19. Migration Flow

A real migration would follow approximately:

```text
1. Azure Foundation
       |
       v
2. VNet / Networking
       |
       v
3. AKS + ACR
       |
       v
4. Identity / Workload Identity
       |
       v
5. Key Vault
       |
       v
6. Managed PostgreSQL
       |
       v
7. NGINX / Ingress
       |
       v
8. Monitoring
       |
       v
9. Container Images
       |
       v
10. Application Deployment
       |
       v
11. Security Validation
       |
       v
12. Production Validation
```

---

# 20. Validation

Before considering the migration successful, validate:

### Application

- frontend availability
- backend availability
- Firebase registration
- Firebase login
- Firebase token verification
- API functionality
- database connectivity

### Infrastructure

- AKS health
- ACR image pulls
- Key Vault access
- Workload Identity
- ingress routing
- TLS
- network policies

### Security

- secrets are not committed
- containers remain non-root
- image scans pass
- Kubernetes validation passes
- authentication controls work
- OWASP ZAP results are acceptable

### Observability

- application metrics
- Kubernetes metrics
- database monitoring
- application logs
- alerting
- notification delivery

---

# 21. Important Accuracy Rules

This migration documentation does **not** claim:

- that Azure resources currently exist
- that the application is deployed to AKS
- that ACR contains the application images
- that Key Vault contains production secrets
- that PostgreSQL data has been migrated
- that Azure monitoring is active
- that a production domain exists
- that Cloudflare is used in the Azure target
- that Firebase Authentication is replaced by Entra ID
- that the frontend receives Firebase configuration from Key Vault

In particular:

```text
Vault
  X
  |
  X----> Frontend Firebase configuration
```

is intentionally not part of the architecture.

---

# 22. Relationship to the Original Project

This migration lab should be viewed as:

```text
Existing ApplicationBib
        |
        | architecture analysis
        v
Azure migration design
        |
        v
Azure-specific preparation
```

It is not:

```text
Existing ApplicationBib
        |
        v
Modified ApplicationBib
```

The original project remains the source implementation.

The `azure-migration/` directory documents the changes that would be required to deploy the same application on Azure.

---

# 23. Final Summary

The project demonstrates two environments conceptually.

### Current DevSecOps platform

```text
Minikube
+
Kubernetes
+
Vault
+
Prometheus
+
Grafana
+
Loki
+
NGINX
+
Cloudflare
+
GitLab
```

### Azure migration target

```text
AKS
+
Azure VNet
+
ACR
+
Key Vault
+
Entra ID / Workload Identity
+
Azure Database for PostgreSQL
+
Azure Monitor
+
Log Analytics
+
NGINX
+
GitLab
```

The application architecture remains largely the same while infrastructure services are replaced with Azure-managed equivalents.

This separation demonstrates the ability to reason about:

- cloud migration
- Kubernetes
- identity
- secret management
- networking
- managed databases
- container registries
- observability
- CI/CD
- DevSecOps security controls

without modifying the original production-oriented project.
```

After this, the next logical step is **not another architecture document**. We should do a **migration-artifact audit**: check every file under `azure-migration/` for inconsistencies, placeholders, accidental Azure assumptions, and contradictions between the docs and manifests.