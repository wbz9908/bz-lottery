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
        records.value = await fetchLotteryRecords(sessionState.profile?.id, 20)
    } catch (err) {
        error.value = err.message || '中奖记录加载失败'
    } finally {
        loading.value = false
    }
}

onMounted(loadRecords)
</script>

<template>
  <div class="admin-page">
    <section class="admin-hero compact">
      <div>
        <p class="eyebrow">中奖记录</p>
        <h2>最近抽奖明细</h2>
      </div>
      <button class="primary-btn" type="button" @click="loadRecords">刷新记录</button>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在加载记录...</p>
    <div v-if="!loading && !records.length" class="empty-state">暂无抽奖记录</div>

    <section class="admin-card">
      <div class="admin-table-list">
        <article v-for="record in records" :key="record.recordNo" class="admin-table-row">
          <div>
            <strong>{{ record.prizeName || '未中奖' }}</strong>
            <span>{{ record.recordNo }}</span>
          </div>
          <div>
            <strong>{{ record.prizeLevel || '未命中' }}</strong>
            <span>{{ formatTime(record.createdAt) }}</span>
          </div>
          <div>
            <strong>{{ record.drawStatus }}</strong>
            <span>{{ record.drawRemark || '暂无备注' }}</span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>
