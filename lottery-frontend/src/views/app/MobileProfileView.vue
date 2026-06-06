<script setup>
import {useRouter} from 'vue-router'
import {sessionState, signOut} from '../../stores/session'

const router = useRouter()

async function logout() {
    await signOut()
    router.replace('/login')
}
</script>

<template>
  <div class="mobile-page">
    <section class="mobile-hero compact">
      <p class="eyebrow">我的</p>
      <h2>{{ sessionState.profile?.nickname || '当前用户' }}</h2>
      <p>管理登录状态和账号信息。</p>
    </section>

    <section class="mobile-card profile-card">
      <div>
        <span>用户名</span>
        <strong>{{ sessionState.profile?.username }}</strong>
      </div>
      <div>
        <span>邮箱</span>
        <strong>{{ sessionState.profile?.email || '未绑定' }}</strong>
      </div>
      <div>
        <span>认证来源</span>
        <strong>{{ sessionState.profile?.authSource || '未知' }}</strong>
      </div>
      <div>
        <span>当前角色</span>
        <div class="tag-row">
          <span v-for="role in sessionState.roles" :key="role" class="status-pill">{{ role }}</span>
        </div>
      </div>
      <button class="ghost-btn full-width" type="button" @click="logout">退出登录</button>
    </section>
  </div>
</template>
