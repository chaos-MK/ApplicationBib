resource "azurerm_kubernetes_cluster" "applicationbib" {
  name                = var.aks_name
  location            = azurerm_resource_group.applicationbib.location
  resource_group_name = azurerm_resource_group.applicationbib.name
  dns_prefix          = "applicationbib"

  sku_tier = "Free"

  default_node_pool {
    name           = "system"
    node_count     = var.node_count
    vm_size        = var.node_vm_size
    vnet_subnet_id = azurerm_subnet.aks.id
  }

  identity {
    type = "SystemAssigned"
  }

  oidc_issuer_enabled       = true
  workload_identity_enabled = true

  azure_policy_enabled = true

  network_profile {
    network_plugin      = "azure"
    network_plugin_mode = "overlay"

    pod_cidr          = var.pod_cidr
    service_cidr      = var.service_cidr
    dns_service_ip    = var.dns_service_ip
    load_balancer_sku = "standard"
  }

  azure_active_directory_role_based_access_control {
    azure_rbac_enabled = true
    tenant_id          = data.azurerm_client_config.current.tenant_id
  }

  key_vault_secrets_provider {
    secret_rotation_enabled = true
  }

  oms_agent {
    log_analytics_workspace_id = azurerm_log_analytics_workspace.applicationbib.id
  }

  monitor_metrics {
    annotations_allowed = null
    labels_allowed      = null
  }

  depends_on = [
    azurerm_subnet_network_security_group_association.aks
  ]
}
