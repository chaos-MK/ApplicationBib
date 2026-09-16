variable "location" {
  description = "Azure region for the ApplicationBib migration target."
  type        = string
  default     = "westeurope"
}

variable "resource_group_name" {
  description = "Azure resource group for the ApplicationBib migration target."
  type        = string
  default     = "applicationbib-azure-rg"
}

variable "acr_name" {
  description = "Azure Container Registry name."
  type        = string
  default     = "applicationbibacr"
}

variable "aks_name" {
  description = "AKS cluster name."
  type        = string
  default     = "applicationbib-aks"
}

variable "key_vault_name" {
  description = "Azure Key Vault name."
  type        = string
  default     = "applicationbib-kv"
}

variable "log_analytics_workspace_name" {
  description = "Log Analytics workspace name."
  type        = string
  default     = "applicationbib-law"
}

variable "monitor_workspace_name" {
  description = "Azure Monitor workspace name."
  type        = string
  default     = "applicationbib-amw"
}

variable "vnet_name" {
  description = "Virtual network name."
  type        = string
  default     = "applicationbib-vnet"
}

variable "aks_subnet_name" {
  description = "AKS subnet name."
  type        = string
  default     = "aks-subnet"
}

variable "vnet_address_space" {
  description = "Virtual network address space."
  type        = list(string)
  default     = ["10.20.0.0/16"]
}

variable "aks_subnet_address_prefixes" {
  description = "AKS subnet address prefixes."
  type        = list(string)
  default     = ["10.20.0.0/22"]
}

variable "pod_cidr" {
  description = "Pod CIDR used by AKS Azure CNI Overlay."
  type        = string
  default     = "10.244.0.0/16"
}

variable "service_cidr" {
  description = "Kubernetes service CIDR."
  type        = string
  default     = "10.30.0.0/16"
}

variable "dns_service_ip" {
  description = "Kubernetes DNS service IP."
  type        = string
  default     = "10.30.0.10"
}

variable "node_count" {
  description = "Initial AKS node count."
  type        = number
  default     = 1
}

variable "node_vm_size" {
  description = "AKS node VM size."
  type        = string
  default     = "Standard_B2s"
}

variable "app_namespace" {
  description = "Kubernetes namespace used by ApplicationBib."
  type        = string
  default     = "applicationbib"
}

variable "app_service_account" {
  description = "Kubernetes ServiceAccount used by ApplicationBib."
  type        = string
  default     = "applicationbib-azure-sa"
}

variable "postgres_identity_name" {
  description = "Managed identity name reserved for PostgreSQL-related access."
  type        = string
  default     = "applicationbib-postgres-identity"
}

variable "postgres_subnet_name" {
  description = "Delegated subnet for Azure Database for PostgreSQL Flexible Server."
  type        = string
  default     = "postgres-subnet"
}

variable "postgres_subnet_address_prefixes" {
  description = "Address prefixes for the PostgreSQL delegated subnet."
  type        = list(string)
  default     = ["10.20.4.0/24"]
}

variable "postgres_server_name" {
  description = "Azure Database for PostgreSQL Flexible Server name."
  type        = string
  default     = "applicationbib-postgres"
}

variable "postgres_database_name" {
  description = "Application database name used by the Azure PostgreSQL target."
  type        = string
  default     = "applicationbib"
}

variable "postgres_admin_username" {
  description = "Administrator username for the PostgreSQL target. Provide the real value only at deployment time."
  type        = string
  default     = "applicationbib_admin"
}

variable "postgres_admin_password" {
  description = "Administrator password for the PostgreSQL target. Never commit a real password."
  type        = string
  sensitive   = true
  default     = "CHANGE_ME_AT_DEPLOYMENT"
}
