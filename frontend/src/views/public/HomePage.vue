<template>
  <section class="hero-block">
    <div class="hero-left">
      <p class="eyebrow">绿色化学 · 安全环保 · 稳定供应</p>
      <h1>专注精细化工与包装解决方案</h1>
      <p class="lead">从基础化工原料到定制包装服务，台州凯绿化工为客户提供稳定的产品、规范的仓储与快捷的物流体系。</p>
      <div class="hero-cta">
        <el-button type="primary" size="large" @click="go('/product-center')">查看产品中心</el-button>
        <el-button size="large" @click="go('/feedback')">提交需求</el-button>
      </div>
      <div class="metrics">
        <div class="metric"><span class="metric__value">20+</span><span class="metric__label">行业应用场景</span></div>
        <div class="metric"><span class="metric__value">3000㎡</span><span class="metric__label">仓储与包装车间</span></div>
        <div class="metric"><span class="metric__value">7×24</span><span class="metric__label">售前售后支持</span></div>
      </div>
    </div>
    <div class="hero-card">
      <div class="hero-badge">品质追溯</div>
      <h3>从原料到交付，批次可追踪</h3>
      <p>统一质检标准、批次记录与安全包装，保障产品稳定与运输安全。</p>
      <ul>
        <li>批次留样与检测报告</li>
        <li>UN 认证包装方案</li>
        <li>覆盖华东与华南的配送网络</li>
      </ul>
    </div>
  </section>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
const go = (path) => router.push(path);
</script>

<style scoped>
.hero-block {
  padding: 60px 6vw;
  display: grid;
  grid-template-columns: 1.2fr 0.9fr;
  gap: 40px;
  align-items: center;
  min-height: 60vh;
}

.eyebrow {
  letter-spacing: 2px;
  text-transform: uppercase;
  color: #0ea371;
  font-weight: 700;
  margin-bottom: 12px;
}

h1 {
  font-size: 38px;
  margin: 0 0 14px;
  line-height: 1.2;
}

.lead {
  color: #4b5563;
  margin-bottom: 20px;
}

.hero-cta {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 18px;
}

.metrics {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.metric {
  padding: 12px 14px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.04);
  min-width: 120px;
}

.metric__value {
  display: block;
  font-size: 20px;
  font-weight: 800;
  color: #0ea371;
}

.metric__label {
  color: #6b7280;
  font-size: 12px;
}

.hero-card {
  background: linear-gradient(150deg, #0b9248 0%, #0ea371 45%, #16b28f 100%);
  color: #fff;
  padding: 24px;
  border-radius: 18px;
  box-shadow: 0 16px 40px rgba(14, 163, 113, 0.35);
}

.hero-badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
  font-size: 12px;
  margin-bottom: 10px;
}

.hero-card h3 {
  margin: 0 0 10px;
}

.hero-card p {
  margin: 0 0 12px;
  color: rgba(255, 255, 255, 0.9);
}

.hero-card ul {
  margin: 0;
  padding-left: 16px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
}

@media (max-width: 960px) {
  .hero-block {
    grid-template-columns: 1fr;
  }
}
</style>
