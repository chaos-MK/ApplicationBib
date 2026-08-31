# Ritual Growth — DevSecOps Platform

A secure, observable, and reproducible deployment of the **Ritual Growth** application using Kubernetes, Terraform, Helm, GitLab CI/CD, HashiCorp Vault, Firebase Authentication, Prometheus, Grafana, Loki, Grafana Alloy, Alertmanager, PostgreSQL, Cloudflare, and automated DevSecOps security controls.

The project demonstrates a complete DevSecOps workflow covering application development, infrastructure as code, container security, CI/CD security, runtime protection, secrets management, monitoring, logging, alerting, and threat modeling.

---

## 1. Architecture Overview

The platform consists of:

- Next.js / React frontend
- Spring Boot backend
- Firebase Authentication
- PostgreSQL
- Kubernetes / Minikube
- NGINX Ingress Controller
- Cloudflare Quick Tunnel
- HashiCorp Vault
- Vault Agent Injector
- Prometheus
- Grafana
- Alertmanager
- Loki
- Grafana Alloy
- kube-state-metrics
- PostgreSQL Exporter
- Podman Exporter
- Terraform
- Helm
- GitLab CI/CD
- GitLab Container Registry

High-level architecture:

```text
Internet
   |
Cloudflare Quick Tunnel
   |
cloudflared
   |
NGINX Ingress
   |------------------|------------------|
Frontend            Backend            Grafana
   |                  |                    |
Firebase Auth     Firebase Auth       Prometheus
                     |                    |
                 PostgreSQL           Loki
                     |
                   Vault
```

The detailed architecture and threat-model documentation are available under:

```text
docs/threat-model/
```

---

## 2. Terraform / Infrastructure as Code

Terraform is used as the primary Infrastructure-as-Code layer.

Terraform manages the Kubernetes and monitoring infrastructure, including:

- Kubernetes namespaces
- Helm releases
- NGINX Ingress Controller
- kube-prometheus-stack
- Prometheus
- Grafana
- Alertmanager
- Loki
- Grafana Alloy
- ServiceMonitors
- PostgreSQL monitoring
- Podman Exporter
- Grafana dashboard provisioning
- Kubernetes monitoring configuration

Typical workflow:

```bash
cd terraform

terraform init
terraform plan
terraform apply
```

Terraform provides:

- Reproducible infrastructure
- Version-controlled infrastructure configuration
- Declarative resource management
- Infrastructure drift detection
- Repeatable deployments
- Reduced dependency on machine-specific commands

Terraform state files and credentials are intentionally excluded from source control.

The infrastructure is designed around Kubernetes and therefore provides a path toward deployment on managed Kubernetes environments such as AWS EKS without redesigning the application architecture.

---

## 3. Helm

Helm is used to deploy Kubernetes infrastructure components.

Terraform manages the Helm releases so that Helm configuration remains part of the Infrastructure-as-Code workflow.

The monitoring stack includes Helm-managed components such as:

- Prometheus
- Grafana
- Alertmanager
- kube-state-metrics
- Loki
- Grafana Alloy

This keeps infrastructure provisioning reproducible and version controlled.

---

## 4. Kubernetes

The application and supporting infrastructure run inside Kubernetes.

Main workloads include:

- Frontend
- Backend
- PostgreSQL
- Vault
- NGINX Ingress
- Prometheus
- Grafana
- Alertmanager
- Loki
- Grafana Alloy
- Monitoring exporters

Kubernetes security controls include:

- Namespaces
- ServiceAccounts
- RBAC
- NetworkPolicies
- Resource requests and limits
- Resource limits for CPU, memory, and ephemeral storage
- Security contexts
- Non-root containers
- UID/GID 10001 for hardened application containers
- `runAsNonRoot`
- Dropped Linux capabilities
- `allowPrivilegeEscalation: false`
- Read-only root filesystems where compatible
- Readiness probes
- Controlled secret injection through Vault

Kubernetes manifests are validated using:

- kubeconform
- kube-score

---

## 5. Container Security and Hardening

Application containers are hardened as part of the DevSecOps process.

The backend container:

