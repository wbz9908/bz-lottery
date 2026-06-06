<script setup>
import {computed, onBeforeUnmount, onMounted, ref} from 'vue'
import {drawLottery, fetchLotteryRecords, fetchPrizeList} from '../../api/lottery'
import {sessionState} from '../../stores/session'

const colors = ['#12b886', '#228be6', '#f59f00', '#e8590c', '#7950f2', '#0ca678']
const loading = ref(false)
const error = ref('')
const prizes = ref([])
const records = ref([])
const highlightedPrizeId = ref(null)
const latestResult = ref(null)
const showResult = ref(false)
const drawing = ref(false)
let intervalId = null

const resultPrize = computed(() => {
    if (!latestResult.value) {
        return prizes.value.find((prize) => prize.id === highlightedPrizeId.value) ?? prizes.value[0] ?? null
    }
    return prizes.value.find((prize) => prize.id === latestResult.value.prizeId) ?? {
        prizeName: latestResult.value.prizeName ?? '未中奖',
        prizeLevel: latestResult.value.prizeLevel ?? '未命中'
    }
})

function decoratePrize(prize, index) {
    return {...prize, color: colors[index % colors.length]}
}

function startRolling() {
    let index = Math.max(prizes.value.findIndex((prize) => prize.id === highlightedPrizeId.value), 0)
    intervalId = window.setInterval(() => {
        index = (index + 1) % prizes.value.length
        highlightedPrizeId.value = prizes.value[index].id
    }, 100)
}

function stopRolling() {
    if (intervalId) {
        window.clearInterval(intervalId)
        intervalId = null
    }
}

async function loadDrawData() {
    loading.value = true
    error.value = ''
    try {
        const [prizeData, recordData] = await Promise.all([
            fetchPrizeList(),
            fetchLotteryRecords(sessionState.profile?.id, 5)
        ])
        prizes.value = prizeData.map(decoratePrize)
        records.value = recordData
        highlightedPrizeId.value = prizes.value[0]?.id ?? null
    } catch (err) {
        error.value = err.message || '抽奖数据加载失败'
    } finally {
        loading.value = false
    }
}

async function handleDraw() {
    if (drawing.value || !prizes.value.length) {
        return
    }
    drawing.value = true
    showResult.value = false
    error.value = ''
    startRolling()

    try {
        const result = await drawLottery(sessionState.profile.id, `REQ-${sessionState.profile.id}-${Date.now()}`)
        latestResult.value = result
        const matchedPrize = prizes.value.find((prize) => prize.id === result.prizeId)
        window.setTimeout(async () => {
            stopRolling()
            highlightedPrizeId.value = matchedPrize?.id ?? prizes.value[0]?.id ?? null
            await loadDrawData()
            showResult.value = true
            drawing.value = false
        }, 1500)
    } catch (err) {
        stopRolling()
        drawing.value = false
        error.value = err.message || '抽奖失败，请稍后重试'
    }
}

onMounted(loadDrawData)
onBeforeUnmount(stopRolling)
</script>

<template>
  <div class="mobile-page draw-page">
    <section class="mobile-hero draw-hero">
      <p class="eyebrow">幸运抽奖</p>
      <h2>点击按钮，看看今天的手气</h2>
      <p>奖池滚动结束后会展示本次抽奖结果。</p>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>
    <p v-if="loading" class="state-text">正在准备奖池...</p>

    <section class="mobile-prize-board">
      <article
          v-for="prize in prizes"
          :key="prize.id"
          :class="{ active: prize.id === highlightedPrizeId }"
          :style="{ '--accent': prize.color }"
          class="mobile-prize-card"
      >
        <span>{{ prize.prizeLevel }}</span>
        <strong>{{ prize.prizeName }}</strong>
        <small>库存 {{ prize.availableStock }}</small>
      </article>
    </section>

    <button :disabled="drawing || loading || !prizes.length" class="draw-button" type="button" @click="handleDraw">
      {{ drawing ? '抽奖中...' : '立即抽奖' }}
    </button>

    <section class="mobile-card">
      <div class="section-header">
        <h3>最近记录</h3>
        <span>{{ records.length }} 条</span>
      </div>
      <div v-if="!records.length" class="empty-state">暂无记录</div>
      <article v-for="record in records" :key="record.recordNo" class="mobile-record-item">
        <div>
          <strong>{{ record.prizeName || '未中奖' }}</strong>
          <span>{{ record.prizeLevel || '未命中' }}</span>
        </div>
        <small>{{ record.drawRemark || '暂无备注' }}</small>
      </article>
    </section>

    <transition name="result-pop">
      <div v-if="showResult && resultPrize" class="result-mask" @click.self="showResult = false">
        <div class="result-dialog mobile-result-dialog">
          <p>本次抽奖结果</p>
          <h3>{{ resultPrize.prizeLevel || '未命中' }}</h3>
          <strong>{{ resultPrize.prizeName || '未中奖' }}</strong>
          <button class="primary-btn result-btn" type="button" @click="showResult = false">我知道了</button>
        </div>
      </div>
    </transition>
  </div>
</template>
