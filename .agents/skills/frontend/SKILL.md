---
name: bz-lottery-frontend
description: Use when working on bz-lottery frontend Vue 3, TypeScript, Vite, routing, state management, API integration, UI components, or frontend builds.
---

# bz-lottery Frontend Skill

## When To Use

Use this Skill for changes under `lottery-frontend/`, frontend API integration, Vue components, router changes, Pinia stores, UI states, Vite configuration, and frontend build issues.

If the task changes backend API contracts, read the backend Skill too.

## Workflow

1. Read the references in this order:
   - `references/api-spec.md`
   - `references/frontend-dev.md`
2. Inspect existing components, stores, router tables, and API clients before editing.
3. Use existing project patterns instead of introducing new UI or state abstractions.
4. Handle loading, empty, and error states for data-fetching views.
5. Run `npm run build` when frontend behavior or TypeScript code changes.
6. Deliver with changed scope, verification result, unverified items, and risk.

## Rules

1. Use Vue 3 Composition API with `<script setup lang="ts">`.
2. Use the shared HTTP client for all API calls.
3. Keep route metadata and role guards in sync with navigation changes.
4. Preserve the existing design system and layout patterns.
5. Do not bypass the gateway; frontend API paths use the configured `/lottery-*` proxy.
6. Keep components focused and avoid unrelated visual rewrites.

## Verification

```powershell
cd lottery-frontend
npm run build
```

When UI behavior is involved, also describe any manual checks performed.

## Deliverable

Include:

- Changed scope
- Verification commands and results
- Unverified items
- Risks

## References

| Reference | Purpose |
| --- | --- |
| `references/api-spec.md` | Frontend-facing API contract |
| `references/frontend-dev.md` | Vue 3, TypeScript, component and state rules |
