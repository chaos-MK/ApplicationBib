# Security Findings Log

This log documents security and reliability findings identified by
manual and automated scanning tools (Gitleaks, Semgrep, SonarQube, Snyk, Hadolint,
SpotBugs, Syft, Trivy, Grype, kubeconform, kube-score, and OWASP ZAP)
and manual architectural reviews, along with the remediation applied for
each. Most tools run automatically in the GitLab CI/CD pipeline, while
manual reviews are used to identify architectural and design-level
security issues that automated scanners cannot detect.

For DAST alterts : The ZAP alert reference for IDs: https://www.zaproxy.org/docs/alerts/


+Injection (most critical for an API with a real database):

40018 — SQL Injection (generic)

40022 — SQL Injection, PostgreSQL-specific (matches your actual DB)

90019 — Server Side Code Injection

90020 — Remote OS Command Injection

90025 — Expression Language Injection (relevant to Spring, which uses SpEL internally)

90023 — XML External Entity (XXE) Attack


+Info disclosure that matters for an auth-gated API:

90022 — Application Error Disclosure (stack traces leaking internals — you already saw this PASS, worth keeping it a hard gate rather than just a pass)

10062 — PII Disclosure (relevant since this is a library/book-tracking app likely handling user data)


+Cross-site scripting (lower priority for a pure JSON API, but worth including if any endpoint ever reflects input):

40012 — Cross Site Scripting (Reflected)





---

## Finding #001 — Critical/High: 38 CVEs via outdated Spring Boot parent version

**Date:** 2026-07-25
**Tool that found it:** Snyk
**Severity:** Critical + High (38 findings)
**Packages affected:** tomcat-embed-jasper, spring-boot-devtools,
spring-boot-starter-actuator, spring-boot-starter-data-jpa,
spring-boot-starter-security, spring-boot-starter-test
(all transitively via spring-boot-starter-parent@3.4.3)

### Risk
38 of 57 total Snyk findings traced back to a single root cause: the project
was pinned to Spring Boot 3.4.3. Notable issues in this group:
- Authentication bypass in Spring Boot Actuator — could allow unauthenticated
  access to management endpoints (health, env, metrics).
- Missing authentication / cache exposure in Spring Security.
- Certificate validation and authentication flaws in embedded Tomcat.
- Denial-of-service and resource exhaustion issues in Spring Data.

All six packages are version-managed by Spring Boot's parent BOM, so one
version bump remediated all 38 at once.

### What I did
1. Grouped Snyk findings by "Upgrade X to fix" root cause instead of
   treating each CVE individually.
2. Upgraded `spring-boot-starter-parent` from 3.4.3 to 3.5.15.
3. Ran `./mvnw clean verify` to confirm no breaking changes.
4. Re-ran Snyk and confirmed all 38 findings resolved.

### What changed
- `pom.xml`: `<version>3.4.3</version>` → `<version>3.5.15</version>` (parent)

**Status:** Fixed


## Finding #002 — High/Critical: 14+ CVEs in spring-boot-starter-web (jackson, spring-webmvc, tomcat) — required major version migration

**Date:** 2026-07-25 → 2026-07-26 (multi-day fix, tracked as one entry due to
a shared root cause and iterative remediation)
**Tool that found it:** Snyk
**Severity:** Critical + High
**Package:** org.springframework.boot:spring-boot-starter-web

### Risk
14 issues stemmed from spring-boot-starter-web, including two Critical
findings in jackson-databind — Incomplete List of Disallowed Inputs and
Deserialization of Untrusted Data — which could lead to remote code
execution if the application deserializes attacker-controlled JSON. Also
present: directory traversal and forced-browsing issues in spring-webmvc,
and an expression injection issue in logback-core.

Unlike Finding #001, Snyk's fix required spring-boot-starter-web **4.0.0**
— a major version not covered by a minor parent bump, since starter-web's
managed version tracked the 3.x line.

### What I did — full remediation path
1. **Compatibility check:** reviewed `./mvnw dependency:tree` and confirmed
   Java 21 and no other dependencies were hard-pinned to a version
   incompatible with Boot 4.
2. **Major version upgrade:** bumped `spring-boot-starter-parent` from
   3.5.15 to 4.0.0.
3. **Fixed pre-existing code defects surfaced by the stricter Boot 4.0
   compiler toolchain:** 6 classes had both a Lombok-generated constructor
   (`@RequiredArgsConstructor` / `@NoArgsConstructor`) and a manually
   written constructor with identical parameters — previously tolerated,
   now correctly flagged as duplicate constructors. Removed the redundant
   manual constructors (CohortService, ProjectResolver, SessionService,
   CompanyService, ProjectService, CohortDTO.UserDTO).
4. **New CVEs surfaced on 4.0.0 itself:** since Boot 4.0.0 was a very
   recent major release, Snyk immediately flagged 50 issues against it —
   mostly the same package family (tomcat-embed-jasper, devtools,
   actuator, security, springdoc, spring-boot-starter-web again) now
   fixed in Boot's own subsequent patch releases. Bumped parent to 4.0.7.
5. **springdoc-openapi major version migration:** identified that
   springdoc-openapi 2.8.x only supports Spring Boot 3.x; Boot 4.x
   requires springdoc's new 3.x line (Jackson 3–based). Upgraded
   `springdoc-openapi-starter-webmvc-ui` from 2.8.10 to 3.0.3, which
   resolved a `ClassNotFoundException` (`WebMvcProperties`) caused by
   the version mismatch and fixed the app context failing to load in
   tests.
6. **Residual Jackson CVEs:** after the above, two Jackson findings
   remained across two different dependency lines — legacy Jackson 2.x
   (pulled in by springdoc/swagger) and Boot 4's native Jackson 3.x
   (`tools.jackson`). Pinned both explicitly via `<dependencyManagement>`
   since no single parent property covered both simultaneously.
7. **Residual Tomcat CVE:** `tomcat-embed-jasper` was bumped to the
   patched version, but its transitive `tomcat-embed-core` dependency
   still resolved to an older, vulnerable version. Forced the correct
   version directly via `<dependencyManagement>`.
8. Ran `./mvnw clean verify` after each change (full test suite,
   including the Postgres integration test) to catch regressions early.
9. Manually verified the app starts and core endpoints/Swagger UI respond
   via `./mvnw spring-boot:run`.
10. Re-ran the Snyk scan in CI after each round until the finding count
    reached 0.

