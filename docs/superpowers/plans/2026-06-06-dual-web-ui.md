# Dual Web UI Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a responsive two-entry frontend for `bz-lottery`: `/admin` for PC operations and `/app` for mobile user participation.

**Architecture:** Keep one Vue 3 + Vite frontend project. Split route groups into admin and mobile app layouts while reusing session state, API helpers, and lottery business calls. First implementation stays JavaScript to match the current codebase and avoids a broad TypeScript migration.

**Tech Stack:** Vue 3 Composition API, Vue Router, Vite, plain CSS, existing `requestJson` / `requestEventStream` API helpers.

---

## File Structure

- Modify: `lottery-frontend/src/router/route-table.js`
  - Replace `/workspace` route grouping with `/admin` and `/app` route groups.
  - Export separate `adminRouteCatalog`, `appRouteCatalog`, and combined `routeCatalog`.
- Modify: `lottery-frontend/src/router/index.js`
  - Install dynamic routes under both `admin` and `mobileApp`.
  - Resolve home route by role: admins to `/admin/overview`, normal users to `/app/home`.
- Create: `lottery-frontend/src/views/admin/AdminLayout.vue`
  - PC operations layout with sidebar, topbar, user info, and nested router view.
- Create: `lottery-frontend/src/views/app/MobileAppLayout.vue`
  - Mobile layout with topbar, bottom tabbar, and nested router view.
- Create: `lottery-frontend/src/views/admin/AdminOverviewView.vue`
  - Admin overview dashboard using existing prize/record APIs.
- Create: `lottery-frontend/src/views/admin/AdminActivitiesView.vue`
  - Activity operations placeholder with static MVP cards.
- Create: `lottery-frontend/src/views/admin/AdminPrizeCenterView.vue`
  - Admin-oriented prize center using existing prize API.
- Create: `lottery-frontend/src/views/admin/AdminRecordsView.vue`
  - Admin-oriented records page using existing records API for current user.
- Create: `lottery-frontend/src/views/admin/AdminAiAnalysisView.vue`
  - Move AI analysis workflow out of current lottery page into an admin page.
- Create: `lottery-frontend/src/views/admin/AdminProfileView.vue`
  - Admin profile and permission summary.
- Create: `lottery-frontend/src/views/app/MobileHomeView.vue`
  - Mobile activity landing page.
- Create: `lottery-frontend/src/views/app/MobileDrawView.vue`
  - Mobile draw page, based on current draw logic.
- Create: `lottery-frontend/src/views/app/MobileRecordsView.vue`
  - Mobile record history.
- Create: `lottery-frontend/src/views/app/MobileProfileView.vue`
  - Mobile profile and logout.
- Modify: `lottery-frontend/src/views/LoginView.vue`
  - Preserve auth behavior; improve redirect defaults only if needed.
- Modify: `lottery-frontend/src/style.css`
  - Replace one-note beige/gold palette with SaaS admin + mobile activity visual system.
  - Add responsive layout classes, mobile safe-area handling, states, and reusable cards.

## Task 1: Route Split

**Files:**
- Modify: `lottery-frontend/src/router/route-table.js`
- Modify: `lottery-frontend/src/router/index.js`

- [ ] **Step 1: Snapshot current route behavior**

Run:

```bash
cd lottery-frontend
npm run build
```

Expected: build currently passes or reveals pre-existing build issues before route changes.

- [ ] **Step 2: Define route catalogs**

Replace `route-table.js` with:

