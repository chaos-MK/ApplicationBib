# ApplicationBib — Azure Ansible Deployment

This directory contains the Ansible deployment layer for the ApplicationBib
Azure migration.

The playbook is a **static deployment artifact** and is not executed against
the existing local Minikube environment.

## Architecture

Ansible runs from a deployment controller and communicates with the Kubernetes
API of the target AKS cluster.

    Deployment Controller
            |
            | Kubernetes API
            v
          Azure AKS
            |
            +-- applicationbib namespace
            |     |
            |     +-- ApplicationBib backend
            |     +-- Ritual Growth frontend
            |     +-- ClusterIP services
            |     +-- Ingress / TLS
            |     +-- ServiceMonitor
            |
            +-- Azure Container Registry
            |
            +-- Azure PostgreSQL
            |
            +-- Azure Key Vault

## Roles

| Role         | Responsibility                              |
|--------------|----------------------------------------------|
| namespace    | Creates the ApplicationBib Kubernetes namespace |
| application  | Deploys frontend, backend, and services       |
| ingress      | Configures HTTPS ingress and routing          |
| monitoring   | Configures Prometheus service discovery       |

## Required Azure resources

The target environment is expected to provide:

- Azure Resource Group
- Azure Kubernetes Service (AKS)
- Azure Container Registry (ACR)
- Azure Database for PostgreSQL
- Azure Key Vault
- DNS/hostname for the application
- TLS certificate/secret
- Monitoring stack compatible with ServiceMonitor

## Container images

The application role expects:

- applicationbibacr.azurecr.io/applicationbib:IMAGE_TAG
- applicationbibacr.azurecr.io/ritual-growth-ui:IMAGE_TAG

Images should be published to ACR before deployment.

## Secrets

Real credentials must not be stored in:

- group_vars/all.yml
- Kubernetes templates
- Git history
- inventory files

Database credentials and other sensitive values should be provided through
Azure Key Vault and the Kubernetes secret integration used by the target AKS
environment.

## Deployment

The intended deployment flow is:

    Build images
        |
        v
    Push images to ACR
        |
        v
    Provision Azure infrastructure
        |
        v
    Configure AKS access
        |
        v
    Run Ansible
        |
        v
    Create namespace
        |
        v
    Deploy application
        |
        v
    Configure ingress
        |
        v
    Configure monitoring

The playbook should only be executed after the target Azure environment has
been provisioned and configured.

## Current status

This is a migration/deployment preparation artifact.

No Azure resources are provisioned by this Ansible directory itself.
