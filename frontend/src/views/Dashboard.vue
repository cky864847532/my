<template>
  <div class="dashboard">
    <div class="cards">
      <el-card class="card" shadow="hover">
        <div class="card__title">当月销售额</div>
        <div class="card__value">¥{{ formatMoney(summary.monthSalesTotal) }}</div>
        <div class="card__meta">数量：{{ summary.monthSalesQty }} / 目标：¥{{ formatMoney(summary.monthSalesTarget) }}</div>
        <el-progress :percentage="Math.min(100, Math.round(summary.targetProgress * 100))" status="success" />
      </el-card>
      <el-card class="card" shadow="hover">
        <div class="card__title">当月采购额</div>
        <div class="card__value">¥{{ formatMoney(summary.monthPurchaseTotal) }}</div>
        <div class="card__meta">本月采购总额</div>
      </el-card>
      <el-card class="card" shadow="hover">
        <div class="card__title">库存 SKU</div>
        <div class="card__value">{{ summary.inventorySkus }}</div>
        <div class="card__meta">产品数：{{ summary.productCount }}</div>
      </el-card>
      <el-card class="card" shadow="hover">
        <div class="card__title">客户/销售单位</div>
        <div class="card__value">{{ summary.customerCount }}</div>
        <div class="card__meta">保持联系，及时跟进</div>
      </el-card>
    </div>

    <el-card shadow="hover" class="chart-card">
      <div class="chart-card__title">本月销售趋势</div>
      <div ref="miniSalesChart" class="mini-chart"></div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref, nextTick, onBeforeUnmount } from 'vue';
import axios from 'axios';
import * as echarts from 'echarts';

const summary = ref({
  monthSalesTotal: 0,
  monthSalesQty: 0,
  monthPurchaseTotal: 0,
  monthSalesTarget: 0,
  targetProgress: 0,
  inventorySkus: 0,
  productCount: 0,
  customerCount: 0,
});

const miniSalesChart = ref(null);
let chartInstance = null;

const formatMoney = (v) => {
  if (v === undefined || v === null) return '0';
  const num = Number(v);
  if (Number.isNaN(num)) return '0';
  return num.toLocaleString(undefined, { minimumFractionDigits: 0, maximumFractionDigits: 0 });
};

const renderMiniChart = (items) => {
  if (!miniSalesChart.value) return;
  if (!chartInstance) {
    chartInstance = echarts.init(miniSalesChart.value);
  }
  const categories = items.map(i => i.period);
  const totals = items.map(i => i.total ?? 0);
  chartInstance.setOption({
    grid: { left: 48, right: 16, top: 16, bottom: 32, containLabel: true },
    xAxis: { type: 'category', data: categories },
    yAxis: { type: 'value' },
    tooltip: { trigger: 'axis' },
    series: [{ type: 'bar', data: totals, itemStyle: { color: '#4f8bff' } }],
  });
};

const loadData = async () => {
  const [sumRes, salesRes] = await Promise.all([
    axios.get('/api/stats/summary'),
    axios.get('/api/stats/sales?groupBy=month'),
  ]);
  summary.value = sumRes.data || summary.value;
  const items = (salesRes.data && salesRes.data.items) ? salesRes.data.items : [];
  await nextTick();
  renderMiniChart(items.slice(-6));
};

const handleResize = () => {
  chartInstance && chartInstance.resize();
};

onMounted(async () => {
  try {
    await loadData();
    window.addEventListener('resize', handleResize);
  } catch (e) {
    console.error('加载首页数据失败', e);
  }
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  chartInstance && chartInstance.dispose();
});
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.card__title {
  color: #6b7280;
  font-size: 14px;
}

.card__value {
  font-size: 28px;
  font-weight: 800;
  margin: 8px 0 4px;
  color: #111827;
}

.card__meta {
  color: #6b7280;
  margin-bottom: 8px;
}

.chart-card__title {
  font-weight: 700;
  margin-bottom: 8px;
}

.mini-chart {
  width: 100%;
  height: 300px;
}
</style>