- Runs as a non-root user
- Uses UID/GID 10001
- Drops unnecessary Linux capabilities
- Disables privilege escalation
- Uses a read-only root filesystem where compatible
- Uses a writable `/tmp` `emptyDir` mount where required by Spring Boot/Tomcat

Example security model:

```text
Container
   |
   +-- Non-root UID 10001
   +-- Non-root GID 10001
   +-- allowPrivilegeEscalation = false
   +-- capabilities = DROP ALL
   +-- readOnlyRootFilesystem where compatible
```

The Kubernetes configuration applies the corresponding runtime security context.

Dockerfiles are also statically analyzed with Hadolint.

---

## 6. Frontend

The frontend is implemented using:

- Next.js 15.3.3
- React 19
- Firebase Web SDK 11.9.1

The frontend handles:

- User interface
- Registration
- Login
- Firebase Authentication
- Firebase ID-token acquisition
- Backend API communication

Production Firebase configuration uses `NEXT_PUBLIC_*` variables.

These values are intentionally public Firebase client configuration values and are therefore not treated as confidential secrets.

The frontend Firebase configuration is supplied during the CI/CD build process and becomes part of the generated Next.js application.

Vault is not used as the runtime secret source for the frontend Firebase public configuration.

---

## 7. Backend

The backend is implemented using:

- Spring Boot
- Java 21
- Maven
- Firebase Admin SDK
- PostgreSQL
- Spring Boot Actuator
- Micrometer

The backend provides:

- REST APIs
- Business logic
- Firebase ID-token verification
- PostgreSQL access
- Health/readiness endpoints
- Prometheus metrics

Authentication is enforced server-side.

Requests contain:

```text
Authorization: Bearer <Firebase ID token>
```

The backend verifies the token using the Firebase Admin SDK:

```java
FirebaseAuth.getInstance().verifyIdToken(idToken);
```

The backend does not trust the frontend simply because a request originates from it.

---

## 8. Firebase Authentication

Firebase Authentication provides application identity management.

The frontend uses the Firebase Web SDK for:

- Registration
- Login
- Session management
- ID-token acquisition

The backend independently verifies the Firebase ID token.

The authentication flow is:

```text
Frontend
   |
Firebase Authentication
   |
Firebase ID Token
   |
Backend
   |
Firebase Admin SDK
   |
Token Verification
```

This provides server-side authentication enforcement rather than relying solely on client-side state.

---

## 9. CI/CD

GitLab CI/CD is used to build, validate, scan, and deploy the application.

The pipeline follows the general flow:

```text
Secrets
   ↓
SAST
   ↓
Dependency Scan
   ↓
Tests
   ↓
Lint
   ↓
Build
   ↓
SBOM
   ↓
Image Scan
   ↓
Manifest Validation
   ↓
Smoke Tests
   ↓
Pre-deployment Checks
   ↓
Deploy
   ↓
Rollout Verification
   ↓
Post-deployment Smoke Tests
   ↓
DAST
```

The backend and frontend use containerized build/security tooling appropriate to each pipeline.

The CI/CD system publishes images to the GitLab Container Registry before Kubernetes deployment.

---

## 10. DevSecOps Security Pipeline

Security is integrated into the software delivery lifecycle.

Implemented security tooling includes:

| Tool | Purpose |
|---|---|
| Gitleaks | Secret detection |
| Semgrep | SAST |
| SonarQube / SonarCloud | Code quality and security analysis |
| Snyk | Dependency vulnerability scanning |
| npm audit | Frontend dependency auditing |
| JUnit / Mockito | Automated tests |
| Hadolint | Dockerfile security/linting |
| Syft | SBOM generation |
| Trivy | Container vulnerability scanning |
| Grype | Additional container vulnerability scanning |
| kubeconform | Kubernetes schema validation |
| kube-score | Kubernetes security/configuration analysis |
| OWASP ZAP | Dynamic application security testing |

The objective is to identify security problems before artifacts reach the runtime environment.

---

## 11. Container Build and Image Workflow

The project uses daemonless/container-native tooling.

The container workflow uses:

- Podman
- Buildah
- Skopeo

General workflow:

```text
Source Code
    ↓
Buildah / Podman
    ↓
Container Image
    ↓
Security Scanning
    ↓
Skopeo / Registry Operations
    ↓
GitLab Container Registry
    ↓
Kubernetes
```

