# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

bz-lottery is a personal full-stack learning project — a lottery platform with Spring Boot 3 + Spring Cloud Alibaba microservices (Java 25, Virtual Threads) on the backend and Vue 3 + Vite 6 on the frontend. Infrastructure runs via Docker Compose (PostgreSQL 17, Redis, Nacos, Kafka, RocketMQ, MinIO).

## Before Any Task

Each sub-project has an `.agents/` directory with mandatory pre-read references. Read the relevant SKILL.md first, then follow its reference chain before making changes:

| Scope | SKILL.md | Reference order |
|-------|----------|-----------------|
| Backend | `lottery-backend/.agents/SKILL.md` | architecture → business → api → code → data → commit |
| Frontend | `lottery-frontend/.agents/SKILL.md` | api → code → commit |
| Deploy | `deploy/.agents/SKILL.md` | infrastructure → commit |

All files must be UTF-8. Make minimal, focused changes — no unrelated refactoring.

## Essential Commands

### Backend

```bash
cd lottery-backend

# Compile all
mvn -q -DskipTests compile

# Compile one module + dependencies
mvn -pl lottery-lottery -am compile

# Run tests for one module
mvn -pl lottery-lottery test

# Run a single service
mvn -pl lottery-lottery -am spring-boot:run
```

Module startup order: `gateway` → `user` → `activity` → `lottery` → `award` → `pay` → `workflow` → `file` → `monitor`.

### Frontend

```bash
cd lottery-frontend
npm install
npm run dev        # port 9510, proxies /lottery-* → gateway:9008
npm run build
npm run preview    # port 9511
```

### Infrastructure

```powershell
.\deploy\scripts\up-dev.ps1     # PostgreSQL, Redis, Nacos, Kafka, RocketMQ, MinIO
.\deploy\scripts\up-edge.ps1    # Nginx
.\deploy\scripts\down.ps1       # Stop all
```

## Architecture

### Service Map

```
Vue 3 (:9510) → Vite proxy → Gateway (:9008) → Nacos registry
                                                   ↓
  user ── activity ── lottery ── award ── pay ── workflow ── file ── monitor ── ai
    ↓        ↓          ↓         ↓       ↓        ↓          ↓        ↓         ↓
  [lottery-common: ApiResponse, TraceId, exception handler, VirtualThread config]
  [lottery-domain: shared entities/POJOs]
```

- **Gateway** (`lottery-gateway`): Unified entry on `:9008`. Route config, CORS, auth filter, rate limiting.
- **Common** (`lottery-common`): `ApiResponse<T>` envelope (`code`, `message`, `data`, `timestamp`, `traceId`), global exception handler, TraceId injection, VirtualThread + WebMvc config.
- **Domain** (`lottery-domain`): Shared domain entities.
- **User**: Auth (Spring Security + Sa-Token style), two roles: `LOTTERY_USER` and `LOTTERY_ADMIN`.
- **Activity**: Activity lifecycle (create, edit, publish, unpublish).
- **Lottery**: Core draw engine — strategy, probability, inventory deduction, draw recording.
- **Award**: Prize catalog, inventory, images, distribution rules.
- **Workflow**: WebSocket real-time push for draw results.
- **File**: MinIO object storage.
- **Pay/AI/Monitor**: Payment integration, AI features, health metrics.

### Layering (strict per module)

```
controller → service (interface + impl) → mapper (MyBatis-Plus)
```

Controllers never call mappers directly. Cross-service calls use Dubbo RPC.

### Database

Flyway migrations (`V{n}__description.sql`) and repeatable scripts (`R__description.sql`). All tables have `id` (int8 auto-increment), `created_at`, `updated_at`, `deleted` (soft delete).

| Table | Purpose |
|-------|---------|
| `lottery_draw_record` | Draw transaction log with full snapshot |
| `lottery_prize` | Prize catalog with inventory |
| `lottery_system_config` | Global KV config (strategy params) |
| `user_account` | User identity and credentials |
| `sys_role` / `sys_menu` | RBAC: roles, menus |
| `user_role_rel` / `role_menu_rel` | M:N relationships |

### Key Conventions

- **TraceId**: Injected per-request via filter, propagated across services. All key logs include it.
- **Concurrency**: Virtual Threads (Java 25). Atomic SQL updates for inventory (`UPDATE SET stock = stock - 1 WHERE ... AND stock > 0`). Unique indexes as concurrency backstop. No read-modify-write.
- **Idempotency**: Draw requests use `requestNo`. MQ consumers must be idempotent with retry.
- **DTOs**: Lombok `@Data` / `@Value` / `@Builder`. Never hand-write getters/setters.
- **DI**: Constructor injection with `@RequiredArgsConstructor`.
- **HTTP clients**: Use `java.net.http.HttpClient` (JDK 25) for external API calls, not `RestTemplate` default client (known CDN issues with Chinese platforms).
- **Batch queries**: Max 500 items per `IN` clause. Split and merge if larger.
- **N+1**: Forbid loops calling repos/mappers. Batch query outside loops, convert to Map lookup.
- **Pagination**: 0-based index, consistent across stack. `@Max(100)` on `size` parameter.

### Frontend

- Composition API with `<script setup>` only. No Options API.
- All API calls use shared Axios instance (`api/http.js`) handling token injection and unified error codes.
- Routes defined in `router/route-table.js` with `meta.roles` for guard-based access.
- Two role-specific layouts: `/admin` (LOTTERY_ADMIN) and `/app` (LOTTERY_USER).
- Handle loading, empty, and error states in every data-fetching component.

### Commit Conventions

- Branches: `codex/{module}-{task}`
- Messages: `type(scope): subject` — `feat`, `fix`, `refactor`, `docs`, `test`, `chore`
- AI participation: append `Co-authored-by: Claude <noreply@anthropic.com>` (and/or Codex equivalent)
