<template>
  <RouterView v-if="!isAuthed" />
  <div v-else class="admin-shell">
    <div class="shell-bg"></div>
    <div class="shell-body">
      <header class="top-bar" :class="{ dark: isDark }">
        <div class="brand">
          <img :src="logo" alt="台州凯绿化工" class="brand-mark" />
          <div class="brand-text">
            <span class="brand-name">台州凯绿化工管理后台</span>
            <small class="brand-tag">Taizhou Kailv Chemical · Admin</small>
          </div>
        </div>
        <div class="top-actions">
          <el-button type="text" @click="logout">退出</el-button>
        </div>
      </header>

      <div class="layout">
        <aside class="side-card" :class="{ dark: isDark }">
          <div class="side-head">
            <span class="side-label">导航</span>
          </div>
          <el-menu
            class="menu"
            :default-active="activePath"
            :background-color="isDark ? '#111827' : 'transparent'"
            :text-color="isDark ? '#e5e7eb' : '#0f172a'"
            :active-text-color="primaryColor"
            @select="navigate"
          >
            <el-menu-item v-for="item in visibleMenu" :key="item.path" :index="item.path">
              <span class="menu-label">{{ item.label }}</span>
            </el-menu-item>
          </el-menu>
        </aside>

        <main class="main-card" :class="{ dark: isDark }">
          <div class="page-wrapper">
            <RouterView />
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch, onMounted } from 'vue';
import { useRoute, useRouter, RouterView } from 'vue-router';
import { useCssVar } from '@vueuse/core';
import { useUserStore } from './store/user';
import logo from './assets/kailv-logo2.png';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
userStore.loadFromStorage();

const menuConfig = [
  { path: '/dashboard', label: '首页' },
  { path: '/products', label: '产品' },
  { path: '/purchases', label: '采购' },
  { path: '/sales', label: '销售' },
  { path: '/suppliers', label: '供货单位' },
  { path: '/sales-units', label: '销售单位' },
  { path: '/inventory', label: '库存' },
  { path: '/stats', label: '统计' },
];

const visibleMenu = computed(() => menuConfig);

const activePath = computed(() => route.path || '/dashboard');

const isDark = ref(false);
const primaryColor = ref('#409eff');
const primaryVar = useCssVar('--el-color-primary');
const isAuthed = computed(() => !!userStore.token);

const applyTheme = () => {
  primaryVar.value = primaryColor.value || '#409eff';
  document.documentElement.classList.toggle('dark', isDark.value);
};

watch([isDark, primaryColor], applyTheme, { immediate: true });
onMounted(applyTheme);

const navigate = (index) => {
  if (index && index !== route.path) {
    router.push(index);
  }
};

const logout = () => {
  userStore.logout();
  router.replace('/login');
};

</script>

<style scoped>
.admin-shell {
  position: relative;
  min-height: 100vh;
  background: linear-gradient(140deg, #f6fbff 0%, #f2f5ff 38%, #ffffff 100%);
  overflow: hidden;
}

.shell-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(0,160,255,0.12), transparent 34%),
    radial-gradient(circle at 80% 30%, rgba(15,182,111,0.12), transparent 36%);
  pointer-events: none;
}

.shell-body {
  position: relative;
  z-index: 1;
  max-width: 1320px;
  margin: 0 auto;
  padding: 24px 24px 32px;
}

.top-bar {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e5e7eb;
  border-radius: 18px;
  box-shadow: 0 10px 36px rgba(0, 0, 0, 0.06);
  backdrop-filter: blur(10px);
}

.top-bar.dark {
  background: rgba(17, 24, 39, 0.9);
  border-color: #0b1221;
  color: #e5e7eb;
}

.brand {
  display: flex;
  align-items: center;
  gap: 14px;
}

.brand-mark {
  height: 48px;
  width: 200px;
  object-fit: contain;
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-name {
  font-weight: 800;
  letter-spacing: 0.5px;
  color: #0f172a;
}

.brand-tag {
  color: #475569;
}

.top-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.layout {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 16px;
  margin-top: 16px;
  min-height: calc(100vh - 140px);
}

.side-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 14px 38px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
  overflow: hidden;
}

.side-card.dark {
  background: rgba(17, 24, 39, 0.9);
  border-color: #0b1221;
}

.side-head {
  padding: 14px 16px 10px;
  border-bottom: 1px solid #e5e7eb;
  font-weight: 700;
  color: #0f172a;
}

.side-card.dark .side-head {
  border-color: #0b1221;
  color: #e5e7eb;
}

.side-label {
  font-size: 13px;
}

.menu {
  border-right: none;
}

.menu-label {
  font-size: 14px;
}

.main-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  box-shadow: 0 16px 44px rgba(0, 0, 0, 0.06);
  min-height: 60vh;
  backdrop-filter: blur(10px);
}

.main-card.dark {
  background: rgba(15, 23, 42, 0.92);
  border-color: #0b1221;
  color: #e5e7eb;
}

.page-wrapper {
  padding: 24px;
  box-sizing: border-box;
}
</style>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  font-family: "Manrope", "Segoe UI", "PingFang SC", "Hiragino Sans", "Arial", sans-serif;
  background: #f5f6fa;
}
</style>
