
<template>
  <section class="section">
    <div class="section__head">
      <h2>产品包装</h2>
      <p>灵活的包装组合，覆盖实验、小试到工业大批量需求。</p>
    </div>
    <div class="cards">
      <div class="card" v-for="item in packagingCards" :key="item.id">
        <div class="card__image" :style="{ backgroundImage: `url(${item.image})` }"></div>
        <div class="card__title">{{ item.title }}</div>
        <p class="card__desc">{{ item.desc }}</p>
      </div>
    </div>
  </section>
</template>

<script setup>
const packagingCards = [
  {
    id: 1,
    title: '铝箔小包装',
    desc: '100g / 500g / 1kg，防潮避光，适合样品与小试。',
    image: 'https://images.unsplash.com/photo-1582719478248-74d0b3c7e593?auto=format&fit=crop&w=600&q=80',
  },
  {
    id: 2,
    title: '纸板桶覆膜',
    desc: '25kg 纸板桶，内衬 PE 袋，常规固体产品优选。',
    image: 'https://images.unsplash.com/photo-1506617420156-8e4536971650?auto=format&fit=crop&w=600&q=80',
  },
  {
    id: 3,
    title: '液体化工桶',
    desc: '20L/200L 桶装，提供危化运输支持与合规标签。',
    image: 'https://images.unsplash.com/photo-1501004318641-b39e6451bec6?auto=format&fit=crop&w=600&q=80',
  },
  {
    id: 4,
    title: '定制分装与贴标',
    desc: '按客户规格分装小袋/小瓶，支持条码与品牌贴标。',
    image: 'https://images.unsplash.com/photo-1497534446932-c925b458314e?auto=format&fit=crop&w=600&q=80',
  },
];
</script>

<style scoped>
.section {
  padding: 72px 6vw;
  min-height: 80vh;
}

.section__head {
  max-width: 720px;
  margin-bottom: 28px;
}

.section__head h2 {
  margin: 0 0 10px;
  font-size: 28px;
}

.section__head p {
  margin: 0;
  color: #4b5563;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}

.card__image {
  height: 140px;
  background-size: cover;
  background-position: center;
}

.card__title {
  font-weight: 700;
  margin: 12px 14px 6px;
}

.card__desc {
  color: #4b5563;
  margin: 0 14px 16px;
}
</style>
