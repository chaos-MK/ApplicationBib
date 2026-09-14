#!/usr/bin/env bash
set -euo pipefail

source "$(dirname "$0")/azure.env"

echo "=== ApplicationBib Azure Infrastructure ==="
echo "Region:         $AZ_LOCATION"
echo "Resource Group: $AZ_RESOURCE_GROUP"
echo "VNet:            $AZ_VNET_NAME"
echo "AKS:             $AZ_AKS_NAME"
echo "ACR:             $AZ_ACR_NAME"
echo "Key Vault:       $AZ_KEYVAULT_NAME"

echo
echo "=== 1. Resource Group ==="

az group create \
  --name "$AZ_RESOURCE_GROUP" \
  --location "$AZ_LOCATION"

echo
echo "=== 2. Virtual Network ==="

az network vnet create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_VNET_NAME" \
  --location "$AZ_LOCATION" \
  --address-prefix "$AZ_VNET_ADDRESS" \
  --subnet-name "$AZ_AKS_SUBNET_NAME" \
  --subnet-prefix "$AZ_AKS_SUBNET_PREFIX"

echo
echo "=== 3. Network Security Group ==="

az network nsg create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "applicationbib-aks-nsg" \
  --location "$AZ_LOCATION"

echo

echo
echo "=== 3b. Associate NSG with AKS subnet ==="

az network vnet subnet update \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --vnet-name "$AZ_VNET_NAME" \
  --name "$AZ_AKS_SUBNET_NAME" \
  --network-security-group "applicationbib-aks-nsg"

echo
echo "=== 4. Azure Container Registry ==="

az acr create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_ACR_NAME" \
  --location "$AZ_LOCATION" \
  --sku Basic \
  --admin-enabled false

echo
echo "Container Registry creation complete."

echo
echo "=== 5. Azure Kubernetes Service ==="

az aks create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_AKS_NAME" \
  --location "$AZ_LOCATION" \
  --node-count 1 \
  --node-vm-size Standard_B2s \
  --network-plugin azure \
  --network-plugin-mode overlay \
  --vnet-subnet-id "$(az network vnet subnet show \
    --resource-group "$AZ_RESOURCE_GROUP" \
    --vnet-name "$AZ_VNET_NAME" \
    --name "$AZ_AKS_SUBNET_NAME" \
    --query id -o tsv)" \
  --pod-cidr "$AZ_POD_CIDR" \
  --service-cidr "$AZ_SERVICE_CIDR" \
  --dns-service-ip "$AZ_DNS_SERVICE_IP" \
  --enable-managed-identity \
  --enable-oidc-issuer \
  --enable-workload-identity \
  --enable-addons azure-keyvault-secrets-provider,monitoring \
  --workspace-resource-id "$(az monitor log-analytics workspace show \
    --resource-group "$AZ_RESOURCE_GROUP" \
    --workspace-name "$AZ_LOG_WORKSPACE_NAME" \
    --query id -o tsv)" \
  --enable-azure-monitor-metrics \
  --azure-monitor-workspace-resource-id "$(az monitor account show \
    --resource-group "$AZ_RESOURCE_GROUP" \
    --name "$AZ_MONITOR_WORKSPACE_NAME" \
    --query id -o tsv)" \
  --enable-aad \
  --enable-azure-rbac \
  --attach-acr "$AZ_ACR_NAME" \
  --generate-ssh-keys

echo
echo "AKS creation complete."

echo
echo "=== 6. Azure Key Vault ==="

az keyvault create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_KEYVAULT_NAME" \
  --location "$AZ_LOCATION" \
  --enable-rbac-authorization true

echo
echo "Azure Key Vault creation complete."

echo
echo "=== 7. Log Analytics Workspace ==="

az monitor log-analytics workspace create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --workspace-name "$AZ_LOG_WORKSPACE_NAME" \
  --location "$AZ_LOCATION"

echo
echo "Log Analytics workspace creation complete."

