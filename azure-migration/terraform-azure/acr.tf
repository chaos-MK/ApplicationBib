resource "azurerm_container_registry" "applicationbib" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.applicationbib.name
  location            = azurerm_resource_group.applicationbib.location
  sku                 = "Basic"
  admin_enabled       = false
}

resource "azurerm_role_assignment" "aks_acr_pull" {
  scope                = azurerm_container_registry.applicationbib.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_kubernetes_cluster.applicationbib.kubelet_identity[0].object_id
}
