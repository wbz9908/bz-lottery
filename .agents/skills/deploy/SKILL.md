---
name: bz-lottery-deploy
description: Use when working on bz-lottery deployment, Docker Compose, Nginx, infrastructure scripts, ports, environment files, or production deployment drafts.
---

# bz-lottery Deploy Skill

## When To Use

Use this Skill for changes under `deploy/`, Docker Compose files, Nginx configuration, environment templates, PowerShell scripts, ports, volumes, local infrastructure, and production deployment drafts.

## Workflow

1. Read `references/deploy-rules.md`.
2. Inspect the relevant compose, script, or Nginx file before editing.
3. Keep development and production configuration separated.
4. Update port tables or environment documentation when ports or services change.
5. Validate Docker Compose syntax when compose files change.
6. Deliver with changed scope, verification result, unverified items, and risk.

## Rules

1. Use `deploy/scripts/` as the unified entry point for environment operations.
2. Do not hard-code secrets in compose or script files.
3. Keep compose layers separated: dev infrastructure, dev tools, and edge.
4. Production configuration under `deploy/prod/` must not be mixed into dev config.
5. Startup and shutdown scripts should be idempotent.
6. Persistent development data belongs under `deploy/docker/data/`.

## Verification

For compose changes, run the matching syntax check, for example:

```powershell
docker compose -f deploy/compose/compose.dev-infra.yml config --quiet
```

For script changes, run or dry-run the specific script when safe and describe the result.

## Deliverable

Include:

- Changed scope
- Verification commands and results
- Unverified items
- Risks

## References

| Reference | Purpose |
| --- | --- |
| `references/deploy-rules.md` | Docker Compose, Nginx, script, port and deployment rules |
