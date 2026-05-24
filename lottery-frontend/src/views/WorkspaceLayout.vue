<script setup>
import {computed, ref, watch} from 'vue'
import {RouterLink, RouterView, useRoute, useRouter} from 'vue-router'
import {routeCatalog} from '../router'
import {hasMenu, hasRole, sessionState, signOut} from '../stores/session'

const route = useRoute()
const router = useRouter()
const mobileNavOpen = ref(false)

const navItems = computed(() => routeCatalog.filter((item) => hasRole(item.meta.roles ?? []) && hasMenu(item.meta.menuCode)))

watch(
    () => route.fullPath,
    () => {
      mobileNavOpen.value = false
    }
)

async function logout() {
  await signOut()
  mobileNavOpen.value = false
  router.replace('/login')
}
</script>

<template>
  <div class="workspace-shell">
    <header class="topnav">
      <div class="topnav-brand">
        <strong>抽奖控制台</strong>
      </div>

      <button
          v-if="navItems.length"
          class="topnav-toggle"
          type="button"
          @click="mobileNavOpen = !mobileNavOpen"
      >
        {{ mobileNavOpen ? '✕' : '☰' }}
      </button>

      <div v-if="mobileNavOpen" class="topnav-mobile-overlay open" @click="mobileNavOpen = false"></div>

      <nav :class="{ open: mobileNavOpen }" class="topnav-links">
        <RouterLink
            v-for="item in navItems"
            :key="item.name"
            :class="{ active: route.name === item.name }"
            :to="`/workspace/${item.path}`"
            class="nav-link"
        >
          <span>{{ item.meta.navLabel }}</span>
        </RouterLink>
      </nav>

      <div class="topnav-user">
        <div class="topnav-user-info">
          <strong>{{ sessionState.profile?.nickname }}</strong>
          <span>{{ sessionState.roles.join(' / ') }}</span>
        </div>
        <button class="ghost-btn" @click="logout">退出</button>
      </div>
    </header>

    <section class="workspace-main">
      <header class="workspace-header">
        <div>
          <p class="eyebrow">受保护工作区</p>
          <h1>{{ route.meta.title || '工作台' }}</h1>
        </div>
      </header>

      <main class="workspace-content">
        <RouterView/>
      </main>
    </section>
  </div>
</template>
