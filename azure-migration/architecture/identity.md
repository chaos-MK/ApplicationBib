# Azure Identity and IAM Migration

## Purpose

The Azure migration replaces infrastructure authentication mechanisms used by the local Kubernetes environment with Azure identity-based access.

The target architecture uses:

- Microsoft Entra ID
- AKS managed identity
- AKS Workload Identity
- Federated Identity Credentials
- Azure RBAC
- Azure Key Vault permissions
- ACR pull authorization

No long-lived Azure credentials are stored in the application containers.

---

# Current local identity model

The existing project uses Kubernetes ServiceAccounts and HashiCorp Vault Kubernetes authentication.

```text
Kubernetes ServiceAccount
        |
        v
Vault Kubernetes Authentication
        |
        v
HashiCorp Vault
        |
        v
Vault Agent Injector
        |
        v
Application / PostgreSQL Pod
```

This local architecture remains untouched.

---

# Azure identity architecture

The Azure target uses Microsoft Entra ID and managed identities.

```text
                    Microsoft Entra ID
                           |
             +-------------+-------------+
             |                           |
             v                           v
      AKS Managed Identity       Workload Identities
             |                           |
             v                           |
            ACR                          |
                                         |
                              +----------+----------+
                              |                     |
                              v                     v
                       ApplicationBib Pod     PostgreSQL Pod
                              |                     |
                              v                     v
                         Key Vault             Key Vault
```

The exact permissions are separated by workload.

---

# AKS managed identity

The planned AKS cluster uses managed identity.

The identity is associated with the AKS control-plane/resource operations rather than being embedded as a static credential.

Conceptually:

```text
AKS
 |
 v
Azure Managed Identity
 |
 +--> Azure resources required by AKS
```

The migration infrastructure also attaches the ACR to AKS.

---

# ACR access

AKS requires permission to pull application images from Azure Container Registry.

The target relationship is:

```text
AKS
  |
  v
AKS Managed Identity
  |
  | AcrPull
  v
Azure Container Registry
```

This replaces the need for a Kubernetes registry username/password.

The target images are:

```text
applicationbibacr.azurecr.io/applicationbib:<tag>

applicationbibacr.azurecr.io/ritual-growth-ui:<tag>
```

---

# Application Workload Identity

ApplicationBib uses:

```text
applicationbib-azure-sa
```

The ServiceAccount is associated with the application managed identity.

```text
ApplicationBib Pod
       |
       v
applicationbib-azure-sa
       |
       v
AKS Workload Identity
       |
       v
Federated Identity Credential
       |
       v
Application Managed Identity
       |
       v
Azure Key Vault
```

The application identity is intended to access the secrets required by the backend.

---

# PostgreSQL Workload Identity

The target Azure architecture uses **Azure Database for PostgreSQL Flexible Server**, so the managed database does not require a Kubernetes ServiceAccount or Kubernetes Workload Identity.

The `postgres-azure-sa` ServiceAccount and separate PostgreSQL managed identity are retained only for the **optional self-hosted PostgreSQL StatefulSet reference manifests** under `azure-migration/kubernetes/app/` and `azure-migration/kubernetes/secrets/`.

Reference architecture:

PostgreSQL Pod
       |
       v
postgres-azure-sa
       |
       v
AKS Workload Identity
       |
       v
Federated Identity Credential
       |
       v
PostgreSQL Managed Identity
       |
       v
Azure Key Vault

This reference identity is not part of the target Azure Database for PostgreSQL Flexible Server architecture.

For the managed PostgreSQL target, the application uses its own Workload Identity to access the required Key Vault secrets, while the managed database is accessed through its private database endpoint.

# Federated Identity Credentials

AKS provides an OIDC issuer.

The federated identity relationship is:

```text
Kubernetes ServiceAccount
        |
        v
AKS OIDC issuer
        |
        v
Federated Identity Credential
        |
        v
Azure Managed Identity
```

The federation establishes trust between the Kubernetes workload identity and the Azure managed identity.

The configured audience is:

```text
api://AzureADTokenExchange
```

The ApplicationBib identity is associated with:

```text
system:serviceaccount:applicationbib:applicationbib-azure-sa
```

The PostgreSQL identity is associated with:

```text
system:serviceaccount:applicationbib:postgres-azure-sa
```

---

# Key Vault access

The application identity accesses Azure Key Vault.

Target:

```text
ApplicationBib Managed Identity
             |
             | authorized secret access
             v
       Azure Key Vault
```

Required application secret:

```text
firebase-service-account
```

PostgreSQL identity accesses:

```text
postgres-url
postgres-username
postgres-password
```

The actual secret values are never represented in this architecture.

---

# Least privilege

Separate identities are used for different workloads.

```text
ApplicationBib Identity
        |
        +--> Application secrets

PostgreSQL Identity
        |
        +--> PostgreSQL secrets
```