```js
import AdminLayout from '../views/admin/AdminLayout.vue'
import MobileAppLayout from '../views/app/MobileAppLayout.vue'

export const adminRouteCatalog = [
  {
    path: 'overview',
    name: 'adminOverview',
    component: () => import('../views/admin/AdminOverviewView.vue'),
    meta: {
      menuCode: 'DASHBOARD_OVERVIEW',
      title: '运营概览',
      navLabel: '概览',
      roles: ['LOTTERY_ADMIN']
    }
  },
  {
    path: 'activities',
    name: 'adminActivities',
    component: () => import('../views/admin/AdminActivitiesView.vue'),
    meta: {
      menuCode: 'OPERATIONS_CENTER',
      title: '活动运营',
      navLabel: '活动',
      roles: ['LOTTERY_ADMIN']
    }
  },
  {
    path: 'prizes',
    name: 'adminPrizes',
    component: () => import('../views/admin/AdminPrizeCenterView.vue'),
    meta: {
      menuCode: 'PRIZE_CENTER',
      title: '奖品中心',
      navLabel: '奖品',
      roles: ['LOTTERY_ADMIN']
    }
  },
  {
    path: 'records',
    name: 'adminRecords',
    component: () => import('../views/admin/AdminRecordsView.vue'),
    meta: {
      menuCode: 'LOTTERY_DRAW',
      title: '中奖记录',
      navLabel: '记录',
      roles: ['LOTTERY_ADMIN']
    }
  },
  {
    path: 'ai',
    name: 'adminAiAnalysis',
    component: () => import('../views/admin/AdminAiAnalysisView.vue'),
    meta: {
      menuCode: 'OPERATIONS_CENTER',
      title: 'AI 分析',
      navLabel: 'AI 分析',
      roles: ['LOTTERY_ADMIN']
    }
  },
  {
    path: 'profile',
    name: 'adminProfile',
    component: () => import('../views/admin/AdminProfileView.vue'),
    meta: {
      menuCode: 'ACCOUNT_PROFILE',
      title: '账号与权限',
      navLabel: '我的',
      roles: ['LOTTERY_ADMIN']
    }
  }
]

export const appRouteCatalog = [
  {
    path: 'home',
    name: 'mobileHome',
    component: () => import('../views/app/MobileHomeView.vue'),
    meta: {
      menuCode: 'DASHBOARD_OVERVIEW',
      title: '活动首页',
      navLabel: '首页',
      roles: ['LOTTERY_USER']
    }
  },
  {
    path: 'draw',
    name: 'mobileDraw',
    component: () => import('../views/app/MobileDrawView.vue'),
    meta: {
      menuCode: 'LOTTERY_DRAW',
      title: '幸运抽奖',
      navLabel: '抽奖',
      roles: ['LOTTERY_USER']
    }
  },
  {
    path: 'records',
    name: 'mobileRecords',
    component: () => import('../views/app/MobileRecordsView.vue'),
    meta: {
      menuCode: 'LOTTERY_DRAW',
      title: '我的记录',
      navLabel: '记录',
      roles: ['LOTTERY_USER']
    }
  },
  {
    path: 'profile',
    name: 'mobileProfile',
    component: () => import('../views/app/MobileProfileView.vue'),
    meta: {
      menuCode: 'ACCOUNT_PROFILE',
      title: '我的',
      navLabel: '我的',
      roles: ['LOTTERY_USER']
    }
  }
]

export const routeCatalog = [...adminRouteCatalog, ...appRouteCatalog]

export const staticRoutes = [
  { path: '/', redirect: '/app' },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminLayout,
    meta: { requiresAuth: true, roles: ['LOTTERY_ADMIN'] },
    children: []
  },
  {
    path: '/app',
    name: 'mobileApp',
    component: MobileAppLayout,
    meta: { requiresAuth: true, roles: ['LOTTERY_USER'] },
    children: []
  },
  { path: '/workspace', redirect: '/app' },
  { path: '/:pathMatch(.*)*', redirect: '/app' }
]
```

- [ ] **Step 3: Update router installer**

Update `index.js` so admin routes install under `admin` and app routes under `mobileApp`; `resolveHomeRoute()` returns `/admin/overview` for admins, otherwise `/app/home`.

- [ ] **Step 4: Build-check routing**

Run:

```bash
npm run build
```

Expected: route imports fail because new views do not exist yet. This is the expected intermediate failure before Task 2.

## Task 2: Layout Shells

**Files:**
- Create: `lottery-frontend/src/views/admin/AdminLayout.vue`
- Create: `lottery-frontend/src/views/app/MobileAppLayout.vue`

- [ ] **Step 1: Add `AdminLayout.vue`**

Create a layout that filters `adminRouteCatalog`, displays sidebar navigation, current route title, current user, and logout button.

- [ ] **Step 2: Add `MobileAppLayout.vue`**

Create a layout that filters `appRouteCatalog`, displays top title, bottom tabbar, nested router view, and logout via profile page only.

- [ ] **Step 3: Build-check shell imports**

Run:

```bash
npm run build
```

Expected: remaining failures are missing admin/app page components.

## Task 3: Admin Pages

