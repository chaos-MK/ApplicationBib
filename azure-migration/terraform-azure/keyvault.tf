data "azurerm_client_config" "current" {}

resource "azurerm_key_vault" "applicationbib" {
  name                = var.key_vault_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
  tenant_id           = data.azurerm_client_config.current.tenant_id

  sku_name = "standard"

  purge_protection_enabled   = true
  soft_delete_retention_days = 7

  public_network_access_enabled = true
  rbac_authorization_enabled    = true
}
