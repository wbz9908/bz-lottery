<script setup>
import {computed, onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {fetchLotteryRecords, fetchPrizeList} from '../../api/lottery'
import {sessionState} from '../../stores/session'

const router = useRouter()
const loading = ref(false)
const error = ref('')
const prizes = ref([])
const records = ref([])
const totalStock = computed(() => prizes.value.reduce((sum, prize) => sum + Number(prize.availableStock ?? 0), 0))

async function loadHome() {
    loading.value = true
    error.value = ''
    try {
        const [prizeData, recordData] = await Promise.all([
            fetchPrizeList(),
            fetchLotteryRecords(sessionState.profile?.id, 5)
        ])
        prizes.value = prizeData
        records.value = recordData
    } catch (err) {
        error.value = err.message || '活动数据加载失败'
    } finally {
        loading.value = false
    }
}

onMounted(loadHome)
</script>

<template>
  <div class="mobile-page">
    <section class="mobile-hero">
      <p class="eyebrow">今日活动</p>
      <h2>幸运奖池已开启</h2>
      <p>查看当前奖池，点击抽奖按钮即可参与。结果会同步进入你的中奖记录。</p>
      <button class="primary-btn draw-cta" type="button" @click="router.push('/app/draw')">去抽奖</button>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在加载活动...</p>

    <section class="mobile-stat-row">
      <article><span>奖品</span><strong>{{ prizes.length }}</strong></article>
      <article><span>库存</span><strong>{{ totalStock }}</strong></article>
      <article><span>记录</span><strong>{{ records.length }}</strong></article>
    </section>

    <section class="mobile-card">
      <div class="section-header">
        <h3>奖池预览</h3>
        <span>{{ prizes.length }} 项</span>
      </div>
      <div v-if="!loading && !prizes.length" class="empty-state">暂无奖品</div>
      <div class="mobile-prize-list">
        <article v-for="prize in prizes.slice(0, 4)" :key="prize.id" class="mobile-prize-card">
          <span>{{ prize.prizeLevel }}</span>
          <strong>{{ prize.prizeName }}</strong>
        </article>
      </div>
    </section>
  </div>
</template>
