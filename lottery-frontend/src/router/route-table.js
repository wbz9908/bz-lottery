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
  {
    path: '/',
    redirect: '/app'
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: {
      public: true,
      title: '登录'
    }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminLayout,
    meta: {
      requiresAuth: true,
      roles: ['LOTTERY_ADMIN']
    },
    children: []
  },
  {
    path: '/app',
    name: 'mobileApp',
    component: MobileAppLayout,
    meta: {
      requiresAuth: true,
      roles: ['LOTTERY_USER']
    },
    children: []
  },
  {
    path: '/workspace',
    redirect: '/app'
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/app'
  }
]
