#!/usr/bin/env bash
set -euo pipefail

source "$(dirname "$0")/azure.env"

echo "=== ApplicationBib Azure Key Vault Secrets ==="
echo
echo "The following secrets are required in Azure Key Vault:"
echo
echo "  firebase-service-account"
echo "  postgres-db"
echo "  postgres-username"
echo "  postgres-password"
echo
echo "These values must NOT be committed to Git."
echo "Populate them only after Azure Key Vault is available."
echo
echo "Example commands:"
echo
echo 'az keyvault secret set --vault-name "$AZ_KEYVAULT_NAME" --name "firebase-service-account" --value "$(cat <secure-file>)"'
echo 'az keyvault secret set --vault-name "$AZ_KEYVAULT_NAME" --name "postgres-db" --value "<value>"'
echo 'az keyvault secret set --vault-name "$AZ_KEYVAULT_NAME" --name "postgres-username" --value "<value>"'
echo 'az keyvault secret set --vault-name "$AZ_KEYVAULT_NAME" --name "postgres-password" --value "<value>"'