### What changed
- `pom.xml`:
  - Parent: `3.4.3` → `4.0.0` → `4.0.7`
  - `springdoc-openapi-starter-webmvc-ui`: `2.8.10` → `3.0.3`
  - `tomcat-embed-jasper`: explicit version pinned to `11.0.23`
  - Added `<dependencyManagement>` overrides:
    - `com.fasterxml.jackson.core:jackson-databind` → `2.21.5`
    - `tools.jackson.core:jackson-databind` → `3.1.5`
    - `org.apache.tomcat.embed:tomcat-embed-core` → `11.0.23`
  - Added `<properties><logback.version>1.5.36</logback.version></properties>`
    to close a logback expression-injection CVE with no upstream Spring
    Boot patch yet available.
- 6 Java files: removed duplicate constructors conflicting with Lombok.

### Lessons learned (worth keeping for the interview writeup)
- A major-version dependency bump can temporarily *increase* the number of
  findings before it decreases them, since brand-new releases haven't
  been patched yet — verify against the latest patch version, not just
  the latest major version.
- Not every CVE closes with a single parent-BOM bump; transitive
  dependencies (tomcat-embed-core here) can lag behind their own parent
  artifact and need to be pinned explicitly.
- Migrating one major dependency (Boot 4) can force a cascading major
  migration in an unrelated dependency (springdoc 2.x → 3.x) due to a
  shared transitive dependency (Jackson).

**Status:** Fixed — Snyk reports 0 findings at `--severity-threshold=high` as of 2026-07-26.


## Finding #003 — High: OS-level CVEs in container base image (sqlite-libs) + unnecessary attack surface (gnupg)

**Date:** 2026-07-26
**Tool that found it:** Trivy, Grype (cross-validated by both scanners)
**Severity:** High (sqlite-libs) + Low/Medium (gnupg, coreutils, busybox)
**Package:** eclipse-temurin:21-jre-alpine base image — sqlite-libs, gnupg suite, coreutils, busybox

### Risk
Trivy and Grype both flagged outdated OS packages baked into the
`eclipse-temurin:21-jre-alpine` base image, not introduced by application
code. Two High-severity findings in `sqlite-libs` (CVE-2026-11822,
CVE-2026-11824) were the primary blockers. Additionally, the image shipped
a full `gnupg` suite (10+ packages) that a runtime JRE application has no
functional need for — unused software in a container image is unnecessary
attack surface, since any CVE in it is exposure with no offsetting benefit.

### What I did
1. Cross-checked the finding with two independent scanners (Trivy and
   Grype) to confirm it wasn't a tool-specific false positive before
   investigating further — initially suspected a scanning misconfiguration
   until a debug step confirmed the image and findings were both correct.
2. Added `apk update && apk upgrade --no-cache` to the Dockerfile's runtime
   stage to patch all available OS package updates at build time.
3. Removed the entire `gnupg` package group via `apk del`, since it is not
   required for the application to run and reduces the image's attack
   surface regardless of its own CVE status.
4. Re-built and re-scanned with both Trivy and Grype to confirm the High
   severity findings were resolved.

### What changed
`Dockerfile` (runtime stage):
```dockerfile
FROM eclipse-temurin:21-jre-alpine
RUN apk update && apk upgrade --no-cache && \
    apk del --no-cache gnupg gnupg-dirmngr gnupg-gpgconf gnupg-keyboxd \
    gnupg-utils gnupg-wks-client gpg gpg-agent gpg-wks-server gpgsm gpgv \
    2>/dev/null || true
```

**Status:** Fixed — Trivy and Grype both pass at `--fail-on high` threshold.


## Finding #004 — False Positive: Gitleaks flagged sonar.projectKey as a secret

**Date:** 2026-07-27
**Tool that found it:** Gitleaks
**Severity:** N/A (false positive)

### Risk
None. Gitleaks' `generic-api-key` rule matched `sonar.projectKey=...` in
`sonar-project.properties` purely due to the substring "key" combined with
high entropy in the value. `projectKey` is a public SonarCloud project
identifier, not a credential — it is also visible in the SonarCloud
dashboard URL itself.

### What I did
1. Verified the flagged value was not sensitive (project identifier, not
   an API token or password).
2. Added the specific finding fingerprint to `.gitleaksignore` rather than
   disabling the Gitleaks job or excluding the whole file, so real secrets
   in this file (if ever added) would still be caught.

### What changed
- Added `.gitleaksignore` with the specific finding fingerprint.

**Status:** Resolved (documented false positive)


## Finding #005 — High: Transitive dependency CVEs introduced by HashiCorp Vault integration

**Date:** 2026-07-30
**Tool that found it:** Snyk
**Severity:** High (4 findings)
**Package:** org.springframework.cloud:spring-cloud-starter-vault-config

### Risk
Adding `spring-cloud-starter-vault-config` to integrate HashiCorp Vault
introduced two vulnerable transitive dependencies:

- `org.apache.httpcomponents.core5:httpcore5-h2@5.3.6`
  - Allocation of Resources Without Limits or Throttling
  - SNYK-JAVA-ORGAPACHEHTTPCOMPONENTSCORE5-17817217
  - SNYK-JAVA-ORGAPACHEHTTPCOMPONENTSCORE5-17817218
- `org.bouncycastle:bcprov-jdk18on@1.81.1`
  - Timing Attack
  - Use of a Broken or Risky Cryptographic Algorithm
  - SNYK-JAVA-ORGBOUNCYCASTLE-16074612
  - SNYK-JAVA-ORGBOUNCYCASTLE-16075266

The application did not depend on either library directly; both were pulled
in transitively by the Vault starter. Without intervention, the vulnerable
versions would continue to be resolved during the Maven build.

### What I did
1. Used the Snyk report to identify the vulnerable transitive dependency
   chain introduced by `spring-cloud-starter-vault-config`.
2. Added explicit version overrides in `<dependencyManagement>` so Maven
   resolves the patched releases instead of the vulnerable transitive
   versions.
3. Upgraded:
   - `httpcore5-h2` from `5.3.6` to `5.4.3`
   - `bcprov-jdk18on` from `1.81.1` to `1.84`
4. Ran `./mvnw clean verify` to ensure the dependency overrides introduced
   no compatibility issues.
5. Re-ran `snyk test --severity-threshold=high` and confirmed all four
   High-severity findings were resolved.

### What changed
- `pom.xml`
  - Added `<dependencyManagement>` override:
    - `org.apache.httpcomponents.core5:httpcore5-h2` → `5.4.3`
    - `org.bouncycastle:bcprov-jdk18on` → `1.84`

**Status:** Fixed — Snyk reports 0 findings at `--severity-threshold=high`.


## Finding #006 — Non-numeric USER instruction in Dockerfile (DL3066)

**Date:** 2026-07-31 (updated 2026-08-03)
**Tool that found it:** Hadolint
**Severity:** Info (pipeline blocking)
**Rule:** DL3066

### Risk
Hadolint flagged the Dockerfile because it used a named user (`USER app`)
instead of a numeric UID. Although the container already ran as a non-root
user, named accounts are not guaranteed to resolve consistently across
different host environments and do not map predictably to Kubernetes Pod
Security Standards, which commonly enforce `runAsNonRoot` with an explicit
numeric UID.

