# Security Findings Log

This log documents vulnerabilities identified by automated security scanning
in the GitLab CI/CD pipeline (Gitleaks, Semgrep, Snyk, Hadolint, Trivy, Grype,
Syft), and the remediation applied for each.

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


| Finding | CVEs/Issues Covered | Tool | Status |
|---|---|---|---|
| #001 | 38 | Snyk | ✅ Fixed |
| #002 | 19 | Snyk | ✅ Fixed |
| #003 | OS-level (2 High + hardening) | Trivy, Grype | ✅ Fixed |