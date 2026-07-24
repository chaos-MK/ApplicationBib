## Finding #001 — Hardcoded database password in application.yml

**Date:** 2026-07-21
**Tool that found it:** Gitleaks
**Severity:** Critical
**Location:** `src/main/resources/application.yml`, line 14

### Risk
The PostgreSQL password was committed in plaintext to the repository.
Anyone with read access to the repo (including in git history, even
after deletion) could connect directly to the production database.
This violates the principle of never storing secrets in source control.

### What I did
1. Rotated the exposed database password immediately (old one is now invalid).
2. Removed the hardcoded value from `application.yml`.
3. Replaced it with an environment variable reference: `${DB_PASSWORD}`.
4. Added the real value as a GitLab CI/CD masked variable instead.
5. Confirmed via `git log -p` that no other secrets exist in history
   (ran Gitleaks against full history, not just the current commit).

### What changed
- `application.yml`: `password: hunter2` → `password: ${DB_PASSWORD}`
- Added `DB_PASSWORD` as a protected, masked variable in GitLab Settings → CI/CD → Variables