Podman is daemonless and is used as the container engine in the development/deployment environment.

Buildah is used for container image construction where applicable.

Skopeo is used for container image/registry operations without requiring a Docker daemon.

These tools form part of the container supply-chain implementation rather than being separate runtime application components.

---

## 12. Cloudflare

External access is provided through a Cloudflare Quick Tunnel during development/testing.

Traffic flows through:

```text
Internet
   ↓
Cloudflare Quick Tunnel
   ↓
cloudflared
   ↓
NGINX Ingress
   ↓
Kubernetes Services
```

The backend is not intentionally exposed directly as a public application port.

The Quick Tunnel provides external connectivity but does not itself provide application-level authentication.

For production environments, stronger edge authentication such as Cloudflare Access should be considered.

The Cloudflare Quick Tunnel is managed separately and is not managed by Terraform.

---

## 13. Prometheus / Grafana

Prometheus is the central metrics collection and storage system.

Grafana provides metrics visualization and dashboards.

```text
Metrics Sources
      ↓
Prometheus
      ↓
Grafana
```

Grafana dashboards cover:

- Backend metrics
- Frontend runtime metrics
- PostgreSQL metrics
- Kubernetes metrics
- Infrastructure metrics
- Logs through Loki

Terraform manages the relevant Grafana dashboard provisioning.

---

## 14. Prometheus Client / Micrometer

The backend uses Micrometer and Spring Boot Actuator for Prometheus instrumentation.

The application exposes:

```text
/actuator/prometheus
```

The architecture is:

```text
Spring Boot
    ↓
Micrometer / Prometheus instrumentation
    ↓
/actuator/prometheus
    ↓
Prometheus Server
```

The Prometheus instrumentation/client is not the Prometheus Server.

Prometheus Server independently scrapes and stores the resulting time-series metrics.

---

## 15. Frontend Monitoring

The frontend does not currently expose a custom `/metrics` endpoint scraped directly by Prometheus.

The confirmed frontend monitoring includes Node.js process/runtime telemetry such as:

- CPU usage
- Memory usage
- Node.js heap used
- Node.js heap total
- Event-loop lag
- Active Node.js handles
- Active Node.js requests

A Terraform-provisioned `ritual-growth-ui` Grafana dashboard contains seven panels corresponding to these metrics.

No unverified frontend request-rate or error-rate metrics are claimed.

---

## 16. Kubernetes State Metrics

Kubernetes state information is collected using kube-state-metrics.

```text
Kubernetes API
      ↓
kube-state-metrics
      ↓
Prometheus
      ↓
Grafana
```

kube-state-metrics exposes Kubernetes object/state information such as:

- Pod state
- Deployment state
- Replica information
- Readiness
- Restarts
- Resource state

It is not the Prometheus Server.

---

## 17. PostgreSQL + PostgreSQL Exporter

PostgreSQL stores application data.

PostgreSQL monitoring uses PostgreSQL Exporter:

```text
PostgreSQL
     ↓
PostgreSQL Exporter
     ↓
Prometheus
     ↓
Grafana
```

PostgreSQL does not send metrics directly to Grafana.

The backend database credentials are managed through Vault and injected into the backend workload.

---

## 18. Podman Exporter

Podman-level telemetry is collected using Podman Exporter.

```text
Podman
   ↓
Podman Exporter
   ↓
Prometheus
   ↓
Grafana
```

Podman Exporter provides container-engine-level monitoring in addition to:

- Application metrics
- Kubernetes state metrics
- PostgreSQL metrics

This separates container-runtime monitoring from application and Kubernetes metrics.

---

## 19. Grafana Alloy + Loki

Grafana Alloy is responsible for centralized container/workload log collection.

The architecture is:

```text
Kubernetes Components / Workloads
            ↓
       Container Logs
            ↓
       Grafana Alloy
            ↓
           Loki
            ↓
         Grafana
```

Alloy collects logs from the Kubernetes environment rather than being limited to only the frontend and backend.

Prometheus and Loki have separate responsibilities:

```text
Prometheus = Metrics
Loki       = Logs
```

Prometheus does not store application logs.