During subsequent Kubernetes hardening, kube-score also identified that the
initial UID (`1001`) was too close to the host's typical user-account range
(starting around UID 1000), increasing the risk of accidental permission
overlap in the unlikely event of a container isolation failure.

### What I did
1. Created the application group and user with explicit numeric IDs instead
   of relying on a username.
2. Updated the Dockerfile to reference the numeric UID directly.
3. Rebuilt the container image and re-ran Hadolint to confirm DL3066 was
   resolved.
4. During Kubernetes hardening, increased the UID/GID from `1001` to
   `10001` following kube-score's recommendation, keeping the container
   non-root while moving outside the host's normal user-ID range.
5. Updated `securityContext.runAsUser` and `runAsGroup` in
   `k8s/app/deployment.yaml` to match the new UID/GID.
6. While rolling out the hardened configuration, identified that enabling
   `readOnlyRootFilesystem: true` caused the application to crash because
   embedded Tomcat requires a writable temporary directory (`/tmp`) during
   startup.
7. Mounted a dedicated `emptyDir` volume at `/tmp`, allowing Tomcat to
   create its temporary files without relaxing the read-only root
   filesystem elsewhere in the container.

### What changed
- `Dockerfile`
  - Replaced:
    ```dockerfile
    USER app
    ```
  - With:
    ```dockerfile
    RUN addgroup -S -g 10001 app && \
        adduser -S -u 10001 -G app app
    USER 10001
    ```
- `k8s/app/deployment.yaml`
  - Updated `securityContext.runAsUser` and `runAsGroup` to `10001`.
  - Added an `emptyDir` volume mounted at `/tmp` to support
    `readOnlyRootFilesystem: true`.

**Status:** Fixed — Hadolint passes without DL3066. UID/GID hardened to
`10001`, and the Tomcat startup issue introduced during the security
hardening rollout was resolved by providing a dedicated writable `/tmp`
volume while keeping the root filesystem read-only.


## Finding #007 — Rootless Podman incompatible with kube-proxy Service networking

**Date:** 2026-08-02
**Component:** Kubernetes (minikube)
**Severity:** Blocking (functional, not a vulnerability)
**Status:** Resolved via documented trade-off

While deploying Vault + Postgres into minikube (Podman driver, rootless),
pod-to-Service networking failed entirely — direct pod-to-pod IP traffic
worked, but ClusterIP-based Service routing timed out consistently. Root
cause: kube-proxy's iptables-based Service routing requires elevated
netfilter/NAT privileges (CAP_NET_ADMIN and access to the host's iptables
tables) that rootless Podman deliberately restricts. kube-proxy logs showed
it starting without error, but Service DNAT rules were not being applied
correctly in this context.

**Decision:** Ran minikube rootful (`sudo minikube start --driver=podman
--force`) instead of rootless. This grants kube-proxy the privileges it
needs to manage iptables normally.

**Trade-off accepted:** the minikube node itself runs with root privileges
on the host. This does NOT affect application pod security — all
application containers still run as non-root (see Dockerfile hardening,
Finding context above) via Pod-level `runAsNonRoot`/numeric UID. This is a
known, common trade-off in the Kubernetes ecosystem — fully rootless
Kubernetes networking remains an actively evolving area, and many
production kubeadm-based clusters run kube-proxy privileged by design.
Alternative rootless-compatible CNIs (e.g. Cilium in eBPF mode) exist but
were not evaluated further, given local/single-node scope of this project.


## Finding #008 — Missing Kubernetes manifest hardening (resources, security context, probes, network policy)

**Date:** 2026-08-03 (updated 2026-08-04)
**Tool that found it:** kube-score
**Severity:** Critical (7 categories) + Warning (1)
**Manifests affected:** `k8s/app/deployment.yaml`, `k8s/postgres/statefulset.yaml`

### Risk

A baseline kube-score scan identified seven Critical hardening issues affecting
both workload manifests (application Deployment and PostgreSQL StatefulSet):
missing CPU/memory/ephemeral-storage resource requests and limits, missing
container security contexts, `imagePullPolicy` not explicitly set to `Always`,
missing readiness probes, and no NetworkPolicies protecting either workload.

Additionally, kube-score reported one Warning because the application
Deployment runs with a single replica.

### What I did

1. Added CPU, memory, and ephemeral-storage resource requests and limits to
   both containers.
2. Added Kubernetes security contexts:
   - **Application:** enforced `runAsNonRoot`, `runAsUser: 1001`,
     `runAsGroup: 1001`, `readOnlyRootFilesystem: true`,
     `allowPrivilegeEscalation: false`, and `capabilities.drop: [ALL]`.
   - **PostgreSQL:** added `allowPrivilegeEscalation: false` and
     `capabilities.drop: [ALL]`. `runAsUser` was intentionally left unset after
     verifying that the official PostgreSQL image starts as root to perform
     ownership initialization before dropping privileges internally to UID 999
     via `gosu`. Forcing `runAsUser: 999` would bypass that initialization step
     and could prevent the database from starting correctly.
3. Set `imagePullPolicy: Always` on both workloads.
4. Added readiness probes:
   - **Application:** HTTP probe against `/actuator/health`.
   - **PostgreSQL:** `pg_isready -h localhost -p 5432`. The database username
     is injected by Vault at runtime, so no `-U` flag is specified.
5. Added NetworkPolicies:
   - Application ingress left open (`ingress: [{}]`) to allow future frontend
     access.
   - Application egress restricted to PostgreSQL (5432), Vault (8200), and DNS
     (53).
   - PostgreSQL ingress restricted to the application only.
   - PostgreSQL egress restricted to Vault (8200) and DNS (53).
6. After kube-score was integrated into the GitLab CI/CD pipeline, the first
   automated scan revealed that this remediation had only partially landed:
   - The application Deployment was still missing its readiness probe.
   - The PostgreSQL StatefulSet was missing its readiness probe,
     `imagePullPolicy: Always`, and security context
     (`allowPrivilegeEscalation: false`,
     `capabilities.drop: [ALL]`).
7. Applied the missing hardening settings to both manifests and re-ran
   kube-score until all intended changes were confirmed in the committed
   manifests.
8. Documented the root cause: this finding had originally been marked fixed
   based on intended changes rather than verified manifests because kube-score
   was executed manually instead of being enforced in CI. Adding kube-score as
   a pipeline gate now prevents this class of configuration drift.

### Accepted trade-offs

- PostgreSQL keeps `readOnlyRootFilesystem: false` because it legitimately
  writes temporary files and Unix sockets outside the mounted data volume.
  PostgreSQL also continues running as root at container start (upstream
  image design — see above); UID hardening above 10000 was not pursued for
  PostgreSQL for this reason.