The identities should not be granted unnecessary access to each other's secrets.

This limits the impact of a compromised workload.

---

# GitLab CI/CD identity

The Azure deployment pipeline also requires an Azure identity.

Conceptually:

```text
GitLab CI/CD
      |
      v
Azure authentication
      |
      v
Microsoft Entra ID
      |
      v
Azure RBAC
      |
      +--> ACR
      +--> AKS
      +--> Key Vault
```

The exact GitLab-to-Azure authentication mechanism is not currently implemented.

For a real Azure deployment, federated workload identity / OpenID Connect should be preferred over storing a long-lived Azure client secret.

No GitLab Azure credentials are stored in this migration repository.

---

# Terraform and Azure identity

The existing local Terraform configuration remains untouched.

The Azure migration does not introduce Azure Terraform.

The Azure infrastructure is currently described through Azure CLI and migration documentation.

Therefore:

```text
Local environment

Terraform
   |
   v
Minikube / local infrastructure
```

while:

```text
Azure migration

Azure CLI / Azure configuration
   |
   v
Azure resources
   |
   v
Microsoft Entra ID / managed identities
```

---

# Identity trust boundaries

The identity architecture crosses multiple trust boundaries.

```text
+-----------------------------+
| Kubernetes                  |
|                             |
| ApplicationBib Pod          |
| PostgreSQL Pod              |
| Kubernetes ServiceAccounts  |
+--------------+--------------+
               |
               | OIDC federation
               v
+-----------------------------+
| Microsoft Entra ID          |
|                             |
| Federated credentials       |
| Managed identities          |
+------+----------------------+
       |
       +----------------------+
       |                      |
       | RBAC                 | secret access
       v                      v
+--------------+       +------------------+
| ACR          |       | Azure Key Vault  |
| AcrPull      |       | Application      |
|              |       | PostgreSQL       |
|              |       | secrets          |
+--------------+       +------------------+
```

---

# Identity threat model

## Spoofing

A compromised Kubernetes ServiceAccount could attempt to obtain an Azure identity.

Mitigation:

- Workload Identity
- OIDC federation
- specific ServiceAccount subjects
- separate identities per workload

## Tampering

Unauthorized identities could attempt to modify Azure resources.

Mitigation:

- Azure RBAC
- least-privilege role assignments
- separate workload identities

## Information disclosure

A compromised application identity could attempt to read secrets.

Mitigation:

- Key Vault authorization
- separate identities
- least privilege
- no secrets embedded in images

## Elevation of privilege

A workload could attempt to use another workload's identity.

Mitigation:

- distinct ServiceAccounts
- federated identity subject restrictions
- separate managed identities
- minimal permissions

---

# Identity mapping

| Local mechanism | Azure mechanism |
|---|---|
| Kubernetes ServiceAccount | Kubernetes ServiceAccount |
| Vault Kubernetes Auth | AKS Workload Identity |
| Vault identity | Microsoft Entra ID |
| Vault role | Federated Identity Credential / Azure authorization |
| Vault secret access | Key Vault authorization |
| Registry credentials | AKS managed identity + AcrPull |
| Static cloud credentials | Federated identity / managed identity |
| Vault Agent authentication | Workload Identity |

---

# Important security rules

The Azure migration must not:

- store Azure client secrets in Git
- store ACR passwords in Kubernetes manifests
- embed Azure credentials into container images
- give ApplicationBib access to PostgreSQL secrets unnecessarily
- give PostgreSQL access to Firebase service-account credentials unnecessarily
- expose Key Vault secrets through application logs
- treat Firebase Web configuration as a backend service-account secret

The Firebase Admin service-account credential remains sensitive.

The Firebase Web configuration remains a separate frontend build-time configuration concern.

---

# What changes

The main identity migration is:

```text
Vault Kubernetes Authentication
            |
            v
AKS Workload Identity
```

and:

```text
Registry credentials
        |
        v
AKS Managed Identity + AcrPull
```

and:

```text
Vault secret authorization
        |
        v
Azure Key Vault authorization
```

---

# What does not change

The migration does not modify:

- Firebase Authentication
- Firebase Web SDK
- Firebase ID token flow
- Spring Security
- application authentication logic
- existing Vault configuration
- existing local Kubernetes ServiceAccounts
- existing Terraform
- existing GitLab CI/CD files

---

# Current status

- Microsoft Entra ID identity architecture documented.
- AKS managed identity documented.
- ACR pull identity documented.
- Application Workload Identity documented.
- PostgreSQL Workload Identity documented.
- Federated Identity Credentials documented.
- Key Vault access documented.
- GitLab CI/CD Azure authentication requirement documented.
- Least-privilege model documented.
- Existing local identity architecture remains untouched.
- No Azure identities have been deployed.
- No Azure RBAC assignments have been applied.
- No Azure resources have been created.