Loki does not store Prometheus time-series metrics.

---

## 20. Alertmanager

Prometheus evaluates alerting rules and sends fired alerts to Alertmanager.

```text
Prometheus
     ↓
Alertmanager
     ├── Webhook
     └── Email
```

Alertmanager provides:

- Alert routing
- Alert grouping
- Notification delivery
- Webhook notifications
- Email notifications

Alertmanager SMTP/email credentials are managed through Vault and injected through the Vault Agent Injector.

Prometheus does not directly send SMTP notifications.

---

## 21. Vault + Vault Agent Injector

HashiCorp Vault provides centralized secret management.

The Vault Agent Injector provides runtime secret injection to workloads that require sensitive credentials.

Currently managed credentials include:

- Backend Firebase service-account credentials
- Backend PostgreSQL/database credentials
- Alertmanager SMTP/email credentials

Architecture:

```text
Vault
   ↓
Vault Agent Injector
   ↓
Workload
```

Backend:

```text
Vault
   ↓
Vault Agent Injector
   ↓
Backend Pod
   ├── Firebase credentials
   └── PostgreSQL credentials
```

Alertmanager:

```text
Vault
   ↓
Vault Agent Injector
   ↓
Alertmanager
   └── SMTP credentials
```

Vault Kubernetes authentication is used for workload authentication.

Vault policies scope which workloads can access which secrets.

Secret TTL controls are also configured.

Sensitive credentials are not committed to the public repository or baked into the frontend image.

---

## 22. Network Security

Kubernetes NetworkPolicies are implemented to restrict workload communication according to the required architecture.

The goal is to prevent unnecessary workload-to-workload communication.

NetworkPolicies form an additional runtime security layer alongside:

- Authentication
- Vault
- RBAC
- Container hardening
- CI/CD security
- Ingress controls

NetworkPolicy coverage should evolve as new workloads and communication paths are introduced.

---

## 23. Kubernetes RBAC

Kubernetes RBAC has been reviewed and tightened where practical.

The project also considers the permissions required by Vault Kubernetes authentication.

Workloads that authenticate to Vault require the appropriate Kubernetes ServiceAccount/token relationship.

Therefore, RBAC restrictions cannot simply remove all ServiceAccount-related permissions without potentially breaking Vault authentication.

The objective is least privilege while preserving required functionality.

---

## 24. Threat Model

The project contains a STRIDE-based threat model covering:

- Spoofing
- Tampering
- Repudiation
- Information Disclosure
- Denial of Service
- Elevation of Privilege

Major trust boundaries include:

- Internet
- Cloudflare
- Kubernetes Ingress
- Frontend
- Backend
- Firebase
- PostgreSQL
- Vault
- Kubernetes Control Plane
- Monitoring
- CI/CD
- Container Registry

Threat-model documentation:

```text
docs/threat-model/threat-model.md
```

The documentation includes:

- Trust-boundary diagram
- Architecture diagram
- STRIDE threat table
- Security control mapping
- Residual risks
- Future recommendations

---

## 25. Security Control Summary

Implemented controls include:

- Firebase ID-token verification
- Vault-managed runtime credentials
- Vault Kubernetes authentication
- Vault policy scoping
- Vault secret TTL controls
- Kubernetes NetworkPolicies
- Kubernetes RBAC review
- Non-root containers
- Container capability dropping
- Privilege-escalation restrictions
- Resource limits
- Secret scanning
- SAST
- Dependency scanning
- Container vulnerability scanning
- SBOM generation
- Dockerfile linting
- Kubernetes manifest validation
- Kubernetes security analysis
- OWASP ZAP DAST
- Prometheus monitoring
- Grafana dashboards
- Alertmanager
- Centralized Alloy/Loki logging
- Cloudflare-based external access

---

## 26. Security Findings and Residual Risks

The project intentionally documents controls that are implemented separately from controls that remain future improvements.

Remaining improvements include:

- Cloudflare Access / stronger edge authentication
- Further Vault policy refinement
- Further Kubernetes RBAC refinement
- Dedicated least-privilege CI service accounts
- Reduced local Shell Runner blast radius
- Vault audit logging
- Stronger Prometheus/Grafana access controls
- Rate limiting
- Image signing and verification
- Improved CI/CD provenance
- Build attestations
- Centralized security audit-event aggregation
- Centralized security-event detection and correlation
- SIEM/SOC-style security monitoring

