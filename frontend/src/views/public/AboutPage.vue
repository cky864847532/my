<template>
  <section class="section">
    <div class="section__head">
      <h2>关于我们</h2>
      <p>我们专注植物生长调节剂与精细化工，提供稳定合规的产品、完善的售后服务与专业技术支持。</p>
      <p>主要市场覆盖英国、美国、西班牙、澳大利亚、新西兰、荷兰、哥伦比亚、墨西哥、印度、日本、韩国、越南等国家和地区。</p>
    </div>
    <div class="about-grid">
      <div class="about-card" v-for="item in aboutItems" :key="item.title">
        <div class="about-card__icon">{{ item.icon }}</div>
        <h3>{{ item.title }}</h3>
        <p>{{ item.desc }}</p>
      </div>
    </div>
  </section>
</template>

<script setup>
const aboutItems = [
  { title: '专业团队', desc: '多年行业经验的销售与技术团队，提供产品选型与法规咨询。', icon: '⚙️' },
  { title: '安全合规', desc: '严格遵守安全与环保要求，提供 MSDS 与检测报告。', icon: '🛡️' },
  { title: '高效配送', desc: '与多家物流合作，支持定时送达与特种运输。', icon: '🚚' },
  { title: '全球客户', desc: '产品销往欧美、东南亚等市场，长期合作客户稳定。', icon: '🌍' },
];
</script>

<style scoped>
.section {
  padding: 72px 6vw;
  min-height: 80vh;
}

.section__head {
  max-width: 820px;
  margin-bottom: 28px;
}

.section__head h2 {
  margin: 0 0 10px;
  font-size: 28px;
}

.section__head p {
  margin: 4px 0;
  color: #4b5563;
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
}

.about-card {
  background: #fff;
  padding: 18px;
  border-radius: 14px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.04);
}

.about-card__icon {
  width: 40px;
  height: 40px;
  display: grid;
  place-items: center;
  background: #e8f7ef;
  color: #0ea371;
  border-radius: 12px;
  margin-bottom: 10px;
  font-size: 18px;
}

.about-card h3 {
  margin: 0 0 8px;
}

.about-card p {
  margin: 0;
  color: #4b5563;
}
</style>