echo
echo "=== 7b. Azure Monitor Workspace ==="

az monitor account create   --resource-group "$AZ_RESOURCE_GROUP"   --name "$AZ_MONITOR_WORKSPACE_NAME"   --location "$AZ_LOCATION"

echo
echo "Azure Monitor workspace creation complete."

echo
echo "=== 6b. Application Workload Managed Identity ==="

az identity create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_WORKLOAD_IDENTITY_NAME" \
  --location "$AZ_LOCATION"

echo
echo "Application workload managed identity creation complete."

echo
echo "=== PostgreSQL Managed Identity ==="

az identity create   --resource-group "$AZ_RESOURCE_GROUP"   --name "applicationbib-postgres-identity"   --location "$AZ_LOCATION"

AZ_POSTGRES_CLIENT_ID="$(az identity show   --resource-group "$AZ_RESOURCE_GROUP"   --name "applicationbib-postgres-identity"   --query clientId -o tsv)"

echo
echo "PostgreSQL managed identity created."

echo
echo "=== Grant Key Vault Secrets User to PostgreSQL Identity ==="

az role assignment create   --assignee "$(az identity show     --resource-group "$AZ_RESOURCE_GROUP"     --name "applicationbib-postgres-identity"     --query principalId -o tsv)"   --role "Key Vault Secrets User"   --scope "$(az keyvault show     --resource-group "$AZ_RESOURCE_GROUP"     --name "$AZ_KEYVAULT_NAME"     --query id -o tsv)"

echo
echo "PostgreSQL Key Vault RBAC assignment complete."

echo
echo "=== 8. Grant Key Vault Secrets User to Application Identity ==="

az role assignment create \
  --assignee "$(az identity show \
    --resource-group "$AZ_RESOURCE_GROUP" \
    --name "$AZ_WORKLOAD_IDENTITY_NAME" \
    --query principalId -o tsv)" \
  --role "Key Vault Secrets User" \
  --scope "$(az keyvault show \
    --resource-group "$AZ_RESOURCE_GROUP" \
    --name "$AZ_KEYVAULT_NAME" \
    --query id -o tsv)"

echo
echo "Key Vault RBAC assignment complete."

echo
echo "=== 9. Federated Identity Credential ==="

AZ_WORKLOAD_CLIENT_ID="$(az identity show \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_WORKLOAD_IDENTITY_NAME" \
  --query clientId -o tsv)"

AZ_AKS_OIDC_ISSUER="$(az aks show \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --name "$AZ_AKS_NAME" \
  --query oidcIssuerProfile.issuerUrl -o tsv)"

az identity federated-credential create \
  --resource-group "$AZ_RESOURCE_GROUP" \
  --identity-name "$AZ_WORKLOAD_IDENTITY_NAME" \
  --name "applicationbib-federated-credential" \
  --issuer "$AZ_AKS_OIDC_ISSUER" \
  --subject "system:serviceaccount:${AZ_APP_NAMESPACE}:${AZ_APP_SERVICE_ACCOUNT}" \
  --audiences "api://AzureADTokenExchange"

echo
echo "Federated identity credential creation complete."

echo
echo "=== PostgreSQL Federated Identity Credential ==="

AZ_POSTGRES_OIDC_ISSUER="$(az aks show   --resource-group "$AZ_RESOURCE_GROUP"   --name "$AZ_AKS_NAME"   --query oidcIssuerProfile.issuerUrl -o tsv)"

az identity federated-credential create   --resource-group "$AZ_RESOURCE_GROUP"   --identity-name "applicationbib-postgres-identity"   --name "applicationbib-postgres-federated-credential"   --issuer "$AZ_POSTGRES_OIDC_ISSUER"   --subject "system:serviceaccount:${AZ_APP_NAMESPACE}:postgres-azure-sa"   --audiences "api://AzureADTokenExchange"

echo
echo "PostgreSQL federated identity credential creation complete."