Operational monitoring through Prometheus, Grafana, Alloy, Loki, and Alertmanager should not be represented as a complete SIEM/SOC capability.

---

## 27. CI Runner Architecture

The CI/CD environment uses different runner execution contexts for different responsibilities.

### Build / Security Runner

The Docker-based executor is used for build and security validation workloads.

It handles activities such as:

- Application builds
- Security scanning
- Dependency analysis
- SBOM generation
- Image scanning
- Static validation

### Local Shell Runner

The Local Shell runner is used for deployment and runtime verification.

It has direct access to the local:

- Kubernetes / Minikube environment
- kubeconfig
- Vault
- Terraform
- Deployment environment

Because of this access, the Local Shell runner has a larger blast radius and is explicitly documented as a residual security risk.

---

## 28. Deployment Procedure

The intended deployment process is:

```text
1. Configure required variables/secrets
2. Run CI/CD security validation
3. Build container images
4. Scan images
5. Generate SBOM
6. Push images to GitLab Container Registry
7. Validate Kubernetes manifests
8. Run Terraform
9. Deploy Kubernetes workloads
10. Verify rollout
11. Verify application health
12. Verify authentication
13. Verify Prometheus
14. Verify Grafana
15. Verify Alloy/Loki
16. Verify Alertmanager
17. Run post-deployment security checks
```

Deployment should be performed through documented scripts and Terraform rather than relying on machine-specific commands.

---

## 29. Required Secrets / CI Variables

Sensitive values must be supplied through secure CI/CD variables, Vault, or another appropriate secret-management mechanism.

### Backend

Examples include:

```text
Firebase service-account credentials
PostgreSQL credentials
Vault authentication information
```

### Alertmanager

Examples include:

```text
SMTP username
SMTP password
SMTP configuration
```

### Registry / Deployment

Where required:

```text
Registry authentication
Deployment credentials
Vault credentials/tokens
```

### Frontend

Firebase `NEXT_PUBLIC_*` configuration values are intentionally public client configuration values.

Examples:

```text
NEXT_PUBLIC_FIREBASE_API_KEY
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN
NEXT_PUBLIC_FIREBASE_PROJECT_ID
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID
NEXT_PUBLIC_FIREBASE_APP_ID
```

These should still be configured correctly and scoped appropriately, but they are not equivalent to backend private credentials.

Private keys, passwords, service-account JSON, tokens, and other confidential credentials must never be committed.

---

## 30. Local Setup

The local environment uses:

- Linux
- Minikube
- Podman
- Terraform
- Helm
- kubectl
- Java / Maven for backend development
- Git

Example:

```bash
git clone <repository>
cd ApplicationBib
```

Start Minikube:

```bash
minikube start --driver=podman
```

Verify Kubernetes:

```bash
kubectl get nodes
```

Initialize Terraform:

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Verify workloads:

```bash
kubectl get pods -A
```

The project should be deployed using the documented automation rather than depending on the original developer's workstation-specific commands.

---

## 31. Troubleshooting

### Kubernetes

```bash
kubectl get pods -A
kubectl get svc -A
kubectl get ingress -A
kubectl describe pod <pod>
kubectl logs <pod>
```

### Terraform

```bash
terraform plan
terraform state list
```

### Monitoring

Verify:

```text
Prometheus
Grafana
Alertmanager
Loki
Grafana Alloy
kube-state-metrics
PostgreSQL Exporter
Podman Exporter
```

### Application

Verify:

```text
Backend readiness
Backend health
Firebase authentication
PostgreSQL connectivity
/actuator/prometheus
```

### Vault

Check:

```text
Vault pod
Vault Agent Injector
Kubernetes authentication
Vault policies
Secret injection
Injected secret files
Secret TTLs
```

### CI/CD

Inspect pipeline stages in order.

A failed security stage should be investigated rather than bypassed without understanding the finding.

---

## 32. Deployment Runbook

The deployment runbook should provide reproducible procedures for:

### Infrastructure

```text
Terraform initialization
Terraform plan
Terraform apply
Terraform verification
```

