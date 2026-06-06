<script setup>
import {computed} from 'vue'
import {RouterLink, RouterView, useRoute, useRouter} from 'vue-router'
import {adminRouteCatalog} from '../../router'
import {hasMenu, hasRole, sessionState, signOut} from '../../stores/session'

const route = useRoute()
const router = useRouter()

const navItems = computed(() => adminRouteCatalog.filter((item) => hasRole(item.meta.roles ?? []) && hasMenu(item.meta.menuCode)))

async function logout() {
    await signOut()
    router.replace('/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="admin-brand" to="/admin/overview">
        <span class="brand-mark">BZ</span>
        <span>
          <strong>抽奖运营台</strong>
          <small>Lottery Console</small>
        </span>
      </RouterLink>

      <nav class="admin-nav">
        <RouterLink
            v-for="item in navItems"
            :key="item.name"
            :class="{ active: route.name === item.name }"
            :to="`/admin/${item.path}`"
            class="admin-nav-link"
        >
          {{ item.meta.navLabel }}
        </RouterLink>
      </nav>
    </aside>

    <section class="admin-main">
      <header class="admin-topbar">
        <div>
          <p class="eyebrow">PC Web</p>
          <h1>{{ route.meta.title || '运营后台' }}</h1>
        </div>
        <div class="admin-user">
          <div>
            <strong>{{ sessionState.profile?.nickname || '未命名用户' }}</strong>
            <span>{{ sessionState.roles.join(' / ') }}</span>
          </div>
          <button class="ghost-btn" type="button" @click="logout">退出</button>
        </div>
      </header>

      <main class="admin-content">
        <RouterView/>
      </main>
    </section>
  </div>
</template>
