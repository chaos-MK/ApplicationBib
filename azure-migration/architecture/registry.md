# Azure Container Registry Migration

## Current architecture

The existing project builds container images through GitLab CI/CD and pushes them to the GitLab Container Registry.

### Backend

```text
GitLab CI
    |
    v
Buildah / Podman
    |
    v
registry.gitlab.com/khalilmohamed798/applicationbib:<tag>
```

### Frontend

```text
GitLab CI
    |
    v
Buildah / Podman
    |
    v
GitLab Container Registry
```

The existing GitLab registry remains unchanged.

---

## Azure target architecture

The Azure migration uses Azure Container Registry (ACR) as the container image registry consumed by AKS.

```text
GitLab CI/CD
     |
     v
Buildah / Podman
     |
     v
Container Image
     |
     v
Azure Container Registry
applicationbibacr.azurecr.io
     |
     v
AKS
```

The target image names are:

```text
applicationbibacr.azurecr.io/applicationbib:<tag>
applicationbibacr.azurecr.io/ritual-growth-ui:<tag>
```

The migration does not require changing the existing GitLab repositories.

---

## Image migration

An existing GitLab image can be migrated to ACR by:

```text
GitLab Container Registry
        |
        | pull
        v
Buildah / Podman
        |
        | tag
        v
applicationbibacr.azurecr.io/applicationbib:<tag>
        |
        | push
        v
Azure Container Registry
```

Example commands for a future Azure-enabled environment:

```bash
az acr login --name "$AZ_ACR_NAME"

podman pull registry.gitlab.com/khalilmohamed798/applicationbib:<tag>

podman tag \
  registry.gitlab.com/khalilmohamed798/applicationbib:<tag> \
  "$AZ_ACR_NAME.azurecr.io/applicationbib:<tag>"

podman push \
  "$AZ_ACR_NAME.azurecr.io/applicationbib:<tag>"
```

Frontend:

```bash
podman pull <gitlab-frontend-image>:<tag>

podman tag \
  <gitlab-frontend-image>:<tag> \
  "$AZ_ACR_NAME.azurecr.io/ritual-growth-ui:<tag>"

podman push \
  "$AZ_ACR_NAME.azurecr.io/ritual-growth-ui:<tag>"
```

These commands are documentation examples only.

No image has been migrated to Azure; the registry configuration is documented as a migration target.

---

## AKS image pulling

The planned AKS infrastructure attaches ACR to the AKS cluster.

The target relationship is:

```text
AKS Managed Identity
        |
        | AcrPull
        v
Azure Container Registry
        |
        v
AKS Nodes
        |
        v
ApplicationBib / Ritual Growth Pods
```

This avoids storing an ACR username and password inside Kubernetes manifests.

The AKS cluster should therefore pull images using Azure identity-based authorization.

---

## Kubernetes image references

The Azure migration manifests use ACR image references.

Backend:

```yaml
image: applicationbibacr.azurecr.io/applicationbib:latest
```

Frontend:

```yaml
image: applicationbibacr.azurecr.io/ritual-growth-ui:latest
```

These are migration-target references and have not been deployed.

---

## Future CI/CD change

The existing GitLab CI/CD pipeline can continue to build images with Buildah.

Only the registry destination would change:

```text
Current:

GitLab CI
   |
   v
Buildah
   |
   v
GitLab Container Registry
```

Azure target:

```text
GitLab CI
   |
   v
Buildah
   |
   v
Azure Container Registry
   |
   v
AKS
```

The existing security stages remain applicable:

```text
Gitleaks
Semgrep
Dependency scanning
Tests
Hadolint
SBOM
Trivy / Grype
        |
        v
Build image
        |
        v
Push to ACR
```

The migration does not remove the existing image-security controls.

---

## Important security considerations

### Image authentication

AKS should use its managed identity to pull from ACR rather than embedding registry credentials in Kubernetes Secrets.

### Image tags

For production deployments, immutable version tags or commit-based tags are preferable to relying only on:

```text
:latest
```

The existing Azure manifests use `latest` only as migration-reference placeholders.

### Image scanning

Container scanning remains part of the CI/CD security process before an image is pushed to the registry.

### Registry boundary

ACR becomes a separate trust boundary:

```text
GitLab CI/CD
     |
     | authenticated image push
     v
Azure Container Registry
     |
     | managed identity / AcrPull
     v
AKS
```

No Firebase credentials, PostgreSQL passwords, or Vault secrets should be stored inside container images.

---

## Migration mapping

| Local/current | Azure target |
|---|---|
| GitLab Container Registry | Azure Container Registry |
| GitLab CI Buildah | GitLab CI Buildah |
| GitLab image authentication | Azure ACR authentication |
| Kubernetes image pull | AKS image pull |
| Registry credentials | AKS managed identity |
| `registry.gitlab.com/...` | `applicationbibacr.azurecr.io/...` |

## Current status

- Azure ACR is defined in the migration architecture.
- AKS is planned to use ACR.
- Azure Kubernetes manifests reference ACR.
- No image has been pushed to ACR.
- No GitLab CI/CD files have been modified.
- No existing container registry has been changed.
- No Azure deployment has been performed.
```

Next is **PostgreSQL → Azure Database for PostgreSQL Flexible Server**, which is the important database-side migration.
