import { createRouter, createWebHashHistory } from 'vue-router';
import { useUserStore } from '../store/user';

const routes = [
  {
    path: '/',
    component: () => import('../views/public/PublicLayout.vue'),
    meta: { public: true },
    children: [
      { path: '', redirect: '/home' },
      { path: '/home', name: 'Home', component: () => import('../views/public/HomePage.vue'), meta: { public: true } },
      { path: '/about', name: 'About', component: () => import('../views/public/AboutPage.vue'), meta: { public: true } },
      { path: '/product-center', name: 'ProductCenter', component: () => import('../views/public/ProductCenterPage.vue'), meta: { public: true } },
      { path: '/packaging', name: 'Packaging', component: () => import('../views/public/PackagingPage.vue'), meta: { public: true } },
      { path: '/feedback', name: 'Feedback', component: () => import('../views/public/FeedbackPage.vue'), meta: { public: true } },
      { path: '/ai-consult', name: 'AIConsult', component: () => import('../views/public/AIConsultPage.vue'), meta: { public: true } },
    ],
  },
  { path: '/login', redirect: '/home' },
  { path: '/dashboard', name: 'Dashboard', component: () => import('../views/Dashboard.vue') },
  { path: '/products', name: 'Products', component: () => import('../views/Products.vue') },
  { path: '/purchases', name: 'Purchases', component: () => import('../views/Purchases.vue') },
  { path: '/sales', name: 'Sales', component: () => import('../views/Sales.vue') },
  { path: '/suppliers', name: 'Suppliers', component: () => import('../views/Suppliers.vue') },
  { path: '/sales-units', name: 'SalesUnits', component: () => import('../views/SalesUnits.vue') },
  { path: '/inventory', name: 'Inventory', component: () => import('../views/Inventory.vue') },
  { path: '/stats', name: 'Stats', component: () => import('../views/Stats.vue') },
  { path: '/ai', name: 'AIChat', component: () => import('../views/AIChat.vue') },
];

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes,
});

// 路由守卫：未登录跳转到 /login
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  userStore.loadFromStorage();
  if (to.meta?.public) {
    if (userStore.token && (to.path === '/' || to.path === '/login')) {
      return next('/dashboard');
    }
    return next();
  }
  if (!userStore.token) return next('/home');
  next();
});

export default router;
