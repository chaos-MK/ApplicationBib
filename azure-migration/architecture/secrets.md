# Azure Secrets Management Migration

## Current architecture

The existing ApplicationBib Kubernetes deployment uses HashiCorp Vault for backend and PostgreSQL secrets.

The current model is:

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
ApplicationBib / PostgreSQL Pods
```

Vault remains part of the existing local project and is not modified by this Azure migration.

---

## Azure target architecture

The Azure migration replaces Vault-based secret injection with:

- Azure Key Vault
- Microsoft Entra ID
- AKS Workload Identity
- Azure Key Vault Secrets Store CSI Driver

Target flow:

```text
Azure Key Vault
       |
       | secret access
       v
Microsoft Entra ID
       |
       v
AKS Workload Identity
       |
       v
Kubernetes ServiceAccount
       |
       v
Secrets Store CSI Driver
       |
       v
Application Pod
```

The migration does not require storing long-lived Azure credentials inside Kubernetes.

---

## ApplicationBib Firebase credentials

The backend currently requires:

```text
FIREBASE_CREDENTIALS=/vault/secrets/firebase.json
```

In Azure, the Firebase service-account credential is stored in Key Vault as:

```text
firebase-service-account
```

The Azure target changes the mounted path to:

```text
/mnt/secrets-store/firebase.json
```

The intended flow is:

```text
Azure Key Vault
      |
      | firebase-service-account
      v
Secrets Store CSI Driver
      |
      v
ApplicationBib Pod
      |
      v
/mnt/secrets-store/firebase.json
      |
      v
Firebase Admin SDK
      |
      v
Firebase
```

The Firebase service-account private key is never stored in Git.

---

## PostgreSQL credentials

The current PostgreSQL deployment receives its credentials through Vault.

The Azure migration stores the equivalent secrets in Key Vault:

```text
postgres-db
postgres-username
postgres-password
```

Target flow:

```text
Azure Key Vault
      |
      +--> postgres-db
      |
      +--> postgres-username
      |
      +--> postgres-password
              |
              v
      Secrets Store CSI Driver
              |
              v
       PostgreSQL Pod
```

The actual secret values are not part of the migration repository.

---

## Application Workload Identity

The ApplicationBib Kubernetes ServiceAccount is:

```text
applicationbib-azure-sa
```

The Azure target associates this ServiceAccount with a managed identity.

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
Azure Managed Identity
       |
       v
Azure Key Vault
```

The identity receives only the permissions required to retrieve the application secrets.

---

## PostgreSQL Workload Identity

PostgreSQL uses a separate ServiceAccount:

```text
postgres-azure-sa
```

A separate managed identity is used for PostgreSQL secret access.

```text
PostgreSQL Pod
       |
       v
postgres-azure-sa
       |
       v
AKS Workload Identity
       |
       v
PostgreSQL Managed Identity
       |
       v
Azure Key Vault
```

This separates application secret access from PostgreSQL secret access.

---

## Identity federation

AKS provides an OIDC issuer for Workload Identity.

The trust relationship is:

```text
AKS OIDC Issuer
       |
       v
Federated Identity Credential
       |
       v
Azure Managed Identity
       |
       v
Key Vault
```

The Kubernetes ServiceAccount identity is therefore federated with Azure rather than using a static client secret.

---

## Application secret mapping

The migration manifest maps:

| Key Vault secret | Mounted filename |
|---|---|
| `firebase-service-account` | `firebase.json` |

The backend reads:

```text
/mnt/secrets-store/firebase.json
```

---

## PostgreSQL secret mapping

The PostgreSQL migration maps:

| Key Vault secret | Mounted filename |
|---|---|
| `postgres-db` | `POSTGRES_DB` |
| `postgres-username` | `POSTGRES_USER` |
| `postgres-password` | `POSTGRES_PASSWORD` |

The PostgreSQL container reads these values from the mounted secret files.

---

## Secrets Store CSI Driver

The Azure AKS cluster is planned to use the Azure Key Vault Secrets Provider addon.

The target architecture is:

```text
Azure Key Vault
       |
       v
Azure Key Vault Secrets Provider
       |
       v
Secrets Store CSI Driver
       |
       v
Kubernetes Pod
       |
       v
Mounted secret files
```

The application does not need to communicate directly with the Key Vault API to read the mounted files.

---

## Security comparison

### Current local architecture

```text
Kubernetes ServiceAccount
        |
        v
Vault Kubernetes Auth
        |
        v
Vault
        |
        v
Vault Agent Injector
        |
        v
Pod filesystem
```

### Azure target architecture

```text
Kubernetes ServiceAccount
        |
        v
AKS Workload Identity
        |
        v
Azure Managed Identity
        |
        v
Azure Key Vault
        |
        v
Secrets Store CSI Driver
        |
        v
Pod filesystem
```

---

## Important security properties

### No static Azure credentials

The Kubernetes workloads do not need an Azure client secret stored in Kubernetes.

Workload Identity provides short-lived Azure authentication based on federated identity.

### Least privilege

ApplicationBib and PostgreSQL use separate managed identities.

Each identity should receive only the Key Vault permissions required by its workload.

### Secrets are not stored in Git

The following must never be committed:

```text
firebase-service-account
postgres-db
postgres-username
postgres-password
```

Only the names and references to these secrets appear in the migration manifests.

### Secrets are not stored in container images

The Firebase service account and PostgreSQL credentials are injected at runtime.

They must not be copied into Dockerfiles or container images.

---

## Frontend exception

The frontend does **not** use this secret-management flow.

The frontend Firebase Web configuration is build-time configuration supplied through GitLab CI/CD variables.

The Azure migration therefore does **not** create:

```text
Azure Key Vault
      |
      v
Frontend
```

for Firebase Web configuration.

The frontend continues to use:

```text
GitLab CI/CD Variables
        |
        v
Build arguments
        |
        v
Next.js build
```

Firebase Web configuration and Firebase Admin service-account credentials remain separate.

---

## Migration mapping

| Local component | Azure target |
|---|---|
| HashiCorp Vault | Azure Key Vault |
| Vault Kubernetes Auth | AKS Workload Identity |
| Vault Agent Injector | Secrets Store CSI Driver |
| Vault secret | Key Vault secret |
| Kubernetes ServiceAccount | ServiceAccount + Workload Identity |
| `/vault/secrets/...` | `/mnt/secrets-store/...` |
| Static secret injection | Federated workload identity |

---

## Trust boundary

The Azure secret architecture introduces a trust boundary between AKS and Azure identity services:

```text
+---------------------------+
| AKS                       |
|                           |
| ApplicationBib Pod        |
|       |                   |
|       v                   |
| Kubernetes ServiceAccount |
+-------|-------------------+
        |
        | federated identity
        v
+---------------------------+
| Microsoft Entra ID        |
|                           |
| Managed Identity          |
+-------|-------------------+
        |
        | authorized access
        v
+---------------------------+
| Azure Key Vault           |
|                           |
| Firebase credential       |
| PostgreSQL credentials    |
+---------------------------+
```

---

## Migration sequence

A future Azure deployment would follow:

```text
1. Create Azure Key Vault
          |
          v
2. Store required secrets
          |
          v
3. Enable AKS Workload Identity
          |
          v
4. Create managed identities
          |
          v
5. Create federated identity credentials
          |
          v
6. Grant Key Vault permissions
          |
          v
7. Create Kubernetes ServiceAccounts
          |
          v
8. Configure SecretProviderClass resources
          |
          v
9. Deploy workloads
          |
          v
10. Verify secret mounts
```

No secrets are created or deployed by this migration lab because the Azure subscription is disabled.

---

## Current status

- Azure Key Vault is defined as the target secret store.
- ApplicationBib Firebase credentials are mapped to `firebase-service-account`.
- PostgreSQL credentials are mapped to three Key Vault secrets.
- ApplicationBib Workload Identity is defined.
- PostgreSQL Workload Identity is defined.
- SecretProviderClass migration manifests exist.
- Existing Vault configuration remains untouched.
- No real secrets have been copied to Azure.
- No Azure Key Vault has been populated.
- No production credentials have been exposed.
- No Azure deployment has been performed.
