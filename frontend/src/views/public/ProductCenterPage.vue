<template>
  <section class="section section--alt">
    <div class="section__head">
      <h2>产品中心</h2>
      <p>精选基础化工品、助剂与特种材料，支持按需定制包装与规格。</p>
    </div>
    <div class="layout">
      <div class="list">
        <div class="list__title">产品列表</div>
        <div class="list__items">
          <button
            v-for="item in productCards"
            :key="item.id"
            class="list__item"
            :class="{ active: selected?.id === item.id }"
            type="button"
            @click="openDetail(item)"
          >
            <span class="list__name">{{ item.name }}</span>
          </button>
        </div>
      </div>

      <div class="cards">
        <div class="card" v-for="item in productCards" :key="item.id" @click="openDetail(item)">
          <div class="card__image" :style="{ backgroundImage: `url(${item.image})` }"></div>
          <div class="card__title">{{ item.name }}</div>
        </div>
      </div>
    </div>

    <el-dialog
      v-model="detailVisible"
      width="950px"
      :show-close="true"
      :title="selected?.name || '产品详情'"
      class="detail-dialog"
    >
      <div v-if="selected" class="dialog__body">
        <img class="dialog__image" :src="selected.image" :alt="selected.name" />
        <p class="dialog__line"><strong>中文名称：</strong>{{ selected.name }}</p>
        <p class="dialog__line"><strong>CAS号：</strong>{{ selected.cas }}</p>
        <p class="dialog__line"><strong>分子式：</strong>{{ selected.formula }}</p>
        <p class="dialog__line"><strong>分子量：</strong>{{ selected.mw }}</p>
        <p class="dialog__line"><strong>结构式：</strong>{{ selected.structure }}</p>
        <p class="dialog__line"><strong>理化性质：</strong>{{ selected.properties }}</p>
        <p class="dialog__line"><strong>产品规格：</strong>{{ selected.specs }}</p>
        <p class="dialog__line"><strong>用途：</strong>{{ selected.usage }}</p>
        <p class="dialog__line"><strong>应用技术：</strong>{{ selected.tech }}</p>
        <p class="dialog__line"><strong>包装规格：</strong>{{ selected.packaging }}</p>
      </div>
    </el-dialog>
  </section>
</template>

<script setup>
import { ref } from 'vue';

