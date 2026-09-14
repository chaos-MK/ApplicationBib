# Azure CI/CD Migration

## Current CI/CD architecture

ApplicationBib currently uses GitLab CI/CD with separate backend and frontend pipelines.

The pipelines already include security, quality, build, container, Kubernetes validation, deployment, and post-deployment verification stages.

The existing pipelines remain unchanged by this migration.

---

# Backend CI/CD

The backend pipeline uses two runner types.

## Docker executor

The Docker-based runner performs application and security stages.

```text
GitLab Repository
       |
       v
GitLab CI
       |
       v
Docker Runner
       |
       +--> Gitleaks
       +--> Semgrep
       +--> SonarQube
       +--> Snyk
       +--> JUnit / Mockito
       +--> Hadolint
       +--> Maven
       +--> Syft
       +--> Trivy
```

The resulting container image is produced with the existing container build tooling.

---

## Local Kubernetes Shell executor

The current deployment runner uses a Shell executor with access to the local Kubernetes environment.

```text
GitLab CI
    |
    v
Shell Runner
    |
    +--> kubectl
    +--> Podman
    +--> Minikube
    +--> Terraform
    +--> Vault
    +--> Kubernetes rollout
    +--> Smoke tests
    +--> OWASP ZAP
```

This runner is tied to the current local infrastructure.

It is therefore the main part that changes when deployment moves from Minikube to Azure.

---

# Azure backend deployment architecture

The Azure target separates image/security processing from Azure deployment.

```text
GitLab Repository
       |
       v
GitLab CI/CD
       |
       +------------------------------+
       |                              |
       v                              v
Docker Runner                    Azure-capable Runner
       |                              |
       |                              +--> Azure CLI
       |                              +--> kubectl
       |                              +--> AKS
       |                              +--> ACR
       |
       +--> Security gates
       +--> Tests
       +--> Build
       +--> SBOM
       +--> Image scan
       |
       v
Buildah
       |
       v
Azure Container Registry
       |
       v
AKS
       |
       v
ApplicationBib
```

The exact future runner hosting model is a deployment decision.

This migration does not claim that a new Azure runner has been created.

---

# Backend security pipeline

The existing security gates remain applicable to Azure.

```text
Source
  |
  v
Gitleaks
  |
  v
Semgrep
  |
  v
SonarQube
  |
  v
Snyk
  |
  v
JUnit / Mockito
  |
  v
Hadolint
  |
  v
Maven Build
  |
  v
Syft SBOM
  |
  v
Trivy
  |
  v
Kubernetes Validation
  |
  v
Pre-deployment Checks
  |
  v
Azure Deployment
  |
  v
Rollout Verification
  |
  v
Smoke Tests
  |
  v
OWASP ZAP
```

The security gates should continue to fail the pipeline when their configured failure conditions are met.

---

# Kubernetes validation

The existing Kubernetes validation stages remain useful for the Azure manifests.

Current tools include:

- kubeconform
- kube-score

The migration therefore keeps:

```text
Azure Kubernetes Manifests
        |
        +--> kubeconform
        |
        +--> kube-score
        |
        v
Azure deployment
```

The Azure migration manifests are separate from the original `k8s/` directory.

---

# Frontend CI/CD

The frontend uses a Docker-based GitLab runner.

Current pipeline:

```text
GitLab Repository
       |
       v
GitLab CI
       |
       v
Docker Runner
       |
       +--> Gitleaks
       +--> Semgrep
       +--> npm audit
       +--> Snyk
       +--> TypeScript
       +--> ESLint
       +--> Next.js build
       +--> Hadolint
       +--> Syft
       +--> Trivy
       +--> Grype
       +--> Skopeo
```

Buildah / Podman are used for daemonless container image building.

---

# Frontend Firebase configuration

The Firebase Web configuration remains a build-time concern.

The Azure migration does not move these values into Azure Key Vault.

The flow remains:

```text
GitLab CI/CD Variables
        |
        v
Buildah --build-arg
        |
        v
Frontend Dockerfile ARG
        |
        v
Dockerfile ENV
        |
        v
npm run build
        |
        v
Next.js production image
```

The Firebase Web configuration is therefore separate from the Firebase Admin service-account credential used by the backend.

---

# Frontend Azure deployment

The target deployment flow is:

```text
GitLab CI
    |
    v
Buildah
    |
    v
Frontend Image
    |
    v
Azure Container Registry
    |
    v
AKS
    |
    v
Ritual Growth Service
```

The frontend is exposed through the Azure NGINX ingress architecture.

---

# Registry migration

The container registry destination changes from GitLab Container Registry to Azure Container Registry.

Current:

```text
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

The existing image security controls remain before the image is pushed.

---

# Deployment authentication

The Azure deployment pipeline must authenticate to Azure before accessing AKS or other Azure resources.

Conceptually:

```text
GitLab CI
    |
    | Azure authentication
    v
Microsoft Entra ID
    |
    v
Azure permissions
    |
    +--> AKS
    +--> ACR
    +--> Key Vault
```

The exact authentication mechanism for the future GitLab runner is not defined by this migration.

A real deployment should prefer short-lived federated credentials / workload identity over long-lived Azure client secrets.

No Azure credentials are added to the migration repository.

---

# AKS deployment

The target deployment sequence is:

```text
Validated Kubernetes manifests
          |
          v
kubectl
          |
          v
AKS
          |
          v
Kubernetes Deployment
          |
          v
ApplicationBib / Ritual Growth Pods
```

The application images are pulled from ACR.

---

# Deployment verification

After deployment, the pipeline should verify:

```text
AKS Deployment
      |
      v
Rollout status
      |
      v
Readiness
      |
      v
Application health
      |
      v
Smoke tests
      |
      v
OWASP ZAP
```

The existing smoke-test philosophy remains applicable to the Azure deployment.

---

# Rollback

The existing project includes deployment verification and rollback logic.

The Azure target retains the same principle:

```text
Deployment
    |
    v
Rollout verification
    |
    +---- PASS ----> Continue
    |
    +---- FAIL ----> Rollback / stop deployment
```

The exact Azure rollback implementation depends on the final AKS deployment strategy.

---

# Local versus Azure CI/CD

| Current local environment | Azure target |
|---|---|
| GitLab CI | GitLab CI |
| Docker runner | Docker runner |
| Shell runner | Azure-capable deployment runner |
| Minikube | AKS |
| kubectl | kubectl |
| Podman / Buildah | Podman / Buildah |
| GitLab Container Registry | Azure Container Registry |
| Local Terraform | Azure infrastructure configuration |
| Vault | Azure Key Vault |
| Vault authentication | Azure Workload Identity / Entra ID |
| Local deployment | AKS deployment |
| Local smoke tests | AKS smoke tests |
| Local ZAP target | Azure application endpoint |

The security stages are largely preserved; the infrastructure and deployment targets change.

---

# Important separation of responsibilities

The Azure migration keeps four concerns separate.

## 1. Source and security

```text
GitLab
  |
  v
Security gates
```

## 2. Image creation

```text
Buildah / Podman
  |
  v
Container image
```

## 3. Image storage

```text
Azure Container Registry
```

## 4. Runtime deployment

```text
AKS
  |
  v
Application pods
```

This separation makes the migration easier to reason about and preserves the existing DevSecOps security gates.

---

# CI/CD trust boundaries

The Azure deployment introduces the following major boundaries:

```text
+-----------------------+
| GitLab                |
| Source + CI/CD        |
+-----------+-----------+
            |
            | authenticated deployment
            v
+-----------------------+
| CI Runner             |
| Build / Security      |
+-----------+-----------+
            |
            | image push
            v
+-----------------------+
| Azure Container       |
| Registry              |
+-----------+-----------+
            |
            | image pull
            v
+-----------------------+
| AKS                   |
| Runtime workloads     |
+-----------------------+
```

Azure credentials, registry credentials, and application secrets must not be embedded into container images.

---

# What changes

The primary CI/CD infrastructure changes are:

```text
Minikube deployment
        ↓
AKS deployment
```

```text
GitLab Container Registry
        ↓
Azure Container Registry
```

```text
Local deployment credentials
        ↓
Azure identity-based authentication
```

```text
Vault
        ↓
Azure Key Vault
```

The application security gates remain conceptually unchanged.

---

# What does not change

The migration does not modify:

- existing GitLab repositories
- existing GitLab CI/CD files
- Gitleaks configuration
- Semgrep configuration
- SonarQube configuration
- Snyk configuration
- unit tests
- Hadolint configuration
- SBOM generation
- container scanning
- frontend Firebase build configuration
- existing local Kubernetes manifests
- existing Terraform
- existing Minikube environment

All Azure-specific migration artifacts remain inside `azure-migration/`.

---

# Current status

- Azure CI/CD architecture documented.
- Backend CI/CD migration documented.
- Frontend CI/CD migration documented.
- GitLab Container Registry → ACR migration documented.
- Security gates retained in the target architecture.
- Azure deployment runner identified as a future requirement.
- Azure authentication requirement documented.
- AKS deployment flow documented.
- Rollout and smoke-test flow documented.
- Existing CI/CD files remain untouched.
- No Azure CI/CD runner has been created.
- No Azure deployment has been performed.
- No image has been pushed to ACR.