- The Deployment continues running a single replica because high availability
  is outside the scope of this local portfolio project.

### What changed

- `k8s/app/deployment.yaml`
- `k8s/app/networkpolicy.yaml`
- `k8s/postgres/statefulset.yaml`
- `k8s/postgres/networkpolicy.yaml`

**Status:** Fixed — all Critical kube-score findings have been remediated or
explicitly documented as accepted design trade-offs. The remediation has been
verified through automated kube-score execution in the GitLab CI/CD pipeline,
and the single-replica warning remains an accepted project-scope decision.


## Finding #009 — Vault server missing NetworkPolicy

**Date:** 2026-08-03
**Tool that found it:** Manual architectural review
**Severity:** Critical

### Risk

While implementing NetworkPolicies for the application and PostgreSQL, a manual
review identified that the Vault server had no NetworkPolicy applied. As a
result, any pod in the cluster could reach Vault, including its API on port
8200, despite Vault storing the application's database credentials.

### What I did

1. Verified the Vault server labels using
   `kubectl get pods -n vault --show-labels`.
2. Confirmed the server pod uses
   `app.kubernetes.io/name=vault` and `component=server`.
3. Created `k8s/vault/networkpolicy.yaml`.
4. Restricted ingress to the Vault server so only workloads from the
   `default` namespace can access TCP port 8200.
5. Left the Vault Agent Injector untouched to avoid disrupting admission
   webhook traffic.
6. Applied the policy and confirmed the application still reports
   `/actuator/health` as `UP`, verifying Vault-based secret injection
   continues to function correctly.

### What changed

- `k8s/vault/networkpolicy.yaml`

**Status:** Fixed


## Finding #010 — automountServiceAccountToken cannot be disabled (Vault Kubernetes auth dependency)

**Date:** 2026-08-04
**Discovered during:** RBAC review (checking for unused ServiceAccount token mounts)
**Severity:** N/A (attempted hardening reverted; documented as architectural constraint)

### Risk / Investigation
While reviewing RBAC, `kubectl auth can-i --list` confirmed both
`applicationbib-sa` and `postgres-sa` have no custom Role/RoleBinding
grants — only Kubernetes' built-in discovery/self-review defaults. Since
neither pod calls the Kubernetes API directly, disabling
`automountServiceAccountToken` looked like a valid hardening step to
remove an unused token from the container filesystem.

### What happened
Setting `automountServiceAccountToken: false` on both ServiceAccounts
caused both the application and PostgreSQL pods to crash-loop on startup.
Logs showed:
`IllegalArgumentException: Resource file
[/var/run/secrets/kubernetes.io/serviceaccount/token] does not exist`

Root cause: although the app never calls the Kubernetes API itself,
Spring Cloud Vault's `KUBERNETES` authentication method (used to
authenticate to Vault and fetch DB credentials — see application-k8s.properties)
reads the projected ServiceAccount token file to authenticate. The
Vault Agent Injector sidecar on the PostgreSQL pod has the same
dependency. Removing the automount removed the exact file Vault auth
needs.

### What I did
Reverted `automountServiceAccountToken` to `true` (the default) on both
ServiceAccounts, restored via `kubectl patch` immediately, then corrected
`k8s/app/serviceaccount.yaml` and `k8s/postgres/serviceaccount.yaml` to
avoid reintroducing the issue on next apply. Restarted both workloads and
confirmed health: `/actuator/health` returns `UP`, PostgreSQL pod
`2/2 Running` with no restarts.

### Accepted trade-off
The ServiceAccount token remains mounted in both pods despite neither pod
calling the Kubernetes API directly, because Vault's Kubernetes auth
method depends on it. This is an intentional, documented exception rather
than an oversight — the actual attack surface is limited by the fact that
`applicationbib-sa` and `postgres-sa` still carry no custom RBAC grants,
so even if the token were exfiltrated, it authenticates to Vault only
(not to arbitrary Kubernetes API operations).

**Status:** Resolved (reverted change; documented as architectural constraint).


## Finding #011 — Critical/High: Newly disclosed Bouncy Castle CVEs introduced by Spring Cloud Vault dependency update

**Date:** 2026-08-04
**Tool that found it:** Snyk
**Severity:** Critical + High (4 findings)
**Package:** org.springframework.cloud:spring-cloud-starter-vault-config

### Risk

Following an update of `spring-cloud-starter-vault-config`, Snyk identified
four newly disclosed vulnerabilities in the transitive dependency
`org.bouncycastle:bcprov-jdk18on@1.84`, which is pulled in through
`spring-cloud-starter`.

The affected version contained:

- Improper Certificate Validation (Critical)
- Improper Input Validation (Critical)
- Memory Allocation with Excessive Size Value (High)
- Inadequate Encryption Strength (High)

The application does not use Bouncy Castle directly; the vulnerable version
was resolved transitively by Maven through the Vault starter. As a result,
the project inherited newly published vulnerabilities despite having no
application code changes related to cryptography.

### What I did

1. Used the Snyk dependency tree to verify the vulnerable package was
   introduced transitively by `spring-cloud-starter-vault-config`.
2. Reviewed the Snyk advisories and confirmed all four vulnerabilities are
   fixed in `bcprov-jdk18on` version `1.85`.
3. Updated the existing `<dependencyManagement>` override to force Maven to
   resolve `bcprov-jdk18on` version `1.85` instead of the vulnerable
   transitive version.
4. Ran `./mvnw clean verify` to confirm the dependency update introduced no
   compatibility issues.
5. Re-ran `snyk test --severity-threshold=high` and confirmed all four
   findings were resolved.

### What changed

- `pom.xml`
  - Updated `<dependencyManagement>` override:
    - `org.bouncycastle:bcprov-jdk18on`:
      `1.84` → `1.85`

**Status:** Fixed — Snyk reports 0 findings related to
`bcprov-jdk18on` after forcing version `1.85`.


## Finding #012 — Low: kube-score false positive on vault-server-netpol (NetworkPolicy targets Pod)
**Date:** 2026-08-04
**Tool that found it:** kube-score
**Severity:** Low (false positive, not a real gap)
**Resource:** k8s/vault/networkpolicy.yaml

### Risk
kube-score reported `[CRITICAL] NetworkPolicy targets Pod` for
`vault-server-netpol`, stating its selector matches no Pods. This is a
static-analysis blind spot, not a real misconfiguration: kube-score only
evaluates resources passed to it in the same invocation, and the vault
server Pod is deployed via the official HashiCorp Helm chart, not from a
YAML file in this repo. When scanning `k8s/vault/*.yaml` in isolation,
kube-score has no Pod object to check the selector against.

