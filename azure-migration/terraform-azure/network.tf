resource "azurerm_virtual_network" "applicationbib" {
  name                = var.vnet_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
  address_space       = var.vnet_address_space
}

resource "azurerm_network_security_group" "aks" {
  name                = "${var.aks_name}-nsg"
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
}

resource "azurerm_subnet" "aks" {
  name                 = var.aks_subnet_name
  resource_group_name  = azurerm_resource_group.applicationbib.name
  virtual_network_name = azurerm_virtual_network.applicationbib.name
  address_prefixes     = var.aks_subnet_address_prefixes
}

resource "azurerm_subnet_network_security_group_association" "aks" {
  subnet_id                 = azurerm_subnet.aks.id
  network_security_group_id = azurerm_network_security_group.aks.id
}

resource "azurerm_subnet" "postgres" {
  name                 = var.postgres_subnet_name
  resource_group_name  = azurerm_resource_group.applicationbib.name
  virtual_network_name = azurerm_virtual_network.applicationbib.name
  address_prefixes     = var.postgres_subnet_address_prefixes

  delegation {
    name = "postgres-flexible-server"

    service_delegation {
      name = "Microsoft.DBforPostgreSQL/flexibleServers"

      actions = [
        "Microsoft.Network/virtualNetworks/subnets/join/action"
      ]
    }
  }
}