const productCards = [
  {
    id: 1,
    name: '吲哚丁酸 (IBA)',
    shortDesc: '常用植物生长调节剂，促根促生长。',
    cas: '133-32-4',
    formula: 'C12H13NO2',
    mw: '203.24',
    structure: '芳香族弱酸类，白色结晶粉末。',
    properties: '熔点约 125℃，易溶于有机溶剂，水中溶解度低。',
    specs: '含量≥98%，水分≤0.5%，包装净重 25kg/纸板桶。',
    usage: '园艺、林业、果树育苗，促进插条生根，提高存活率。',
    tech: '可与萘乙酸联合使用，按建议浓度兑水浸泡或蘸粉。',
    packaging: '25kg 纸板桶/覆膜袋，可按需分装小袋。',
    image: 'http://kaiyuagro.com/data/attachment/201902/26/061a63c6d4316e217463e06542fbf7c5.jpg',
  },
  {
    id: 2,
    name: '赤霉素 (GA3)',
    shortDesc: '提升作物伸长生长、打破休眠的植物生长调节剂。',
    cas: '77-06-5',
    formula: 'C19H22O6',
    mw: '346.37',
    structure: '四环二萜类化合物，白色结晶粉末。',
    properties: '熔点约 233℃，微溶于水，易溶于乙醇、丙酮。',
    specs: '含量≥90%，水分≤1.0%，可提供 90% TC 或制剂。',
    usage: '水稻、小麦、果蔬促花促梢，打破种子休眠。',
    tech: '按作物配方稀释喷雾，注意避光保存。',
    packaging: '1kg 铝箔袋/25kg 纸板桶，支持小包装。',
    image: 'https://images.unsplash.com/photo-1502741338009-cac2772e18bc?auto=format&fit=crop&w=600&q=80',
  },
  {
    id: 3,
    name: '生根粉混配剂',
    shortDesc: '复配型插条生根促进剂，广谱易用。',
    cas: '—',
    formula: '复配制剂',
    mw: '—',
    structure: '浅色粉末，含 IBA、NAA 等活性组分。',
    properties: '易分散于水，稳定性佳，低气味。',
    specs: '含量按配方定制，常规 0.1% / 0.2% 等规格。',
    usage: '绿化苗木、果树、花卉插条蘸粉或浸泡。',
    tech: '插条基部均匀蘸粉或浸泡 3-10 秒后扦插。',
    packaging: '100g/500g/1kg 小包装，可定制大包装。',
    image: 'https://images.unsplash.com/photo-1506617420156-8e4536971650?auto=format&fit=crop&w=600&q=80',
  },
  {
    id: 4,
    name: '萘乙酸钠 (NAA)',
    shortDesc: '促进根系发育，广泛应用的生长调节剂。',
    cas: '61-67-6',
    formula: 'C12H9NaO3',
    mw: '224.19',
    structure: '白色或类白色粉末，弱芳香气味。',
    properties: '微溶于水，易溶于醇类，稳定性好。',
    specs: '含量≥98%，水分≤1.0%，常规 1kg/25kg 包装。',
    usage: '果树、蔬菜、苗木促生根、疏果与坐果调节。',
    tech: '喷雾或浸蘸应用，注意按作物说明稀释。',
    packaging: '1kg 铝箔袋/25kg 纸板桶，可小袋分装。',
    image: 'https://images.unsplash.com/photo-1582719478248-74d0b3c7e593?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 5,
    name: '6-苄基氨基嘌呤 (6-BA)',
    shortDesc: '促进细胞分裂、延缓衰老的细胞分裂素类。',
    cas: '1214-39-7',
    formula: 'C12H11N5',
    mw: '225.25',
    structure: '类白色结晶粉末，轻微嘌呤味。',
    properties: '微溶于水，易溶于醇类和二甲基亚砜。',
    specs: '含量≥98%，水分≤1.0%，提供原药与制剂。',
    usage: '果树促花保果、叶面喷施延缓叶片衰老。',
    tech: '低剂量均匀喷雾，避免高温强光时段。',
    packaging: '25kg 纸板桶或 1kg 铝箔袋。',
    image: 'https://images.unsplash.com/photo-1502741126161-b048400d0796?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 6,
    name: '激动素 (Kinetin)',
    shortDesc: '细胞分裂素，常用于组织培养与抗衰处理。',
    cas: '525-79-1',
    formula: 'C10H9N5O',
    mw: '215.21',
    structure: '白色或类白色晶体。',
    properties: '微溶于水，溶于稀酸、碱及醇类。',
    specs: '含量≥98%，水分≤1.0%，提供分析纯/工业级。',
    usage: '花卉保鲜、果蔬延缓老化、组织培养外植体促分化。',
    tech: '按配方溶解后叶面喷施或用于培养基。',
    packaging: '1kg 铝箔袋，25kg 纸板桶。',
    image: 'https://images.unsplash.com/photo-1468971050039-be99497410af?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 7,
    name: '多效唑 (Paclobutrazol)',
    shortDesc: '抑制徒长、控制株高的常用调节剂。',
    cas: '76738-62-0',
    formula: 'C15H20ClN3O',
    mw: '293.79',
    structure: '白色晶体或粉末，微带气味。',
    properties: '难溶于水，易溶于有机溶剂，稳定性好。',
    specs: '原药含量≥95%，可提供 15%WP、25%SC 等制剂。',
    usage: '果树、园林植物控梢促花，降低倒伏风险。',
    tech: '喷施/灌根皆可，严格按推荐剂量使用。',
    packaging: '25kg 纸板桶/编织袋，制剂支持小包装。',
    image: 'https://images.unsplash.com/photo-1504198458649-3128b932f49b?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 8,
    name: '乙烯利 (Ethephon)',
    shortDesc: '释放乙烯促进成熟、脱叶、催花。',
    cas: '16672-87-0',
    formula: 'C2H6ClO3P',
    mw: '144.49',
    structure: '无色透明液体，弱酸性。',
    properties: '易溶于水，对热敏感，低温储存。',
    specs: '含量≥85%，常规 480g/L 水剂。',
    usage: '菠萝、烟草、棉花催熟脱叶，果树促着色。',
    tech: '喷雾均匀覆盖目标部位，注意作物安全间隔期。',
    packaging: '1L/5L/20L 塑料桶，可按需贴标。',
    image: 'https://images.unsplash.com/photo-1439886183900-e79ec0057170?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 9,
    name: '氯吡脲 (CPPU)',
    shortDesc: '高活性细胞分裂素，促果实膨大与坐果。',
    cas: '68157-60-8',
    formula: 'C12H10ClN5O',
    mw: '275.69',
    structure: '白色结晶粉末。',
    properties: '微溶于水，溶于二甲基亚砜和极性溶剂。',
    specs: '含量≥98%，提供 99% 原药及水剂/可溶粉制剂。',
    usage: '葡萄、猕猴桃、西瓜促膨大，提高商品性。',
    tech: '低剂量点涂/喷施，避免过量导致畸形。',
    packaging: '25kg 纸板桶，支持 10g/100g 小袋分装。',
    image: 'https://images.unsplash.com/photo-1451186859696-371d9477be93?auto=format&fit=crop&w=900&q=80',
  },
  {
    id: 10,
    name: '噻苯隆 (Thidiazuron)',
    shortDesc: '脱叶促熟与组织培养常用细胞分裂素。',
    cas: '51707-55-2',
    formula: 'C9H8N4OS',
    mw: '220.25',
    structure: '浅黄色结晶粉末。',
    properties: '难溶于水，溶于极性有机溶剂，耐热性好。',
    specs: '含量≥97%，可提供 50%WP、95%TC。',
    usage: '棉花脱叶、果树催熟、组培诱导不定芽。',
    tech: '喷施遵循安全间隔期，组培按培养基配方添加。',
    packaging: '25kg 纸板桶或铝箔复合袋。',
    image: 'https://images.unsplash.com/photo-1421809313281-48f03fa45e9f?auto=format&fit=crop&w=900&q=80',
  },
];
const selected = ref(productCards[0]);
const detailVisible = ref(false);

