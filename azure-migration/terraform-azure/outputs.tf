output "resource_group_name" {
  description = "ApplicationBib Azure resource group."
  value       = azurerm_resource_group.applicationbib.name
}

output "aks_name" {
  description = "ApplicationBib AKS cluster name."
  value       = azurerm_kubernetes_cluster.applicationbib.name
}

output "acr_login_server" {
  description = "Azure Container Registry login server."
  value       = azurerm_container_registry.applicationbib.login_server
}

output "key_vault_uri" {
  description = "Azure Key Vault URI."
  value       = azurerm_key_vault.applicationbib.vault_uri
}

output "application_identity_client_id" {
  description = "Client ID of the ApplicationBib workload identity."
  value       = azurerm_user_assigned_identity.applicationbib.client_id
}

output "postgres_server_name" {
  description = "Azure Database for PostgreSQL Flexible Server name."
  value       = azurerm_postgresql_flexible_server.applicationbib.name
}

output "postgres_database_name" {
  description = "ApplicationBib PostgreSQL database name."
  value       = azurerm_postgresql_flexible_server_database.applicationbib.name
}

output "monitor_workspace_id" {
  description = "Azure Monitor Workspace resource ID."
  value       = azurerm_monitor_workspace.applicationbib.id
}

output "log_analytics_workspace_id" {
  description = "Log Analytics Workspace resource ID."
  value       = azurerm_log_analytics_workspace.applicationbib.id
}
