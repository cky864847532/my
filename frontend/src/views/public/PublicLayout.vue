<template>
  <div class="landing">
    <header class="hero">
      <div class="hero__nav">
        <div class="brand">
          <img :src="logo" alt="台州凯绿化工" class="brand__logo" />
        </div>
        <nav class="nav-links">
          <RouterLink
            v-for="item in navItems"
            :key="item.path"
            :to="item.path"
            class="nav-link"
            :class="{ active: route.path === item.path }"
          >{{ item.label }}</RouterLink>
          <a v-for="item in anchorItems" :key="item.label" href="javascript:void(0)" class="nav-link" @click.prevent="scrollContact">{{ item.label }}</a>
        </nav>
        <div class="nav-actions">
          <span class="nav-contact">服务热线：135 6688 9210</span>
          <el-button size="small" type="primary" @click="loginVisible = true">管理员登录</el-button>
        </div>
      </div>
    </header>

    <main class="content">
      <RouterView />
      <section class="contact" id="contact">
        <div class="contact__overlay"></div>
        <div class="contact__content">
          <div>
            <h2>联系我们</h2>
            <p>地址:浙江省台州市椒江区云西路147-5号2楼　 P.C.:318000</p>
            <p>电话：135 6688 9210 / 邮箱：info@kaiyuagro.comm</p>
            <p>QQ:8097092 / 微信：liu_ruping</p>
            <p>传真:0576-8888 1060</p>
          </div>
        </div>
      </section>
    </main>

    <el-dialog v-model="loginVisible" width="420px" title="管理员登录" :close-on-click-modal="false">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" @submit.prevent>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" autocomplete="current-password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="loginVisible = false">取消</el-button>
        <el-button type="primary" :loading="loading" @click="onLogin">登录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter, RouterLink, RouterView } from 'vue-router';
import { useUserStore } from '../../store/user';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import logoUrl from '../../assets/kailv-logo2.png';

const logo = logoUrl;
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const navItems = [
  { label: '首页', path: '/home' },
  { label: '关于我们', path: '/about' },
  { label: '产品中心', path: '/product-center' },
  { label: 'AI咨询', path: '/ai-consult' },
  { label: '产品包装', path: '/packaging' },
  { label: '信息反馈', path: '/feedback' },
];

const anchorItems = [{ label: '联系我们' }];

const loginVisible = ref(false);
const form = ref({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};
const formRef = ref();
const loading = ref(false);

const goFeedback = () => {
  router.push('/feedback');
};

const scrollContact = () => {
  const target = document.getElementById('contact');
  if (target) target.scrollIntoView({ behavior: 'smooth', block: 'start' });
};

const onLogin = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (!valid) return;
    loading.value = true;
    try {
      const res = await axios.post('/api/auth/login', form.value);
      userStore.setUser(res.data);
      loginVisible.value = false;
      router.push('/dashboard');
    } catch (e) {
      ElMessage.error('登录失败');
    } finally {
      loading.value = false;
    }
  });
};
</script>

<style scoped>
.landing {
  background: linear-gradient(140deg, #f6fbff 0%, #f2f5ff 38%, #ffffff 100%);
  color: #0f172a;
  min-height: 100vh;
}

.hero {
  padding: 20px 6vw 10px;
  position: sticky;
  top: 0;
  z-index: 10;
  background: rgba(255,255,255,0.9);
  backdrop-filter: blur(6px);
}

.hero__nav {
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 16px;
  padding: 10px 16px;
  border-radius: 14px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.04);
}

.brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand__logo {
  width: 300px;
  height: 56px;
  object-fit: contain;
}

.brand__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand__name {
  font-weight: 800;
  letter-spacing: 0.5px;
}

.brand__tag {
  color: #4b5563;
  font-size: 12px;
}

.nav-links {
  display: flex;
  gap: 14px;
  justify-content: center;
  font-weight: 700;
}

.nav-link {
  padding: 8px 12px;
  border-radius: 10px;
  transition: color 0.2s ease, background 0.2s ease;
  color: #1f2937;
}

.nav-link:hover,
.nav-link.active {
  color: #0ea371;
  background: #e8f7ef;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.nav-contact {
  color: #475569;
}

.content {
  position: relative;
  z-index: 1;
}

.section {
  padding: 72px 6vw;
  position: relative;
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

.contact {
  padding: 72px 6vw;
  position: relative;
  max-height: 30vh;
  background: url('https://images.unsplash.com/photo-1503389152951-9f343605f61e?auto=format&fit=crop&w=1400&q=80') center/cover no-repeat;
  color: #fff;
  position: relative;
}

.contact__overlay {
  position: absolute;
  inset: 0;
  background: rgba(10, 55, 95, 0.72);
}

.contact__content {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.contact__content h2 {
  margin: 0 0 12px;
}

.contact__content p {
  margin: 4px 0;
  color: #e2e8f0;
}

.contact__actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 960px) {
  .hero__nav {
    grid-template-columns: 1fr;
  }
  .nav-links {
    flex-wrap: wrap;
    justify-content: flex-start;
  }
  .contact__content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
