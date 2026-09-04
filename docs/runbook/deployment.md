# Deployment Runbook

## Prerequisites

Before deployment:

- Minikube/Kubernetes is running.
- Terraform is initialized.
- Vault is running and **unsealed**.
- `secrets.env` has been created outside the Git repository.

Never commit `secrets.env` or place real secret values in Git.

## Deployment

Run:

```bash
./scripts/deploy.sh
```

The script applies the Terraform-managed infrastructure and waits for the backend, frontend, and PostgreSQL workloads to roll out.

## Secrets

Sensitive application configuration must be stored in **HashiCorp Vault**, not in the repository.

Examples:

- PostgreSQL credentials
- Firebase Admin service-account credentials
- Alertmanager SMTP credentials

Initialize/update Vault secrets with:

```bash
./scripts/bootstrap-vault-secrets.sh
```

The backend receives its Firebase service-account credential through Vault Agent Injector at:

```text
/vault/secrets/firebase.json
```

Do not put personal data, passwords, private keys, service-account credentials, or other sensitive values in Git, Dockerfiles, Kubernetes manifests, or frontend source code.

Frontend `NEXT_PUBLIC_*` Firebase configuration is handled separately through GitLab CI/CD build arguments because it is public client configuration, not a Firebase Admin secret.

## Verification

```bash
./scripts/verify-deployment.sh
```