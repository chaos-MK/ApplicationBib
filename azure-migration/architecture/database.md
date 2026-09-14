# Azure PostgreSQL Migration

## Current architecture

The existing ApplicationBib deployment runs PostgreSQL inside Kubernetes.

```text
Spring Boot
    |
    | TCP 5432
    v
PostgreSQL Service
    |
    v
PostgreSQL StatefulSet
    |
    v
PersistentVolumeClaim
```

The PostgreSQL database is currently part of the Kubernetes application environment.

Database credentials are injected through the existing Vault architecture.

---

## Azure target architecture

The Azure migration moves PostgreSQL from Kubernetes to **Azure Database for PostgreSQL Flexible Server**.

```text
AKS
 |
 | private database connection
 v
Azure Database for PostgreSQL
Flexible Server
 |
 v
Managed Azure storage
```

The application architecture becomes:

```text
Internet
    |
    v
NGINX Ingress
    |
    v
ApplicationBib
    |
    | PostgreSQL connection
    v
Azure Database for PostgreSQL
Flexible Server
```

The database is no longer required to run as a PostgreSQL StatefulSet inside AKS.

---

## Migration mapping

| Current local architecture | Azure target |
|---|---|
| PostgreSQL StatefulSet | Azure Database for PostgreSQL Flexible Server |
| PostgreSQL Service | Azure PostgreSQL endpoint |
| Kubernetes PVC | Azure managed database storage |
| PostgreSQL credentials in Vault | PostgreSQL credentials in Azure Key Vault |
| PostgreSQL exporter | Azure Monitor PostgreSQL metrics |
| PostgreSQL pod | Managed Azure database service |
| Kubernetes database lifecycle | Azure-managed database lifecycle |

---

## Application connection

The Spring Boot application continues to use PostgreSQL.

The main change is the database endpoint.

Current:

```text
Spring Boot
    |
    v
postgres:5432
```

Azure:

```text
Spring Boot
    |
    v
<azure-postgresql-server>:5432
```

The application should not connect to the database through the public Internet when a private Azure networking design is used.

---

## Azure networking

The target architecture places the database behind Azure networking controls.

```text
Azure VNet
|
+-- AKS subnet
|      |
|      +-- ApplicationBib Pods
|
+-- PostgreSQL delegated subnet
       |
       +-- Azure Database for PostgreSQL
```

The database should accept connections only from the application networking path.

The exact subnet and private DNS configuration are deployment details for the future Azure implementation.

No public database exposure is assumed.

---

## Database authentication

The migration keeps database credentials out of Kubernetes manifests.

Target flow:

```text
Azure Key Vault
      |
      | secret retrieval
      v
ApplicationBib Pod
      |
      | database credentials
      v
Spring Boot
      |
      | authenticated PostgreSQL connection
      v
Azure Database for PostgreSQL
```

The required PostgreSQL secrets remain:

```text
POSTGRES_DB
POSTGRES_USER
POSTGRES_PASSWORD
```

Actual values must never be committed to Git.

The Azure migration already defines these secrets for Key Vault:

```text
postgres-db
postgres-username
postgres-password
```

---

## PostgreSQL monitoring

The current local architecture uses a PostgreSQL exporter:

```text
PostgreSQL
    |
    v
PostgreSQL Exporter
    |
    v
Prometheus
    |
    v
Grafana
```

The Azure target can use Azure-native PostgreSQL monitoring:

```text
Azure Database for PostgreSQL
            |
            v
Azure Monitor
            |
            +--> Metrics
            |
            +--> Logs
            |
            v
Azure Monitor dashboards / alerts
```

The PostgreSQL exporter is therefore not required when the migration uses Azure-native database monitoring.

The existing local PostgreSQL exporter remains untouched.

---

## Backup and availability model

The current Kubernetes PostgreSQL deployment is responsible for its own persistence through Kubernetes storage.

With Azure Database for PostgreSQL Flexible Server, database infrastructure is managed by Azure.

The migration therefore moves database operational responsibilities from:

```text
ApplicationBib / Kubernetes
```

to:

```text
Azure Database for PostgreSQL
```

This provides an Azure-managed database service rather than maintaining the PostgreSQL server lifecycle inside AKS.

Backup, retention, availability, and disaster-recovery settings should be explicitly configured during a real Azure deployment.

They are not configured by this migration lab.

---

## Security boundary

The database becomes a separate managed-service trust boundary:

```text
+-----------------------------+
| Azure Kubernetes Environment|
|                             |
|  Spring Boot                |
|       |                     |
+-------|---------------------+
        |
        | authenticated DB connection
        |
+-------v---------------------+
| Azure Database for          |
| PostgreSQL Flexible Server  |
+-----------------------------+
```

The database should not be directly accessible from the public Internet.

---

## Migration sequence

A future real migration would follow approximately this sequence:

```text
1. Provision Azure PostgreSQL Flexible Server
              |
              v
2. Configure networking / private access
              |
              v
3. Create database and database user
              |
              v
4. Store credentials in Azure Key Vault
              |
              v
5. Export data from existing PostgreSQL
              |
              v
6. Import data into Azure PostgreSQL
              |
              v
7. Update Spring Boot database configuration
              |
              v
8. Deploy application to AKS
              |
              v
9. Verify database connectivity
              |
              v
10. Verify application functionality
```

None of these migration operations are performed by this lab.

---

## What changes

The Azure migration changes the database infrastructure:

```text
PostgreSQL inside Kubernetes
        ↓
Azure Database for PostgreSQL
```

The application remains:

```text
Next.js / React
        |
        v
Spring Boot
        |
        v
PostgreSQL
```

The database technology itself remains PostgreSQL.

---

## What does not change

The migration does not require:

- changing Spring Boot business logic
- changing Firebase Authentication
- changing the frontend authentication model
- changing the PostgreSQL data model
- changing the existing local Kubernetes manifests
- changing the existing Terraform configuration
- modifying the original ApplicationBib project

The Azure database work is isolated inside `azure-migration/`.

---

## Current status

- Azure PostgreSQL migration is documented.
- Azure Database for PostgreSQL Flexible Server is the target database service.
- Existing Kubernetes PostgreSQL remains untouched.
- Existing PostgreSQL StatefulSet remains untouched.
- Existing PostgreSQL PVC remains untouched.
- Existing PostgreSQL exporter remains untouched.
- Azure PostgreSQL credentials are planned for Azure Key Vault.
- No Azure database has been created.
- No application database has been migrated.
- No production data has been copied.
