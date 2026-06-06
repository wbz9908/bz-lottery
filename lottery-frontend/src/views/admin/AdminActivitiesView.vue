<template>
  <div class="admin-page">
    <section class="admin-hero">
      <div>
        <p class="eyebrow">活动运营</p>
        <h2>活动配置与状态看板</h2>
        <p>第一版先展示运营结构，后续可接入活动 CRUD、上下架、时间窗口和参与规则配置。</p>
      </div>
    </section>

    <section class="admin-grid">
      <article class="admin-card activity-card active">
        <span class="status-pill success">进行中</span>
        <h3>端午幸运抽奖</h3>
        <p>面向登录用户开放，支持单次抽奖、中奖记录查询和结果审核。</p>
        <dl>
          <div><dt>参与规则</dt><dd>每人每日 1 次</dd></div>
          <div><dt>活动状态</dt><dd>奖池库存正常</dd></div>
        </dl>
      </article>
      <article class="admin-card activity-card">
        <span class="status-pill warning">待配置</span>
        <h3>年会抽奖模板</h3>
        <p>保留为后续接单模板，可扩展为大屏抽奖、签到抽奖和名单导入。</p>
        <dl>
          <div><dt>参与规则</dt><dd>名单导入</dd></div>
          <div><dt>活动状态</dt><dd>待接入接口</dd></div>
        </dl>
      </article>
    </section>
  </div>
</template>
