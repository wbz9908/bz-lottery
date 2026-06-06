<script setup>
import {sessionState} from '../../stores/session'
</script>

<template>
  <div class="admin-page">
    <section class="admin-hero compact">
      <div>
        <p class="eyebrow">账号与权限</p>
        <h2>{{ sessionState.profile?.nickname || '当前用户' }}</h2>
      </div>
    </section>

    <section class="admin-card profile-card">
      <div>
        <span>用户 ID</span>
        <strong>{{ sessionState.profile?.id }}</strong>
      </div>
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
      <div class="profile-wide">
        <span>当前角色</span>
        <div class="tag-row">
          <span v-for="role in sessionState.roles" :key="role" class="status-pill">{{ role }}</span>
        </div>
      </div>
    </section>
  </div>
</template>