### Kubernetes

```text
Namespace verification
Pod verification
Service verification
Ingress verification
Rollout verification
```

### Application

```text
Frontend verification
Backend health verification
Firebase authentication verification
Database connectivity verification
```

### Monitoring

```text
Prometheus verification
Grafana verification
Alertmanager verification
Loki verification
Alloy verification
Exporter verification
```

### Security

```text
Vault verification
NetworkPolicy verification
RBAC verification
Container security verification
CI/CD security verification
DAST verification
```

### Recovery

The runbook should also document:

- Failed rollout recovery
- Pod restart procedures
- Secret injection troubleshooting
- Vault recovery checks
- Monitoring recovery
- Registry/image troubleshooting
- Terraform recovery procedures

The objective is that another engineer can operate the platform without knowing the original developer's machine-specific commands.

---

## 33. Secret Rotation

Sensitive runtime credentials are managed through Vault.

The intended rotation model is:

```text
Secret
   ↓
Vault
   ↓
Vault Agent Injector
   ↓
Workload
```

Rotation applies to credentials such as:

- Firebase service-account credentials
- PostgreSQL credentials
- Alertmanager SMTP credentials
- Registry/deployment credentials where applicable
- Vault authentication credentials/tokens

General procedure:

```text
1. Update the credential in Vault
2. Verify the Vault policy
3. Verify Vault Agent injection
4. Restart/reload the affected workload if required
5. Verify application/database connectivity
6. Verify monitoring
7. Verify Alertmanager
8. Remove/revoke the obsolete credential
```

Credentials must never be rotated by committing new secret values to Git.

---

## 34. Monitoring and Observability Summary

The observability architecture separates metrics, logs, dashboards, and alert routing.

### Application metrics

```text
Spring Boot
   ↓
Micrometer
   ↓
/actuator/prometheus
   ↓
Prometheus
   ↓
Grafana
```

### Kubernetes metrics

```text
Kubernetes API
   ↓
kube-state-metrics
   ↓
Prometheus
   ↓
Grafana
```

### PostgreSQL metrics

```text
PostgreSQL
   ↓
PostgreSQL Exporter
   ↓
Prometheus
   ↓
Grafana
```

### Podman metrics

```text
Podman
   ↓
Podman Exporter
   ↓
Prometheus
   ↓
Grafana
```

### Logs

```text
Kubernetes Components / Workloads
   ↓
Grafana Alloy
   ↓
Loki
   ↓
Grafana
```

### Alerts

```text
Prometheus
   ↓
Alertmanager
   ├── Webhook
   └── Email
```

---

## 35. Grafana Dashboards

Grafana dashboards are managed through Terraform.

The project includes:

- Existing Vault-related Grafana dashboard imported into Terraform
- Terraform-managed monitoring configuration
- Seven-panel `ritual-growth-ui` customer dashboard

The `ritual-growth-ui` dashboard monitors:

1. CPU usage
2. Memory usage
3. Node.js heap used
4. Node.js heap total
5. Event-loop lag
6. Active Node.js handles
7. Active Node.js requests

Dashboard JSON is stored in:

```text
k8s/monitoring/dashboards/ritual-growth-ui-dashboard.json
```

Terraform provisions the dashboard through the monitoring configuration.

---

## 36. Observability vs Security Monitoring

The project distinguishes operational observability from centralized security monitoring.

### Operational observability

Implemented:

```text
Prometheus
Grafana
Alertmanager
Grafana Alloy
Loki
```

These provide:

- Metrics
- Logs
- Dashboards
- Operational alerts

### Centralized security monitoring

Not currently implemented as a complete SIEM/SOC capability.

Future work includes centralized collection and correlation of:

- Vault audit events
- Kubernetes audit events
- CI/CD security events
- Ingress security events
- Application security events
- Administrative actions

This distinction prevents operational monitoring from being incorrectly represented as a complete security-event monitoring platform.

---

## 37. Repository Structure

The repository is organized around application code, infrastructure, deployment automation, CI/CD, and documentation.

