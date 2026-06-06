<script setup>
import {computed} from 'vue'
import {RouterLink, RouterView, useRoute} from 'vue-router'
import {appRouteCatalog} from '../../router'
import {hasMenu, hasRole} from '../../stores/session'

const route = useRoute()

const navItems = computed(() => appRouteCatalog.filter((item) => hasRole(item.meta.roles ?? []) && hasMenu(item.meta.menuCode)))
</script>

<template>
  <div class="mobile-shell">
    <header class="mobile-topbar">
      <div>
        <p class="eyebrow">Mobile Web</p>
        <h1>{{ route.meta.title || '抽奖活动' }}</h1>
      </div>
    </header>

    <main class="mobile-content">
      <RouterView/>
    </main>

    <nav class="mobile-tabbar">
      <RouterLink
          v-for="item in navItems"
          :key="item.name"
          :class="{ active: route.name === item.name }"
          :to="`/app/${item.path}`"
          class="mobile-tab"
      >
        <span>{{ item.meta.navLabel }}</span>
      </RouterLink>
    </nav>
  </div>
</template>
