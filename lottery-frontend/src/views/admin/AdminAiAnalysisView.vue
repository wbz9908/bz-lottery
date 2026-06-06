<script setup>
import {computed, ref} from 'vue'
import {streamLotteryUserDataAnalysis} from '../../api/lottery'
import {sessionState} from '../../stores/session'

const loading = ref(false)
const error = ref('')
const focus = ref('')
const meta = ref(null)
const metrics = ref(null)
const reasoning = ref('')
const draft = ref('')
const result = ref(null)
let requestToken = 0

const displayMetrics = computed(() => result.value?.metrics ?? metrics.value)
const stageText = computed(() => {
    if (!loading.value) {
        return result.value ? '分析完成' : '待分析'
    }
    if (draft.value.trim()) {
        return '正在生成分析结论'
    }
    if (reasoning.value.trim()) {
        return '正在接收推理过程'
    }
    if (metrics.value) {
        return '指标已就绪'
    }
    return '正在打开分析流'
})

function normalizeResult(payload) {
    return {
        status: payload.status,
        model: payload.model,
        overview: payload.overview,
        insights: payload.insights ?? [],
        suggestions: payload.suggestions ?? [],
        metrics: {
            totalDrawCount: payload.metrics?.totalDrawCount ?? 0,
            activeDays: payload.metrics?.activeDays ?? 0,
            recent30DayDrawCount: payload.metrics?.recent30DayDrawCount ?? 0,
            trendSummary: payload.metrics?.trendSummary ?? 'flat',
            highestPrizeLevel: payload.metrics?.highestPrizeLevel ?? '未知',
            highTierHitCount: payload.metrics?.highTierHitCount ?? 0,
            pendingReviewCount: payload.metrics?.pendingReviewCount ?? 0,
            prizeLevelDistribution: payload.metrics?.prizeLevelDistribution ?? []
        },
        generatedAt: payload.generatedAt ? new Date(payload.generatedAt).toLocaleString('zh-CN', {hour12: false}) : ''
    }
}

function reset() {
    error.value = ''
    meta.value = null
    metrics.value = null
    reasoning.value = ''
    draft.value = ''
    result.value = null
}

async function analyze() {
    if (loading.value || !sessionState.profile?.id) {
        return
    }
    const token = Date.now()
    requestToken = token
    loading.value = true
    reset()

    try {
        await streamLotteryUserDataAnalysis(sessionState.profile.id, focus.value.trim(), {
            onMeta(payload) {
                if (requestToken === token) meta.value = payload
            },
            onMetrics(payload) {
                if (requestToken === token) metrics.value = normalizeResult({metrics: payload}).metrics
            },
            onReasoning(text) {
                if (requestToken === token && text) reasoning.value += text
            },
            onDelta(text) {
                if (requestToken === token && text) draft.value += text
            },
            onComplete(payload) {
                if (requestToken === token) result.value = normalizeResult(payload)
            },
            onError(message) {
                if (requestToken === token) error.value = message || 'AI 分析暂时不可用'
            }
        })
        if (requestToken === token && !result.value && !error.value) {
            error.value = '分析流已结束，但没有返回最终结论'
        }
    } catch (err) {
        if (requestToken === token) {
            error.value = err.message || 'AI 分析暂时不可用'
        }
    } finally {
        if (requestToken === token) {
            loading.value = false
        }
    }
}
</script>

<template>
  <div class="admin-page">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">AI 分析</p>
        <h2>抽奖用户数据洞察</h2>
        <p>读取抽奖指标并生成运营分析，适合演示项目的 AI 集成能力。</p>
      </div>
      <button :disabled="loading" class="primary-btn" type="button" @click="analyze">
        {{ loading ? '分析中...' : '开始分析' }}
      </button>
    </section>

    <section class="admin-card ai-control-card">
      <label class="field-label" for="admin-analysis-focus">关注点</label>
      <input
          id="admin-analysis-focus"
          v-model="focus"
          class="user-input"
          maxlength="50"
          placeholder="例如：近 30 天活跃度是否提升？"
          type="text"
      />
      <p class="ai-stream-status">
        <span class="status-pill">{{ meta?.model || '抽奖 AI' }}</span>
        <small>{{ stageText }}</small>
      </p>
    </section>

    <p v-if="error" class="page-message">{{ error }}</p>

    <section v-if="displayMetrics" class="admin-stat-grid">
      <article class="admin-stat-card"><span>累计抽奖</span><strong>{{ displayMetrics.totalDrawCount }}</strong></article>
      <article class="admin-stat-card"><span>活跃天数</span><strong>{{ displayMetrics.activeDays }}</strong></article>
      <article class="admin-stat-card"><span>近 30 天</span><strong>{{ displayMetrics.recent30DayDrawCount }}</strong></article>
      <article class="admin-stat-card"><span>最高奖级</span><strong>{{ displayMetrics.highestPrizeLevel }}</strong></article>
    </section>

    <section v-if="reasoning || draft || result" class="admin-card ai-result-card">
      <div v-if="reasoning">
        <h3>推理过程</h3>
        <pre class="ai-stream-block">{{ reasoning }}</pre>
      </div>
      <div v-if="draft && !result">
        <h3>生成草稿</h3>
        <pre class="ai-stream-block">{{ draft }}</pre>
      </div>
      <template v-if="result">
        <p class="ai-overview">{{ result.overview }}</p>
        <div class="admin-grid">
          <article class="mini-card">
            <h3>关键洞察</h3>
            <ul><li v-for="item in result.insights" :key="item">{{ item }}</li></ul>
          </article>
          <article class="mini-card">
            <h3>优化建议</h3>
            <ul><li v-for="item in result.suggestions" :key="item">{{ item }}</li></ul>
          </article>
        </div>
      </template>
    </section>
  </div>
</template>