### What I did
1. Ran `kubectl get pods -n vault --show-labels` to inspect the live
   vault-0 Pod's actual labels.
2. Confirmed `app.kubernetes.io/name=vault` and `component=server` are
   both present on vault-0, matching the NetworkPolicy's `podSelector`
   exactly.
3. Confirmed via `kubectl describe networkpolicy vault-server-netpol -n
   vault` (implicitly, via the working Vault Agent Injector traffic) that
   the policy is actively enforcing, not inert.
4. Added a scoped `kube-score/ignore: networkpolicy-targets-pod`
   annotation to the resource itself, rather than disabling the check
   globally in CI, so the suppression stays documented on the object and
   doesn't blind future scans to real selector mismatches elsewhere.

### What changed
- `k8s/vault/networkpolicy.yaml`
  - Added annotation: `kube-score/ignore: networkpolicy-targets-pod`

**Status:** Verified false positive — annotated and confirmed live label
match against `vault-0`. No functional change; policy was already
correctly enforcing.


## Finding #013 — High: Backend authentication enforced only by frontend (architectural security flaw)

**Date:** 2026-08-05 → 2026-08-12
**Tool that found it:** Manual architectural review (during OWASP ZAP authenticated DAST preparation)
**Severity:** High
**Components affected:** Spring Security configuration, protected business API endpoints

### Risk

While preparing authenticated OWASP ZAP scanning, it was discovered that the
React frontend authenticated users with Firebase, but the Spring Boot backend
did not validate Firebase ID tokens or any bearer token.

Protected business endpoints (`/project/**`, `/company/**`, `/users/**`,
`/session/**`) were configured with `permitAll()`, relying entirely on the
frontend to restrict access.

This allowed anyone to bypass the React application and send requests directly
to the backend using tools such as `curl`, Postman, OWASP ZAP, or Burp Suite.

The backend therefore trusted the client instead of enforcing authentication
server-side, violating the principle that authentication and authorization
decisions must be enforced by the backend.

### What I did

1. Reviewed the authentication architecture while preparing authenticated
   OWASP ZAP scanning.
2. Confirmed that the backend did not previously validate Firebase ID tokens.
3. Confirmed that protected business endpoints were effectively accessible
   without backend authentication because the frontend was responsible for
   enforcing access control.
4. Implemented Firebase Authentication verification in the Spring Boot backend
   using the Firebase Admin SDK.
5. Added a backend authentication filter to extract and validate Firebase
   ID tokens from the `Authorization: Bearer <token>` header.
6. Updated Spring Security configuration so protected business endpoints
   require successful backend authentication.
7. Updated the Kubernetes/network configuration to allow the required HTTP
   traffic to the application so the CI/CD authentication test and OWASP ZAP
   could reach the protected API.
8. Generated a real Firebase ID token in the GitLab CI/CD pipeline using the
   Firebase Authentication API.
9. Added an authenticated API integration test to verify that the generated
   Firebase token is accepted by the deployed backend.
10. Confirmed the authenticated API request returns HTTP `200`.
11. Integrated the authenticated token into the OWASP ZAP scan so ZAP can
    test authenticated application behavior.

### Verification

The GitLab CI/CD pipeline successfully demonstrated:

```text
Firebase ID token generated successfully.
Testing authenticated API access...
API response status: 200
Authenticated API request succeeded.

Starting authenticated OWASP ZAP scan...

FAIL-NEW: 0
FAIL-INPROG: 0
WARN-NEW: 0
WARN-INPROG: 0
INFO: 0
IGNORE: 0
PASS: 61

ZAP exit code: 0
ZAP scan completed.
```

### What changed

- Added Firebase Admin SDK authentication to the backend.
- Added `FirebaseAuthenticationFilter`.
- Updated Spring Security configuration to require authentication for protected
  business endpoints.
- Updated Kubernetes/network configuration to allow the required HTTP traffic
  to the application.
- Added authenticated API verification to the GitLab CI/CD pipeline.
- Added authenticated OWASP ZAP scanning.

**Status:** backend authentication is enforced server-side and
authenticated API access is successfully verified in CI/CD.


## Finding #014 — High: Transitive CVEs introduced by Firebase Admin SDK

**Date:** 2026-08-06
**Tool that found it:** Snyk
**Severity:** High (6 findings)
**Package affected:** firebase-admin@9.7.0
(transitively via grpc-netty-shaded, netty-codec-http,
netty-codec-compression and opentelemetry-api)

### Risk

After integrating Firebase Authentication into the backend, Snyk detected
six High-severity vulnerabilities introduced by the Firebase Admin SDK's
transitive dependencies.

The reported issues included:

- Resource allocation / denial-of-service risks in Netty HTTP components.
- Infinite loop vulnerability in Netty compression.
- Resource exhaustion issue in gRPC.
- Resource allocation issue in OpenTelemetry API.

Although the application does not directly depend on these libraries,
they become part of the runtime through the Firebase Admin SDK.

### What I did

1. Upgraded `firebase-admin` from **9.7.0** to **9.7.1**.
2. Overrode vulnerable transitive dependencies using Maven
   `dependencyManagement`.
3. Updated Netty HTTP components to the patched release.
4. Updated OpenTelemetry API to the fixed version.
5. Re-ran `./mvnw clean verify`.
6. Re-ran the Snyk dependency scan to verify remediation.

### What changed

- `pom.xml`
  - `firebase-admin`:
    `9.7.0` → `9.7.1`
  - Added dependency overrides:
    - `io.netty:netty-codec-http`
    - `io.netty:netty-codec-compression`
    - `io.opentelemetry:opentelemetry-api`

**Status:** Fixed


## Finding #015 — Medium: Over-Permissive Vault Policies and Unbounded Kubernetes Auth Token TTLs

**Date:** 2026-08-12
**Tool that found it:** Manual architectural review (`vault read`, `vault policy read`)
**Severity:** Medium
**Component:** HashiCorp Vault — Kubernetes authentication roles `applicationbib` and `postgres`

### Risk

A manual review of the Vault policies and Kubernetes authentication roles
identified two least-privilege issues.

First, the `applicationbib-read` policy granted `read` access to the wildcard
path:

`secret/data/applicationbib/*`

This allowed the application to read any current or future secret created
under the `applicationbib` prefix, even though the application only requires
the `firebase` and `db` secrets.

Second, the PostgreSQL Kubernetes authentication role was also bound to the
`applicationbib-read` policy. This unnecessarily granted the PostgreSQL pod
read access to the Firebase service-account credentials, which PostgreSQL
does not require.

Both Kubernetes authentication roles also had:

```text
token_max_ttl = 0s
token_explicit_max_ttl = 0s
```

A value of `0s` means no explicit maximum lifetime was configured. This
allowed Vault-issued tokens to remain renewable without a finite maximum
lifetime, increasing the potential impact of a compromised pod or leaked
Vault token.

