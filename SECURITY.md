# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| main    | ✅ |

## Reporting a Vulnerability

If you discover a security vulnerability in this project, please report it by emailing:

**khalilmohamed798@gmail.com**

Do not open a public GitHub/GitLab issue for security vulnerabilities.

You should receive a response within 48 hours. Please include:

- Description of the vulnerability
- Steps to reproduce
- Potential impact

## Automated Scanning

This repository is scanned on every push and merge request using:

- **Gitleaks** — secret detection
- **Semgrep** — static application security testing (SAST)
- **SonarQube / SonarCloud** — code quality and security analysis
- **Snyk** — dependency / software composition analysis (SCA)
- **Hadolint** — Dockerfile linting
- **Syft** — SBOM (Software Bill of Materials) generation
- **Trivy & Grype** — container image vulnerability scanning
- **kubeconform** — Kubernetes manifest schema validation
- **kube-score** — Kubernetes manifest security/configuration analysis
- **OWASP ZAP** — dynamic application security testing (DAST)

## Runtime & Infrastructure Security

Beyond pipeline scanning, the deployed environment includes:

- **HashiCorp Vault** — centralized secret management, with credentials injected at runtime via the Vault Agent Injector rather than stored in configuration or source
- **cert-manager** — automated issuance and rotation of the TLS certificate used by the Vault Agent Injector's admission webhook (Terraform-managed, `rotationPolicy: Always`)
- **Kubernetes NetworkPolicies** — restrict workload-to-workload communication
- **Kubernetes RBAC** — least-privilege review, constrained by required Vault Kubernetes authentication
- **Container hardening** — non-root user, dropped Linux capabilities, `allowPrivilegeEscalation: false`, read-only root filesystem where compatible

A full architecture-level threat model, including trust boundaries, STRIDE analysis, attack paths, mitigations, and residual risks, is maintained at:

```text
~/ApplicationBib/docs/threat-model/threat-model.md
```

Risks that have been formally reviewed and accepted are tracked separately in:

```text
SECURITY-FINDINGS.md
```