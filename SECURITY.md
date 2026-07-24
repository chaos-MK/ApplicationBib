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

## Scanning

This repository is scanned automatically on every push/merge request using:
- Gitleaks (secret detection)
- Semgrep (SAST)
- Snyk (dependency/SCA)
- Hadolint (Dockerfile linting)
- Trivy & Grype (container image scanning)
- Syft (SBOM generation)
