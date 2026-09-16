resource "azurerm_log_analytics_workspace" "applicationbib" {
  name                = var.log_analytics_workspace_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_monitor_workspace" "applicationbib" {
  name                = var.monitor_workspace_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
}
