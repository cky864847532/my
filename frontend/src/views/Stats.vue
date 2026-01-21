<template>  
  <el-card>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="销售统计" name="sales">
        <div ref="salesChartRef" class="chart"></div>
        <el-table :data="pagedSales" style="width: 100%">
          <el-table-column prop="period" label="日期/月" />
          <el-table-column prop="total" label="销售额" />
          <el-table-column prop="qty" label="数量" />
        </el-table>
        <el-pagination
          v-if="sales.length > 0"
          background
          layout="prev, pager, next, jumper"
          :total="sales.length"
          :page-size="pageSize"
          v-model:current-page="currentPage"
          style="margin:16px 0;text-align:right;"
        />
      </el-tab-pane>
      <el-tab-pane label="产品排行" name="product">
        <div ref="productChartRef" class="chart"></div>
        <el-table :data="topProducts" style="width: 100%">
          <el-table-column prop="name" label="产品" />
          <el-table-column prop="total" label="销售额" />
          <el-table-column prop="qty" label="数量" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="销售单位排行" name="salesUnit">
        <div ref="salesUnitChartRef" class="chart"></div>
        <el-table :data="topSalesUnits" style="width: 100%">
          <el-table-column prop="name" label="销售单位" />
          <el-table-column prop="total" label="销售额" />
          <el-table-column prop="qty" label="数量" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="库存占比" name="inventory">
        <div ref="inventoryChartRef" class="chart"></div>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>


<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';

const sales = ref([]);
const topProducts = ref([]);
const topSalesUnits = ref([]);
const inventoryPie = ref([]);
const currentPage = ref(1);
const pageSize = 10;
const activeTab = ref('sales');
const pagedSales = computed(() => sales.value.slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize));

const salesChartRef = ref(null);
const productChartRef = ref(null);
const salesUnitChartRef = ref(null);
const inventoryChartRef = ref(null);
const charts = {
  sales: null,
  product: null,
  salesUnit: null,
  inventory: null,
};

const initChart = (el, key) => {
  if (!el) return null;
  if (!charts[key]) {
    charts[key] = echarts.init(el);
  }
  return charts[key];
};

const renderSalesChart = () => {
  const chart = initChart(salesChartRef.value, 'sales');
  if (!chart) return;

  const categories = sales.value.map(item => item.period);
  const totals = sales.value.map(item => item.total ?? 0);
  const quantities = sales.value.map(item => item.qty ?? 0);

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '数量'] },
    grid: { left: 72, right: 32, bottom: 40, top: 32, containLabel: true },
    xAxis: { type: 'category', data: categories },
    yAxis: [
      { type: 'value', name: '销售额' },
      { type: 'value', name: '数量', position: 'right' },
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.15 },
        data: totals,
        itemStyle: { color: '#5470c6' },
      },
      {
        name: '数量',
        type: 'bar',
        yAxisIndex: 1,
        data: quantities,
        itemStyle: { color: '#91cc75' },
      },
    ],
  });
};

const renderTopProductsChart = () => {
  const chart = initChart(productChartRef.value, 'product');
  if (!chart) return;

  const names = topProducts.value.map(item => item.name || `产品${item.id}`);
  const totals = topProducts.value.map(item => item.total ?? 0);

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 120, right: 16, bottom: 24, top: 16 },
    xAxis: { type: 'value', name: '销售额' },
    yAxis: { type: 'category', data: names, inverse: true },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: totals,
        itemStyle: { color: '#3ba272' },
        label: { show: true, position: 'right' },
      },
    ],
  });
};

const renderTopSalesUnitsChart = () => {
  const chart = initChart(salesUnitChartRef.value, 'salesUnit');
  if (!chart) return;

  const names = topSalesUnits.value.map(item => item.name || `客户${item.id}`);
  const totals = topSalesUnits.value.map(item => item.total ?? 0);

  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 120, right: 16, bottom: 24, top: 16 },
    xAxis: { type: 'value', name: '销售额' },
    yAxis: { type: 'category', data: names, inverse: true },
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: totals,
        itemStyle: { color: '#fac858' },
        label: { show: true, position: 'right' },
      },
    ],
  });
};

const renderInventoryChart = () => {
  const chart = initChart(inventoryChartRef.value, 'inventory');
  if (!chart) return;

  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '库存数量',
        type: 'pie',
        radius: ['35%', '65%'],
        avoidLabelOverlap: true,
        data: inventoryPie.value.map(item => ({ name: item.name, value: item.quantity })),
        label: { formatter: '{b}: {c}' },
      },
    ],
  });
};

const renderCharts = () => {
  renderSalesChart();
  renderTopProductsChart();
  renderTopSalesUnitsChart();
  renderInventoryChart();
};

const handleResize = () => {
  Object.values(charts).forEach(chart => chart && chart.resize());
};

const handleTabChange = async () => {
  await nextTick();
  handleResize();
};

onMounted(async () => {
  try {
    const [res1, res2, res3, res4] = await Promise.all([
      axios.get('/api/stats/sales?groupBy=month'),
      axios.get('/api/stats/products/top?limit=10'),
      axios.get('/api/stats/sales-units/top?limit=10'),
      axios.get('/api/stats/inventory'),
    ]);
    sales.value = res1.data.items || [];
    topProducts.value = res2.data.items || [];
    topSalesUnits.value = res3.data.items || [];
    inventoryPie.value = res4.data.items || [];
    await nextTick();
    renderCharts();
    window.addEventListener('resize', handleResize);
  } catch (err) {
    console.error('加载统计数据失败', err);
  }
});

watch(sales, renderSalesChart);
watch(topProducts, renderTopProductsChart);
watch(topSalesUnits, renderTopSalesUnitsChart);
watch(inventoryPie, renderInventoryChart);

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize);
  Object.keys(charts).forEach(key => {
    if (charts[key]) {
      charts[key].dispose();
      charts[key] = null;
    }
  });
});
</script>

<style scoped>
.chart {
  width: 100%;
  height: 320px;
  margin-bottom: 16px;
}
</style>
