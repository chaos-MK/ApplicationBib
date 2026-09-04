# Secret Rotation Runbook

Sensitive credentials are kept outside the Git repository and supplied through `secrets.env`.

## Secrets file

Create and maintain:

```text
secrets.env
```

The file must remain outside Git and must never be committed.

## Vault secrets

Bootstrap or update Vault secrets with:

```bash
./scripts/bootstrap-vault-secrets.sh
```

This manages:

- PostgreSQL credentials
- Firebase Admin service-account credentials
- Alertmanager SMTP credentials

Vault Agent Injector provides the required secrets to the appropriate workloads.

## GitLab Registry credential

Refresh the Kubernetes registry secret with:

```bash
./scripts/rotate-registry-secret.sh
```

The script updates:

```text
default/gitlab-registry-secret
```

using the registry credentials from `secrets.env`.

## Rotation procedure

1. Generate or revoke the credential in the relevant provider.
2. Update the value in `secrets.env`.
3. Run the appropriate rotation script.
4. Verify that the secret was updated.
5. Never commit `secrets.env` or expose secret values.

Secret values must not appear in Git history, source code, logs, or documentation.