Together, these issues violated the principle of least privilege and
increased the potential blast radius of a compromised workload or exposed
Vault token.

### What I did

1. Reviewed the existing Vault policy:

```bash
vault policy read applicationbib-read
```

2. Reviewed both Kubernetes authentication roles:

```bash
vault read auth/kubernetes/role/applicationbib
vault read auth/kubernetes/role/postgres
```

3. Reviewed the application Vault configuration and PostgreSQL StatefulSet to
   determine the minimum secrets required by each workload.

4. Confirmed that the application requires:

```text
secret/data/applicationbib/firebase
secret/data/applicationbib/db
```

while PostgreSQL only requires:

```text
secret/data/applicationbib/db
```

5. Replaced the wildcard application policy with explicitly scoped paths:

```hcl
path "secret/data/applicationbib/firebase" {
  capabilities = ["read"]
}

path "secret/data/applicationbib/db" {
  capabilities = ["read"]
}
```

6. Created a dedicated PostgreSQL policy containing only the database secret:

```hcl
path "secret/data/applicationbib/db" {
  capabilities = ["read"]
}
```

7. Applied the corrected policies:

```bash
vault policy write applicationbib-read applicationbib-read.hcl
vault policy write postgres-read postgres-read.hcl
```

8. Updated the Kubernetes authentication roles so each workload receives
   only the policy it requires and has a finite token lifetime:

```bash
vault write auth/kubernetes/role/applicationbib \
  bound_service_account_names=applicationbib-sa \
  bound_service_account_namespaces=default \
  policies=applicationbib-read \
  ttl=1h \
  max_ttl=24h

vault write auth/kubernetes/role/postgres \
  bound_service_account_names=postgres-sa \
  bound_service_account_namespaces=default \
  policies=postgres-read \
  ttl=1h \
  max_ttl=24h
```

9. Restarted the `applicationbib` and `postgres` workloads to force
   re-authentication using the corrected Vault roles.

10. Verified that the application successfully obtained its required secrets
    and started normally.

11. Verified database connectivity through the application connection pool
    (`HikariPool-1`).

12. Verified `/actuator/health` returned `UP`.

13. Verified PostgreSQL remained healthy and its readiness probe continued to
    pass.

14. Verified Firebase token authentication continued to work in the GitLab
    CI/CD pipeline after the Vault policy changes.

### What changed

- `applicationbib-read` policy:
  - Removed the wildcard `secret/data/applicationbib/*` permission.
  - Restricted access to exactly:
    - `secret/data/applicationbib/firebase`
    - `secret/data/applicationbib/db`
- Created a dedicated `postgres-read` policy containing access only to:
  - `secret/data/applicationbib/db`
- Updated the `postgres` Kubernetes authentication role to use
  `postgres-read` instead of `applicationbib-read`.
- Configured a `1h` token TTL and `24h` maximum token lifetime for both
  Kubernetes authentication roles.
- Removed the previous unlimited token-lifetime configuration.
- Verified that Vault authentication, application startup, database
  connectivity, health checks, and Firebase authentication remained
  functional after the hardening.

### Security impact

The remediation reduces the blast radius of a compromised workload:

- The application can no longer read arbitrary secrets under the
  `applicationbib` prefix.
- PostgreSQL can no longer read the application's Firebase credentials.
- Vault tokens are now subject to a finite maximum lifetime and must
  periodically re-authenticate.

**Status:** Fixed — Vault policies and Kubernetes authentication roles now
follow least-privilege access and enforce finite token lifetimes.


## Finding #016 — ZAP Policy Configuration and Fine-Grained Alert Gating

**Date:** 2026-08-12
**Tool:** OWASP ZAP
**Severity:** Medium (security-control hardening)

### Risk

The initial OWASP ZAP integration relied primarily on the baseline scan
without explicitly documenting which ZAP security rules were considered
important enough to gate the CI/CD pipeline.

For an authenticated API, treating every ZAP alert identically can create
two problems:

- Important security findings may not be enforced consistently as pipeline
  blockers.
- Lower-value or informational findings can create unnecessary pipeline
  failures and reduce the usefulness of the security gate.

A controlled ZAP policy was therefore introduced so that security-relevant
alerts are explicitly selected and their expected severity is defined.

### What I did

1. Reviewed the available OWASP ZAP alert rules using the official ZAP
   alert reference:

   https://www.zaproxy.org/docs/alerts/

2. Identified security-relevant alert categories for the application,
   particularly:

   - SQL Injection
   - PostgreSQL-specific SQL Injection
   - Server-Side Code Injection
   - Remote OS Command Injection
   - Expression Language Injection
   - XML External Entity (XXE)
   - Application Error Disclosure
   - PII Disclosure
   - Reflected Cross-Site Scripting

3. Configured the ZAP scan policy so the selected security rules are
   explicitly evaluated during the CI/CD scan.

4. Added fine-grained pipeline gating based on selected ZAP alert IDs
   instead of treating every ZAP message as a pipeline failure.

5. Kept lower-priority informational or non-blocking findings from
   unnecessarily failing the deployment pipeline.

6. Verified the configured ZAP policy against the authenticated scan of
   the deployed application.

### Selected ZAP Alert IDs

The selected security controls include:

- `40018` — SQL Injection
- `40022` — SQL Injection (PostgreSQL)
- `90019` — Server Side Code Injection
- `90020` — Remote OS Command Injection
- `90025` — Expression Language Injection
- `90023` — XML External Entity (XXE)
- `90022` — Application Error Disclosure
- `10062` — PII Disclosure
- `40012` — Cross Site Scripting (Reflected)

The complete alert definitions are maintained in the official OWASP ZAP
alert reference.

### CI/CD Verification

The authenticated OWASP ZAP scan was executed against the deployed
application after Firebase authentication was integrated.

The pipeline successfully reported:

    FAIL-NEW: 0
    FAIL-INPROG: 0
    WARN-NEW: 0
    WARN-INPROG: 0
    INFO: 0
    IGNORE: 0
    PASS: 61

The ZAP process exited successfully with exit code `0`.

This confirms that the configured security policy and selected alert
gates did not identify any new blocking findings in the deployed
application.

### What changed

- Documented the ZAP security policy and selected alert IDs.
- Defined security-relevant ZAP rules that should be treated as pipeline
  security gates.
- Added fine-grained gating based on specific ZAP alert IDs.
- Kept non-security-critical or informational ZAP results from causing
  unnecessary pipeline failures.
- Documented the official ZAP alert reference used to identify and classify
  the selected rules.

### Security impact

The ZAP integration now provides a controlled DAST security gate rather
than simply executing a generic baseline scan.

The pipeline explicitly checks for selected high-value attack classes,
including injection, code execution, XXE, authentication-related
information disclosure, PII disclosure, and reflected XSS.