```text
ApplicationBib/
├── src/
├── k8s/
│   └── monitoring/
│       └── dashboards/
│           └── ritual-growth-ui-dashboard.json
├── terraform/
├── scripts/
├── docs/
│   └── threat-model/
│       └── threat-model.md
├── Dockerfile
├── pom.xml
└── .gitlab-ci.yml
```

The exact structure may evolve as deployment automation and operational runbooks are expanded.

---

## 38. Reproducibility

The project is designed so that another engineer can reproduce and operate the environment without relying on undocumented workstation-specific commands.

Target workflow:

```text
Clone Repository
      ↓
Configure Required Variables / Secrets
      ↓
Run CI/CD or Deployment Automation
      ↓
Terraform / Helm
      ↓
Kubernetes Infrastructure
      ↓
Application Deployment
      ↓
Monitoring + Logging
      ↓
Security Verification
```

Machine-specific:

- Credentials
- Tokens
- Terraform state
- Service-account private keys
- Generated runtime secrets

are intentionally excluded from source control.

---

## 39. Project Status

Implemented:

- ✅ Next.js / React frontend
- ✅ Spring Boot backend
- ✅ Firebase Authentication
- ✅ Firebase Admin token verification
- ✅ PostgreSQL
- ✅ Kubernetes / Minikube
- ✅ Terraform
- ✅ Helm
- ✅ NGINX Ingress
- ✅ Cloudflare Quick Tunnel
- ✅ Vault
- ✅ Vault Agent Injector
- ✅ Vault Kubernetes authentication
- ✅ Vault secret TTL controls
- ✅ Prometheus
- ✅ Grafana
- ✅ Alertmanager
- ✅ Loki
- ✅ Grafana Alloy
- ✅ kube-state-metrics
- ✅ PostgreSQL Exporter
- ✅ Podman Exporter
- ✅ Terraform-managed Grafana dashboards
- ✅ CI/CD
- ✅ Gitleaks
- ✅ Semgrep
- ✅ SonarQube / SonarCloud
- ✅ Snyk
- ✅ npm audit
- ✅ Hadolint
- ✅ Syft
- ✅ Trivy
- ✅ Grype
- ✅ kubeconform
- ✅ kube-score
- ✅ OWASP ZAP
- ✅ Kubernetes NetworkPolicies
- ✅ Kubernetes RBAC review
- ✅ Container hardening
- ✅ SBOM generation
- ✅ Runtime secret injection
- ✅ Centralized container/workload logging
- ✅ Monitoring and alerting
- ✅ STRIDE threat model

---

## 40. Future Improvements

The following are explicitly future improvements:

- Cloudflare Access / stronger edge authentication
- Further Vault least-privilege refinement
- Further Kubernetes RBAC refinement
- Dedicated least-privilege CI service accounts
- Reduced Local Shell Runner blast radius
- Centralized Vault audit logging
- Stronger Prometheus/Grafana access controls
- Ingress/application rate limiting
- Image signing and verification with Cosign/Sigstore
- Improved CI/CD provenance
- Build attestations
- Centralized security audit-event aggregation
- Centralized security-event detection and correlation
- SIEM/SOC-style security monitoring

These items are not represented as currently implemented controls.

---

## 41. Security Philosophy

The project follows a defense-in-depth DevSecOps model.

Security is applied across:

```text
Source Code
     ↓
CI/CD
     ↓
Dependencies
     ↓
Container Build
     ↓
Image Security
     ↓
Kubernetes Validation
     ↓
Infrastructure
     ↓
Runtime Security
     ↓
Secrets Management
     ↓
Monitoring
     ↓
Alerting
```

The architecture intentionally separates:

- Authentication from authorization
- Public frontend configuration from confidential backend credentials
- Secrets from source code
- Metrics from logs
- Monitoring from security-event monitoring
- Infrastructure management from runtime application logic
- CI/CD build responsibilities from deployment responsibilities

---

## 42. Security Disclaimer

This project is a DevSecOps engineering and portfolio environment.

Security controls are documented according to the deployed architecture and verified configuration. The presence of a security tool or control does not guarantee complete security.

Security findings, residual risks, limitations, and future improvements are intentionally documented.

The objective is to demonstrate a realistic:

**secure-by-design, observable, reproducible, continuously validated, and infrastructure-as-code driven DevSecOps platform.**