const openDetail = (item) => {
  selected.value = item;
  detailVisible.value = true;
};
</script>

<style scoped>
.section {
  padding: 72px 6vw;
  min-height: 80vh;
}

.section--alt {
  background: #f9fbff;
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

.layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 20px;
  align-items: start;
}

.list {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.04);
  padding: 14px;
}

.list__title {
  font-weight: 700;
  margin-bottom: 12px;
}

.list__items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.list__item {
  display: flex;
  align-items: center;
  width: 100%;
  border: 1px solid #e5e7eb;
  background: #f8fafc;
  border-radius: 10px;
  padding: 10px 12px;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
}

.list__item:hover {
  border-color: #3b82f6;
  box-shadow: 0 8px 16px rgba(59, 130, 246, 0.12);
  background: #eef2ff;
}

.list__item.active {
  border-color: #2563eb;
  background: #e0e7ff;
}

.list__name {
  font-weight: 700;
  color: #0f172a;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.08);
}

.card__image {
  height: 160px;
  background-size: cover;
  background-position: center;
}

.card__title {
  padding: 12px 14px 14px;
  font-weight: 700;
  color: #0f172a;
}

.detail-dialog :deep(.el-dialog__header) {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 10px;
}

.detail-dialog :deep(.el-overlay-dialog) {
  align-items: flex-start;
  padding-top: 80px;
}

.dialog__body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: #0f172a;
}

.dialog__image {
  width: 50%;
  border-radius: 12px;
  object-fit: cover;
  background: #f1f5f9;
  margin-bottom: 6px;
  margin: auto;
}

.dialog__line {
  margin: 0;
  line-height: 2;
  font-size: 20px;
  color: #374151;
}

@media (max-width: 960px) {
  .layout {
    grid-template-columns: 1fr;
  }
}
</style>
