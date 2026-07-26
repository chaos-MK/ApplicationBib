# Security Findings Log

## Finding #001 — Critical/High: 38 CVEs via outdated Spring Boot parent (tomcat, devtools, actuator, data-jpa, security, test)

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** Critical + High (mixed, 38 total findings)
**Packages affected:** tomcat-embed-jasper, spring-boot-devtools, spring-boot-starter-actuator,
spring-boot-starter-data-jpa, spring-boot-starter-security, spring-boot-starter-test
(all transitively via spring-boot-starter-parent@3.4.3)

### Risk
38 of 57 total Snyk findings traced back to a single root cause: the project
pinned to Spring Boot 3.4.3. Notable issues in this group included:
- Authentication bypass in Actuator (unauthenticated access to management endpoints)
- Missing authentication / cache exposure in Spring Security
- Certificate validation and authentication flaws in embedded Tomcat
- Denial-of-service and resource exhaustion issues in Spring Data

Since all six packages are version-managed by Spring Boot's parent BOM,
one version bump remediates all 38.

### What I did
1. Grouped all Snyk findings by "Upgrade X to fix" root cause instead of
   treating each CVE individually.
2. Upgraded `spring-boot-starter-parent` from 3.4.3 to 3.5.15 in `pom.xml`.
3. Ran `./mvnw clean verify` to confirm no breaking changes.
4. Re-ran the Snyk scan and confirmed these 38 findings no longer appear.

### What changed
- `pom.xml`: `<version>3.4.3</version>` → `<version>3.5.15</version>` (parent)

**Status:** ✅ Fixed


## Finding #002 — High/Critical: 14 CVEs in spring-boot-starter-web (jackson, spring-webmvc, logback)

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** High + Critical (2 critical: jackson-databind RCE + incomplete input validation)
**Package:** org.springframework.boot:spring-boot-starter-web@3.4.3

### Risk
14 issues stemmed from spring-boot-starter-web, including two Critical
findings in jackson-databind — Incomplete List of Disallowed Inputs and
Deserialization of Untrusted Data — which could lead to remote code
execution if the application deserializes attacker-controlled JSON.
Also present: directory traversal and forced-browsing issues in
spring-webmvc, and an expression injection issue in logback-core.

Snyk's suggested fix required spring-boot-starter-web 4.0.0, a major
version bump not covered by the earlier 3.5.15 minor upgrade.

### What I did
1. Reviewed `./mvnw dependency:tree` to confirm no other dependencies were
   hard-pinned to incompatible Spring Framework/Jakarta versions before
   attempting the major-version jump.
2. Upgraded spring-boot-starter-parent from 3.5.15 to 4.0.0.
3. Fixed 6 pre-existing compile errors surfaced by the stricter Boot 4.0
   toolchain — several classes had both a Lombok constructor annotation
   (@RequiredArgsConstructor/@NoArgsConstructor) and a manually written
   constructor with identical parameters, which the newer compiler plugin
   correctly flagged as duplicate constructors. Removed the redundant
   Lombok constructor annotations while keeping the explicit constructors.
4. Ran `./mvnw clean verify` — full test suite (including Postgres
   integration test) passed, jar built and repackaged successfully.
5. Manually verified the application starts and core endpoints respond
   via `./mvnw spring-boot:run`.
6. Re-ran the Snyk scan in the GitLab CI/CD pipeline to confirm all 14
   findings in this group are resolved.

### What changed
- `pom.xml`: `<version>3.5.15</version>` → `<version>4.0.0</version>` (parent)
- 6 Java files: removed duplicate Lombok constructor annotations that
  conflicted with explicitly defined constructors (CohortService,
  ProjectResolver, SessionService, CompanyService,
  ProjectService, CohortDTO.UserDTO)

**Status:** ✅ Fixed


## Finding #003 — High: Incorrect Default Permissions in mysql-connector-j

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** High
**Package:** com.mysql:mysql-connector-j@9.1.0

### Risk
Snyk flagged Incorrect Default Permissions (SNYK-JAVA-COMMYSQL-9725315) in
the MySQL JDBC driver. Not Spring-managed — declared directly in `pom.xml`
with an explicit version, so it required its own bump.

### What I did
1. Updated the explicit dependency version in `pom.xml`.
2. Ran `./mvnw clean verify` to confirm connectivity still works.
3. Re-ran Snyk scan to confirm the finding is resolved.

### What changed
```xml
<!-- before -->
<mysql.version>9.1.0</mysql.version>
<!-- after -->
<mysql.version>9.3.0</mysql.version>
```

**Status:** ✅ Fixed


## Finding #004 — High: 3 CVEs in PostgreSQL JDBC driver

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** High
**Package:** org.postgresql:postgresql@42.7.5

### Risk
Two instances of Incorrect Implementation of Authentication Algorithm and
one Allocation of Resources Without Limits (potential DoS) in the PostgreSQL
JDBC driver. This is the driver used for the app's live database connection,
so an auth-algorithm flaw here is directly relevant to production data access.

### What I did
1. Bumped the explicit driver version.
2. Ran integration tests against the Postgres service container in CI to
   confirm the connection still works post-upgrade.
3. Re-ran Snyk scan to confirm resolution.

### What changed
```xml
<!-- before -->
<postgresql.version>42.7.5</postgresql.version>
<!-- after -->
<postgresql.version>42.7.12</postgresql.version>
```

**Status:** ✅ Fixed


## Finding #005 — High: Uncontrolled Recursion in commons-lang3 (via springdoc-openapi)

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** High
**Package:** org.apache.commons:commons-lang3@3.17.0 (transitive via springdoc-openapi-starter-webmvc-ui@2.1.0)

### Risk
Uncontrolled Recursion (SNYK-JAVA-ORGAPACHECOMMONS-10734078) could allow a
crafted input to trigger a stack overflow / denial of service. Pulled in
transitively through the OpenAPI/Swagger documentation dependency, not a
direct project dependency.

### What I did
1. Upgraded springdoc-openapi-starter-webmvc-ui directly, since it's an
   independent project not covered by the Spring Boot BOM.
2. Confirmed the Swagger UI (`/swagger-ui.html`) still renders correctly
   after the bump.
3. Re-ran Snyk scan to confirm resolution.

### What changed
```xml
<!-- before -->
<springdoc.version>2.1.0</springdoc.version>
<!-- after -->
<springdoc.version>2.8.10</springdoc.version>
```

**Status:** ✅ Fixed