This makes the DAST stage predictable, auditable, and aligned with the
application's actual security requirements.

**Status:** Fixed — ZAP policy configuration and fine-grained alert-ID
pipeline gating are implemented and verified in CI/CD.


## Finding #017 — Medium: Vault KV Secret Versioning, Retention Limits, and CAS Enforcement

**Date:** 2026-08-13
**Tool that found it:** Manual architectural review (`vault kv metadata`)
**Severity:** Medium
**Component:** HashiCorp Vault KV v2 — `secret/applicationbib/firebase` and `secret/applicationbib/db`

### Risk

A review of the application's Vault KV v2 secrets identified that the Firebase service-account secret and database credentials did not have explicit version-retention limits or Check-And-Set (CAS) enforcement.

Without a maximum version count, repeated updates could retain an unbounded number of historical secret versions. This increases storage usage and unnecessarily extends the lifetime of old credential versions.

Without a defined retention period, historical secret versions could remain available indefinitely, increasing the amount of sensitive credential material retained by Vault.

Without CAS enforcement, a secret could be overwritten without requiring the writer to explicitly specify the expected current version. This creates a risk of accidental or unintended overwrites during secret rotation or concurrent administrative operations.

The affected secrets were:

- `secret/applicationbib/firebase`
- `secret/applicationbib/db`

### What I did

1. Reviewed the existing KV metadata for both application secrets:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata get secret/applicationbib/firebase`

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata get secret/applicationbib/db`

2. Configured both secrets with a maximum of **5 retained versions**.

3. Configured automatic deletion of old secret versions after **2160 hours (90 days)**.

4. Enabled **CAS enforcement** using `-cas-required=true`.

5. Applied the configuration:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata put -max-versions=5 -delete-version-after=2160h -cas-required=true secret/applicationbib/firebase`

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata put -max-versions=5 -delete-version-after=2160h -cas-required=true secret/applicationbib/db`

6. Re-read the metadata to verify the configuration:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata get secret/applicationbib/firebase`

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv metadata get secret/applicationbib/db`

7. Confirmed both secrets report:

    `cas_required          true`
    `delete_version_after  2160h0m0s`
    `max_versions          5`

### What changed

- `secret/applicationbib/firebase`
  - Maximum retained versions: **5**
  - Automatic deletion after: **90 days**
  - CAS enforcement: **enabled**

- `secret/applicationbib/db`
  - Maximum retained versions: **5**
  - Automatic deletion after: **90 days**
  - CAS enforcement: **enabled**

### Security impact

The remediation limits the number and lifetime of historical secret versions retained by Vault and requires CAS-aware writes.

This reduces unnecessary exposure of old credentials, prevents uncontrolled version growth, and provides safer secret rotation and update semantics.

**Status:** Fixed — Vault KV secrets now enforce bounded version retention, 90-day cleanup, and CAS-required writes.


## Finding #018 — Medium: Vault Audit Logging Was Disabled

**Date:** 2026-08-13
**Tool that found it:** Manual architectural review (`vault audit list`)
**Severity:** Medium
**Component:** HashiCorp Vault audit subsystem

### Risk

A manual review of the Vault security configuration identified that no audit devices were enabled.

Running:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault audit list`

initially returned:

    `No audit devices are enabled.`

Without an enabled Vault audit device, Vault API operations such as authentication attempts, secret reads, policy operations, and other security-relevant requests are not recorded by Vault's audit subsystem.

This reduces the ability to investigate unauthorized access, detect suspicious secret access, reconstruct security events, and perform forensic analysis after a security incident.

### Investigation

The Vault pod stores persistent data under `/vault/data`, which is backed by the persistent volume claim `data-vault-0`.

A dedicated audit directory was created under this persistent location.

The initial directory permissions were too restrictive and prevented Vault from writing the audit log.

The Vault process was verified with:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- whoami`

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- id`

The Vault process runs as:

    `vault`
    `uid=100(vault) gid=1000(vault) groups=1000(vault)`

The audit directory initially had restrictive permissions:

    `Access: (0600/drw-------)`
    `Uid: (100/vault)`
    `Gid: (1000/vault)`

### What I did

1. Created the dedicated audit directory:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- mkdir -p /vault/data/audit`

2. Corrected the directory permissions:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- chmod 700 /vault/data/audit`

3. Verified the resulting permissions:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- stat /vault/data/audit`

    The directory was confirmed as:

    `Access: (0700/drwx------)`
    `Uid: (100/vault)`
    `Gid: (1000/vault)`

4. Verified that the Vault process could write to the directory by creating a temporary test file:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- sh -c 'touch /vault/data/audit/test.log'`

5. Removed the temporary test file:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- rm /vault/data/audit/test.log`

6. Enabled the file-based Vault audit device using the persistent Vault volume:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault audit enable file file_path=/vault/data/audit/audit.log`

    Vault returned:

    `Success! Enabled the file audit device at: file/`

7. Verified that the audit device is enabled:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault audit list`

    Result:

    `Path     Type    Description`
    `----     ----    -----------`
    `file/    file    n/a`

8. Generated a real Vault secret-read operation to exercise the audit device:

    `sudo /usr/local/bin/kubectl exec -n vault vault-0 -- vault kv get secret/applicationbib/firebase`

    The request successfully returned the Firebase secret, confirming that Vault remained operational after enabling the audit device.

### What changed

- Created `/vault/data/audit` on the persistent Vault data volume.
- Corrected the directory permissions to `0700` for the Vault process.
- Enabled the Vault `file/` audit device.
- Configured the audit log at:

    `/vault/data/audit/audit.log`

- Verified the audit device is active with `vault audit list`.
- Verified normal Vault secret access continues to function after enabling auditing.

### Security impact

Vault now maintains an audit trail of Vault API activity, providing visibility into authentication, secret access, policy operations, and other security-relevant requests.

The audit log is stored under the persistent `/vault/data` volume rather than ephemeral container storage, so it survives Vault pod restarts.

Vault automatically HMACs sensitive fields such as tokens and secret values in audit records, preventing the actual secret contents from being stored directly in plaintext in the audit log.

### Operational consideration

The audit log is currently stored on the same persistent volume as the Vault data backend.

Vault's file audit device does not provide automatic log rotation. For a production deployment, audit-log rotation or forwarding to a centralized logging system should be considered to prevent audit logs from consuming the Vault storage volume.

**Status:** Fixed — Vault audit logging is enabled, writable, persistent, and verified.


