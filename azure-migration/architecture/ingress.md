# Azure Ingress Migration

## Current local architecture

The existing ApplicationBib deployment uses NGINX Ingress inside Kubernetes.

External traffic reaches the Kubernetes ingress and is routed to application services.

## Azure target

The Azure migration retains NGINX Ingress as the Kubernetes ingress layer.

Target flow:

```text
Internet
   |
   v
Azure public endpoint / Load Balancer
   |
   v
NGINX Ingress Controller
   |
   +--------------------+
   |                    |
 /api/*                /*
   |                    |
   v                    v
ApplicationBib       Ritual Growth
Service :8080        Service :3000
   |                    |
   v                    v
Spring Boot          Next.js / React
```

## Routing

```text
https://<AZURE_APPLICATION_HOST>/
        |
        v
Ritual Growth frontend
Service :3000
```

```text
https://<AZURE_APPLICATION_HOST>/api/*
        |
        v
ApplicationBib backend
Service :8080
```

The frontend uses:

```text
NEXT_PUBLIC_API_BASE_URL=/api
```

This keeps the frontend and backend under the same HTTPS origin and avoids requiring a separate API hostname or CORS configuration.

## TLS

TLS is terminated at the NGINX Ingress layer.

The ingress references:

```text
Secret: applicationbib-tls
```

The TLS certificate and secret are part of the Azure deployment design.

## Kubernetes services

The Azure migration uses:

```text
NGINX Ingress Controller
        |
        +-----------------------------+
        |                             |
        v                             v
Ritual Growth Service           ApplicationBib Service
       :3000                           :8080
        |                             |
        v                             v
Next.js / React                  Spring Boot
```

Both services are located in the `applicationbib` namespace.

## Migration notes

The existing application architecture is retained.

The migration changes the infrastructure surrounding the application:

```text
Local Minikube
     |
     v
Azure AKS
```

The local NGINX Ingress architecture is retained as the Kubernetes ingress layer.

The external Azure path becomes:

```text
Internet
   |
   v
Azure public endpoint / Load Balancer
   |
   v
NGINX Ingress Controller
   |
   +----------------------+
   |                      |
   v                      v
Frontend                 Backend
:3000                    :8080
```

The backend remains responsible for Firebase authentication and PostgreSQL access.

The frontend continues using the Firebase Web SDK.

Firebase Authentication remains separate from the Azure infrastructure.

## Deployment status

This document describes the **Azure migration target architecture only**.

It does not mean that:

- Azure resources have been deployed
- AKS has been created
- NGINX Ingress has been installed
- the application has been deployed to Azure
- the TLS secret has been created
- the public endpoint exists

These resources are documented as the target migration architecture and have not been deployed.
