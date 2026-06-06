<script setup>
import {onMounted, ref} from 'vue'
import {fetchLotteryRecords} from '../../api/lottery'
import {sessionState} from '../../stores/session'

const loading = ref(false)
const error = ref('')
const records = ref([])

function formatTime(value) {
    return value ? new Date(value).toLocaleString('zh-CN', {hour12: false}) : '未知时间'
}

async function loadRecords() {
    loading.value = true
    error.value = ''
    try {
        records.value = await fetchLotteryRecords(sessionState.profile?.id, 30)
    } catch (err) {
        error.value = err.message || '记录加载失败'
    } finally {
        loading.value = false
    }
}

onMounted(loadRecords)
</script>

<template>
  <div class="mobile-page">
    <section class="mobile-hero compact">
      <p class="eyebrow">我的记录</p>
      <h2>抽奖历史</h2>
      <p>查看最近参与和中奖情况。</p>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在加载记录...</p>
    <div v-if="!loading && !records.length" class="empty-state">暂无抽奖记录</div>

    <section class="mobile-record-list">
      <article v-for="record in records" :key="record.recordNo" class="mobile-record-item">
        <div>
          <strong>{{ record.prizeName || '未中奖' }}</strong>
          <span>{{ record.prizeLevel || '未命中' }}</span>
        </div>
        <small>{{ formatTime(record.createdAt) }}</small>
      </article>
    </section>
  </div>
</template>