## Finding #019 — Critical/High: 3 CVEs via outdated Netty & Micrometer (transitive, firebase-admin & micrometer-registry-prometheus)
**Date:** 2026-08-24
**Tool that found it:** Snyk
**Severity:** Critical (1) + High (2)
**Packages affected:**
- io.netty:netty-handler@4.2.15.Final (transitively via com.google.firebase:firebase-admin@9.7.1)
- io.netty:netty-codec-http@4.2.16.Final (transitively via com.google.firebase:firebase-admin@9.7.1)
- io.micrometer:micrometer-core@1.16.6 (transitively via io.micrometer:micrometer-registry-prometheus@1.16.7 AND org.springframework.boot:spring-boot-starter-actuator@4.0.7)

### Risk
3 Snyk findings traced back to two root causes: outdated transitive dependencies pulled in despite direct dependency versions being bumped, because Spring Boot's parent BOM and/or transitive resolution order kept re-pinning the older versions.

- **[Critical] Improper Check for Unusual or Exceptional Conditions**
  (SNYK-JAVA-IONETTY-19005879) in `netty-handler@4.2.15.Final` — malformed
  or unexpected input handling could lead to abnormal behavior or denial
  of service.
- **[High] Cert validation mismatch**
  (SNYK-JAVA-IONETTY-19233599) in `netty-handler@4.2.15.Final`.
- **[High] CRLF Injection**
  (SNYK-JAVA-IOMICROMETER-19233327) in `micrometer-core@1.16.6` —
  fixed upstream in 1.16.7 / 1.17.1.

### What I did
1. Confirmed no direct upgrade/patch was offered by Snyk for the Netty issues (fix required overriding the transitive version); a fix version existed for Micrometer (1.16.7) but bumping the direct `micrometer-registry-prometheus` version alone didn't propagate to `micrometer-core`.
2. Ran `mvn dependency:tree -Dincludes=io.netty:netty-handler` — confirmed initial `dependencyManagement` override for Netty wasn't taking effect (Maven still resolved 4.2.15.Final from `firebase-admin`).
3. Fixed Netty by excluding `netty-handler` from `firebase-admin` and adding it back as a direct dependency pinned to `4.2.17.Final`. Re-ran the tree check to confirm `4.2.17.Final` resolved cleanly.
4. Ran `mvn dependency:tree -Dincludes=io.micrometer` — confirmed `micrometer-core` still resolved to `1.16.6`, pulled in both by `spring-boot-starter-actuator` (via parent BOM) and by `micrometer-registry-prometheus`.
5. Fixed Micrometer by adding an explicit `dependencyManagement` entry pinning `micrometer-core` to `1.16.7`. Re-ran the tree check to confirm `1.16.7` resolved cleanly.
6. Also fixed an unrelated Maven build warning: removed a duplicate `spring-boot-starter-actuator` declaration in `<dependencies>`.
7. Re-ran `snyk test --severity-threshold=high` — 0 issues found. Pipeline passed.

### What changed
- `pom.xml`:
  - Excluded `io.netty:netty-handler` from `com.google.firebase:firebase-admin@9.7.1` and added it as a direct dependency at `4.2.17.Final`.
  - Added `dependencyManagement` entries pinning `io.netty:netty-codec-http` and `io.netty:netty-codec-compression` to `4.2.17.Final`.
  - Added `dependencyManagement` entry pinning `io.micrometer:micrometer-core` to `1.16.7`.
  - Removed duplicate `spring-boot-starter-actuator` dependency declaration.

### Verification
- `mvn dependency:tree -Dincludes=io.netty:netty-handler` → resolves `4.2.17.Final`
- `mvn dependency:tree -Dincludes=io.micrometer` → `micrometer-core` resolves `1.16.7`
- `snyk test --severity-threshold=high` → **Tested 238 dependencies, 0 issues found. Pipeline passed.**

**Status:** ✅ Fixed & Verified




## Summary

| Finding | CVEs / Issues Covered | Tool | Status |
| ------- | ---------------------- | ---- | ------ |
| #001 | **38 Critical/High CVEs** — Spring Boot 3.4.3 dependency vulnerabilities | Snyk | ✅ Fixed |
| #002 | **19 Critical/High CVEs** — Spring Web/Tomcat/Jackson vulnerabilities requiring Spring Boot 4 migration | Snyk | ✅ Fixed |
| #003 | **2 High OS-level CVEs** + unnecessary runtime attack surface (`gnupg`) | Trivy, Grype | ✅ Fixed |
| #004 | **False positive** — `sonar.projectKey` incorrectly detected as a secret | Gitleaks | ✅ Resolved |
| #005 | **4 High CVEs** — vulnerable transitive `httpcore5-h2` and Bouncy Castle dependencies | Snyk | ✅ Fixed |
| #006 | **Hadolint DL3066** + container UID/GID hardening + `readOnlyRootFilesystem` rollout issue | Hadolint, kube-score | ✅ Fixed |
| #007 | **Kubernetes networking failure** — rootless Podman incompatible with kube-proxy Service routing | Manual / minikube | ✅ Resolved (documented trade-off) |
| #008 | **7 Critical + 1 Warning** — missing Kubernetes resources, security contexts, probes, image policies, and NetworkPolicies | kube-score | ✅ Fixed (Warning accepted) |
| #009 | **Critical NetworkPolicy gap** — Vault server had unrestricted pod-to-Vault access | Manual architectural review | ✅ Fixed |
| #010 | **Vault Kubernetes auth constraint** — ServiceAccount token cannot be disabled without breaking Vault authentication | RBAC review | ✅ Documented trade-off |
| #011 | **4 CVEs (2 Critical + 2 High)** — vulnerable Bouncy Castle `1.84` transitive dependency | Snyk | ✅ Fixed |
| #012 | **False positive** — kube-score incorrectly reported Vault NetworkPolicy selector as targeting no Pod | kube-score | ✅ Verified false positive |
| #013 | **High architectural flaw** — backend trusted frontend authentication; business APIs were publicly accessible | Manual architecture review | ✅ Fixed |
| #014 | **6 High CVEs** — vulnerable Firebase Admin SDK transitive dependencies (Netty, gRPC, OpenTelemetry) | Snyk | ✅ Fixed |
| #015 | **Vault least-privilege hardening + finite token TTLs (Medium)** | Manual architectural review | ✅ Fixed |
| #016 | **OWASP ZAP policy/rule configuration + fine-grained alert-ID pipeline gating (Medium)** | OWASP ZAP / Manual security review | ✅ Fixed |
| #017 | **Vault KV secret versioning/retention limits + CAS enforcement (Medium)** | Manual architectural review | ✅ Fixed |
| #018 | **Vault audit logging enabled (Medium)** | Manual architectural review | ✅ Fixed |
| #019 | **3 Critical/High CVEs** — Netty 4.2.15/4.2.16 (via firebase-admin) & Micrometer-core 1.16.6 (via micrometer-registry-prometheus & spring-boot-starter-actuator) | Snyk | ✅ Fixed |