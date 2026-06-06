<script setup>
import {onMounted, ref} from 'vue'
import {fetchPrizeList} from '../../api/lottery'

const loading = ref(false)
const error = ref('')
const prizes = ref([])

async function loadPrizes() {
    loading.value = true
    error.value = ''
    try {
        prizes.value = await fetchPrizeList()
    } catch (err) {
        error.value = err.message || '奖品数据加载失败'
    } finally {
        loading.value = false
    }
}

onMounted(loadPrizes)
</script>

<template>
  <div class="admin-page">
    <section class="admin-hero compact">
      <div>
        <p class="eyebrow">奖品中心</p>
        <h2>奖池库存与概率</h2>
      </div>
      <button class="primary-btn" type="button" @click="loadPrizes">刷新奖池</button>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在加载奖池...</p>
    <div v-if="!loading && !prizes.length" class="empty-state">暂无奖品配置</div>

    <section class="admin-grid">
      <article v-for="prize in prizes" :key="prize.id" class="admin-card prize-admin-card">
        <span class="status-pill">{{ prize.prizeLevel }}</span>
        <h3>{{ prize.prizeName }}</h3>
        <div class="prize-admin-meta">
          <span>库存 <strong>{{ prize.availableStock }}</strong></span>
          <span>概率 <strong>{{ prize.probability }}</strong></span>
        </div>
      </article>
    </section>
  </div>
</template>
