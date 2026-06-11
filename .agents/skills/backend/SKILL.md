---
name: bz-lottery-backend
description: Use when working on bz-lottery backend Java/Spring Boot services, APIs, business rules, database migrations, MyBatis-Plus mappers, Dubbo integrations, gateway behavior, or backend tests.
---

# bz-lottery Backend Skill

## When To Use

Use this Skill for changes under `lottery-backend/`, backend API contracts, Flyway migrations, database schema documentation, service boundaries, draw business rules, gateway routing, Dubbo calls, backend tests, and backend build or runtime issues.

If the task also changes frontend or deployment files, read the matching Skill as well.

## Workflow

1. Read the references in this order:
   - `references/architecture-rules.md`
   - `references/business-rules.md`
   - `references/api-spec.md`
   - `references/coding-standards.md`
   - `references/java-dev.md`
   - `references/data-baseline.md` when data or migrations are involved
   - `references/schema-index.md` and `references/ddl-all-tables.md` when table structure is involved
2. Inspect the existing module structure before editing.
3. Keep changes inside the owning module unless a shared contract truly needs to move.
4. Update API annotations, DTOs, migrations, and reference docs when the contract or schema changes.
5. Run the narrowest useful Maven verification.
6. Deliver with changed scope, verification result, unverified items, and risk.

## Rules

1. Preserve module boundaries: `gateway`, `common`, `domain`, `user`, `activity`, `lottery`, `award`, `pay`, `workflow`, `file`, `monitor`, `ai`.
2. Follow layering: `controller -> service -> mapper`; controllers must not call mappers directly.
3. Cross-service calls use the established Dubbo/RPC contracts.
4. Shared capabilities belong in `lottery-common`; shared domain objects belong in `lottery-domain`.
5. Key logs must include `traceId` and useful business identifiers.
6. Inventory and balance changes must use atomic SQL or equivalent concurrency-safe guards.
7. MQ consumers must be idempotent and retry-safe.
8. Database changes must use Flyway; never bypass migrations.

## Verification

Prefer the narrowest command that covers the touched module:

```powershell
cd lottery-backend
mvn -pl <module> -am compile
```

For shared modules or broad changes:

```powershell
cd lottery-backend
mvn -q -DskipTests compile
```

Run tests when behavior changes are non-trivial:

```powershell
cd lottery-backend
mvn -pl <module> test
```

## Deliverable

Include:

- Changed scope
- Verification commands and results
- Unverified items
- Risks and rollback notes when relevant

## References

| Reference | Purpose |
| --- | --- |
| `references/architecture-rules.md` | Module boundaries, layering, cross-service rules |
| `references/business-rules.md` | Core lottery business rules |
| `references/api-spec.md` | API contract conventions |
| `references/coding-standards.md` | General coding standards |
| `references/java-dev.md` | Java/Spring/MyBatis details |
| `references/data-baseline.md` | Flyway and data baseline rules |
| `references/schema-index.md` | Table purpose, relations, indexes |
| `references/ddl-all-tables.md` | Full DDL reference |
