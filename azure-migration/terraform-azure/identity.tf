resource "azurerm_user_assigned_identity" "applicationbib" {
  name                = "applicationbib-workload-id"
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
}

resource "azurerm_role_assignment" "applicationbib_keyvault" {
  scope                = azurerm_key_vault.applicationbib.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_user_assigned_identity.applicationbib.principal_id
}

resource "azurerm_user_assigned_identity" "postgres" {
  name                = var.postgres_identity_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
}

resource "azurerm_role_assignment" "postgres_keyvault" {
  scope                = azurerm_key_vault.applicationbib.id
  role_definition_name = "Key Vault Secrets User"
  principal_id         = azurerm_user_assigned_identity.postgres.principal_id
}

resource "azurerm_federated_identity_credential" "applicationbib" {
  name                = "applicationbib-azure-sa"
  resource_group_name = azurerm_resource_group.applicationbib.name
  parent_id           = azurerm_user_assigned_identity.applicationbib.id

  audience = [
    "api://AzureADTokenExchange"
  ]

  issuer = azurerm_kubernetes_cluster.applicationbib.oidc_issuer_url

  subject = "system:serviceaccount:${var.app_namespace}:${var.app_service_account}"
}

resource "azurerm_federated_identity_credential" "postgres" {
  name                = "postgres-azure-sa"
  resource_group_name = azurerm_resource_group.applicationbib.name
  parent_id           = azurerm_user_assigned_identity.postgres.id

  audience = [
    "api://AzureADTokenExchange"
  ]

  issuer = azurerm_kubernetes_cluster.applicationbib.oidc_issuer_url

  subject = "system:serviceaccount:${var.app_namespace}:postgres-azure-sa"
}
