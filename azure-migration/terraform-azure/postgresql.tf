resource "azurerm_private_dns_zone" "postgres" {
  name                = "private.postgres.database.azure.com"
  resource_group_name = azurerm_resource_group.applicationbib.name
}

resource "azurerm_private_dns_zone_virtual_network_link" "postgres" {
  name                  = "applicationbib-postgres-dns-link"
  private_dns_zone_name = azurerm_private_dns_zone.postgres.name
  resource_group_name   = azurerm_resource_group.applicationbib.name
  virtual_network_id    = azurerm_virtual_network.applicationbib.id
}

resource "azurerm_postgresql_flexible_server" "applicationbib" {
  name                = var.postgres_server_name
  resource_group_name = azurerm_resource_group.applicationbib.name
  location            = azurerm_resource_group.applicationbib.location
  version             = "16"
  delegated_subnet_id = azurerm_subnet.postgres.id
  private_dns_zone_id = azurerm_private_dns_zone.postgres.id

  administrator_login    = var.postgres_admin_username
  administrator_password = var.postgres_admin_password

  storage_mb = 32768
  sku_name   = "B_Standard_B1ms"

  depends_on = [
    azurerm_private_dns_zone_virtual_network_link.postgres
  ]
}

resource "azurerm_postgresql_flexible_server_database" "applicationbib" {
  name      = var.postgres_database_name
  server_id = azurerm_postgresql_flexible_server.applicationbib.id
}