**Files:**
- Create: `lottery-frontend/src/views/admin/AdminOverviewView.vue`
- Create: `lottery-frontend/src/views/admin/AdminActivitiesView.vue`
- Create: `lottery-frontend/src/views/admin/AdminPrizeCenterView.vue`
- Create: `lottery-frontend/src/views/admin/AdminRecordsView.vue`
- Create: `lottery-frontend/src/views/admin/AdminAiAnalysisView.vue`
- Create: `lottery-frontend/src/views/admin/AdminProfileView.vue`

- [ ] **Step 1: Add API-backed admin overview**

Use `fetchPrizeList()` and `fetchLotteryRecords(sessionState.profile.id, 8)` with loading/empty/error states.

- [ ] **Step 2: Add static activity operations MVP**

Use static activity cards to show current activity, draft activity, and future scheduling; do not invent backend API calls.

- [ ] **Step 3: Add admin prize center**

Use `fetchPrizeList()` and render stock, probability, and prize level in dense admin cards.

- [ ] **Step 4: Add admin records**

Use `fetchLotteryRecords(sessionState.profile.id, 20)` and render record status, prize name, level, and time.

- [ ] **Step 5: Add admin AI analysis page**

Move AI streaming analysis behavior from current `LotteryView.vue` into a standalone admin page.

- [ ] **Step 6: Add admin profile**

Render user ID, nickname, roles, auth source, and menu count from `sessionState`.

## Task 4: Mobile App Pages

**Files:**
- Create: `lottery-frontend/src/views/app/MobileHomeView.vue`
- Create: `lottery-frontend/src/views/app/MobileDrawView.vue`
- Create: `lottery-frontend/src/views/app/MobileRecordsView.vue`
- Create: `lottery-frontend/src/views/app/MobileProfileView.vue`

- [ ] **Step 1: Add mobile home**

Fetch prizes and records, show current event summary, prize count, recent draw count, and CTA to `/app/draw`.

- [ ] **Step 2: Add mobile draw**

Use current draw logic from `LotteryView.vue`: prize list, rolling highlight, draw action, result dialog, and record refresh.

- [ ] **Step 3: Add mobile records**

Fetch records and show mobile-friendly cards with empty/error/loading states.

- [ ] **Step 4: Add mobile profile**

Show current user info and logout button.

## Task 5: Visual System

**Files:**
- Modify: `lottery-frontend/src/style.css`

- [ ] **Step 1: Replace design tokens**

Update CSS variables to a neutral admin palette with blue-green primary and gold accent.

- [ ] **Step 2: Add admin layout styles**

Add `.admin-shell`, `.admin-sidebar`, `.admin-main`, `.admin-card`, `.admin-stat-grid`, `.admin-table-list`.

- [ ] **Step 3: Add mobile layout styles**

Add `.mobile-shell`, `.mobile-page`, `.mobile-tabbar`, `.mobile-hero`, `.mobile-prize-card`, `.draw-button`.

- [ ] **Step 4: Preserve existing auth styles**

Keep login usable while aligning token colors.

- [ ] **Step 5: Add responsive and safe-area rules**

Use `env(safe-area-inset-bottom)` for mobile bottom navigation and avoid content being hidden under the tabbar.

## Task 6: Verification

**Files:**
- Modify only if verification reveals defects.

- [ ] **Step 1: Build**

Run:

```bash
cd lottery-frontend
npm run build
```

Expected: `vite build` completes successfully.

- [ ] **Step 2: Start dev server**

Run:

```bash
npm run dev -- --host 127.0.0.1
```

Expected: Vite starts on port `9510` unless occupied.

- [ ] **Step 3: Browser desktop check**

Open `http://127.0.0.1:9510/admin/overview` and check that layout does not overlap at desktop width.

- [ ] **Step 4: Browser mobile check**

Open `http://127.0.0.1:9510/app/home` and `http://127.0.0.1:9510/app/draw` with a mobile viewport. Check bottom tabbar, draw CTA, and result modal spacing.

- [ ] **Step 5: Final status**

Run:

```bash
git status --short
```

Expected: only intentional frontend files and plan/spec files are modified or added; `.codegraph/daemon.pid` remains untracked and should not be committed.

## Self-Review

- Spec coverage: The plan covers route split, admin layout, mobile layout, admin pages, mobile pages, shared API/session reuse, visual system, responsive verification, and build/browser checks.
- Placeholder scan: No `TODO`, `TBD`, or unspecified implementation placeholders remain.
- Type consistency: Current project uses JavaScript despite local frontend spec recommending TypeScript; plan explicitly keeps JavaScript for this implementation to reduce migration risk.
