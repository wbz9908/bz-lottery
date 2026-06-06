<script setup>
import {computed, onMounted, ref} from 'vue'
import {fetchLotteryRecords, fetchPrizeList} from '../../api/lottery'
import {sessionState} from '../../stores/session'

const loading = ref(false)
const error = ref('')
const prizes = ref([])
const records = ref([])

const totalStock = computed(() => prizes.value.reduce((sum, prize) => sum + Number(prize.availableStock ?? 0), 0))
const hitRecords = computed(() => records.value.filter((record) => record.prizeId))
const hitRate = computed(() => {
    if (!records.value.length) {
        return '0%'
    }
    return `${Math.round((hitRecords.value.length / records.value.length) * 100)}%`
})

async function loadDashboard() {
    loading.value = true
    error.value = ''
    try {
        const [prizeData, recordData] = await Promise.all([
            fetchPrizeList(),
            fetchLotteryRecords(sessionState.profile?.id, 8)
        ])
        prizes.value = prizeData
        records.value = recordData
    } catch (err) {
        error.value = err.message || '运营概览加载失败'
    } finally {
        loading.value = false
    }
}

onMounted(loadDashboard)
</script>

<template>
  <div class="admin-page">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">运营态势</p>
        <h2>抽奖活动数据总览</h2>
        <p>聚合奖品、库存和最近抽奖记录，用于快速判断活动是否正常运行。</p>
      </div>
      <button class="primary-btn" type="button" @click="loadDashboard">刷新数据</button>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在加载运营数据...</p>

    <section class="admin-stat-grid">
      <article class="admin-stat-card">
        <span>奖品数</span>
        <strong>{{ prizes.length }}</strong>
      </article>
      <article class="admin-stat-card">
        <span>可用库存</span>
        <strong>{{ totalStock }}</strong>
      </article>
      <article class="admin-stat-card">
        <span>最近抽奖</span>
        <strong>{{ records.length }}</strong>
      </article>
      <article class="admin-stat-card">
        <span>命中率</span>
        <strong>{{ hitRate }}</strong>
      </article>
    </section>

    <section class="admin-card">
      <div class="section-header">
        <h3>最近中奖记录</h3>
        <span>{{ records.length }} 条</span>
      </div>
      <div v-if="!loading && !records.length" class="empty-state">暂无抽奖记录</div>
      <div v-else class="admin-table-list">
        <article v-for="record in records" :key="record.recordNo" class="admin-table-row">
          <div>
            <strong>{{ record.prizeName || '未中奖' }}</strong>
            <span>{{ record.recordNo }}</span>
          </div>
          <div>
            <strong>{{ record.prizeLevel || '未命中' }}</strong>
            <span>{{ record.drawRemark || '暂无备注' }}</span>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>
