<template>
  <div class="landing" id="home">
    <header class="hero">
      <div class="hero__nav">
        <div class="brand">
          <img :src="logo" alt="台州凯绿化工" class="brand__logo" />
        </div>
        <nav class="nav-links">
          <a v-for="item in navItems" :key="item.target" href="javascript:void(0)" @click="scrollTo(item.target)">{{ item.label }}</a>
        </nav>
        <div class="nav-actions">
          <span class="nav-contact">服务热线：135 6688 9210</span>
          <el-button size="small" type="primary" @click="loginVisible = true">管理员登录</el-button>
        </div>
      </div>

      <div class="hero__content">
        <div>
          <p class="eyebrow">绿色化学 · 安全环保 · 稳定供应</p>
          <h1>专注精细化工与包装解决方案</h1>
          <p class="lead">从基础化工原料到定制包装服务，台州凯绿化工为客户提供稳定的产品、规范的仓储与快捷的物流体系。</p>
          <div class="hero__cta">
            <el-button type="primary" size="large" @click="scrollTo('products')">查看产品中心</el-button>
            <el-button size="large" @click="scrollTo('contact')">联系我们</el-button>
          </div>
          <div class="metrics">
            <div class="metric"><span class="metric__value">20+</span><span class="metric__label">行业应用场景</span></div>
            <div class="metric"><span class="metric__value">3000㎡</span><span class="metric__label">仓储与包装车间</span></div>
            <div class="metric"><span class="metric__value">7×24</span><span class="metric__label">售前售后支持</span></div>
          </div>
        </div>
        <div class="hero__card">
          <div class="hero__badge">品质追溯</div>
          <h3>从原料到交付，批次可追踪</h3>
          <p>统一质检标准、批次记录与安全包装，保障产品稳定与运输安全。</p>
          <ul>
            <li>批次留样与检测报告</li>
            <li>UN 认证包装方案</li>
            <li>覆盖华东与华南的配送网络</li>
          </ul>
        </div>
      </div>
    </header>

    <section class="section" id="about">
      <div class="section__head">
        <h2>关于我们</h2>
        <p>我公司是专门从事研究，开发和生产农化产品——植物生长调节剂为主。公司在植物生长调节剂产品方面有着10多年的经营经验，有着优良的售后服务，专业的技术指导。</p>
        <p>我们的主要市场：英国，美国，西班牙，澳大利亚，新西兰，荷兰，哥伦比亚，墨西哥，印度，日本，韩国，越南等等。</p>
        <p>农业是人类生存的基础，而现在人们越来越重视食品安全，因此健康，无污染的产品是非常重要的。</p>
        <p>我们以开发安全，环保的产品来满足市场的需求，一步步取得进步，实现与客户的双赢。</p>
      </div>
      <div class="about-grid">
        <div class="about-card" v-for="item in aboutItems" :key="item.title">
          <div class="about-card__icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
        </div>
      </div>
    </section>

    <section class="section section--alt" id="products">
      <div class="section__head">
        <h2>产品中心</h2>
        <p>精选基础化工品、助剂与特种材料，支持按需定制包装与规格。</p>
      </div>
      <div class="cards">
        <div class="card" v-for="item in productCards" :key="item.title">
          <div class="card__title">{{ item.title }}</div>
          <p class="card__desc">{{ item.desc }}</p>
          <div class="card__tags">
            <span v-for="tag in item.tags" :key="tag">{{ tag }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section" id="packaging">
      <div class="section__head">
        <h2>产品包装</h2>
        <p>安全、合规、可追溯的包装方案，满足运输与仓储要求。</p>
      </div>
      <div class="packaging-grid">
        <div class="packaging-card" v-for="item in packagingList" :key="item.title">
          <h3>{{ item.title }}</h3>
          <p>{{ item.desc }}</p>
          <div class="chip-row">
            <span class="chip" v-for="chip in item.chips" :key="chip">{{ chip }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="section section--alt" id="feedback">
      <div class="section__head">
        <h2>信息反馈</h2>
        <p>留下需求与想法，我们会尽快联系您。</p>
      </div>
      <div class="feedback-panel">
        <div class="feedback-copy">
          <h3>快速响应</h3>
          <p>提供产品选型建议、包装方案与送样支持，专人跟进进度。</p>
          <ul>
            <li>工作日 2 小时内初步回复</li>
            <li>支持线上会议与现场沟通</li>
            <li>样品、检测、定制包装全流程陪伴</li>
          </ul>
        </div>
        <el-form class="feedback-form" :model="feedbackForm" label-width="80px">
          <el-form-item label="姓名">
            <el-input v-model="feedbackForm.name" placeholder="您的姓名" />
          </el-form-item>
          <el-form-item label="电话">
            <el-input v-model="feedbackForm.phone" placeholder="便于联系的电话" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="feedbackForm.email" placeholder="选填" />
          </el-form-item>
          <el-form-item label="需求">
            <el-input type="textarea" :rows="4" v-model="feedbackForm.message" placeholder="产品、包装或其他需求" />
          </el-form-item>
          <el-button type="primary" @click="submitFeedback" :loading="feedbackSubmitting">提交</el-button>
        </el-form>
      </div>
    </section>

    <section class="section contact" id="contact">
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
import { useUserStore } from '../store/user';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';
import logoUrl from '../assets/kailv-logo2.png';

const logo = logoUrl;
const navItems = [
  { label: '首页', target: 'home' },
  { label: '关于我们', target: 'about' },
  { label: '产品中心', target: 'products' },
  { label: '产品包装', target: 'packaging' },
  { label: '信息反馈', target: 'feedback' },
  { label: '联系我们', target: 'contact' },
];

const aboutItems = [
  { title: '专业团队', desc: '多年行业经验的销售与技术团队，提供产品选型与法规咨询。', icon: '⚙️' },
  { title: '安全合规', desc: '严格遵守安全与环保要求，提供 MSDS 与检测报告。', icon: '🛡️' },
  { title: '高效配送', desc: '与多家物流合作，支持定时送达与特种运输。', icon: '🚚' },
];

const productCards = [
  { title: '基础化工与溶剂', desc: '通用化工原料，批次稳定，满足多行业生产需求。', tags: ['基础原料', '大包装/散装', '批次追溯'] },
  { title: '功能助剂', desc: '涂料、塑料、农化等行业常用助剂，提供技术支持与配方建议。', tags: ['分散剂', '消泡剂', '增塑剂'] },
  { title: '特种材料', desc: '定制化特殊规格与纯度，支持样品测试与小批量供货。', tags: ['高纯度', '定制规格', '送样支持'] },
];

const packagingList = [
  { title: '标准包装', desc: '桶、袋、IBC 等多种规格，符合运输法规。', chips: ['UN 认证', '防泄漏', '易堆码'] },
  { title: '安全标签与批次管理', desc: '批次编号、危化标识、生产日期一体化管理。', chips: ['追溯标签', '批次留样', '数字档案'] },
  { title: '仓储与配送', desc: '恒温仓储、分区管理，覆盖华东华南的配送网络。', chips: ['分区仓储', '定时配送', '全程跟踪'] },
];

const feedbackForm = ref({ name: '', phone: '', email: '', message: '' });
const feedbackSubmitting = ref(false);

const userStore = useUserStore();
const router = useRouter();
const form = ref({ username: '', password: '' });
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
};
const formRef = ref();
const loading = ref(false);
const loginVisible = ref(false);

const scrollTo = (id) => {
  const target = document.getElementById(id);
  if (target) target.scrollIntoView({ behavior: 'smooth', block: 'start' });
};

const submitFeedback = async () => {
  feedbackSubmitting.value = true;
  setTimeout(() => {
    feedbackSubmitting.value = false;
    ElMessage.success('感谢您的反馈，我们会尽快联系您');
  }, 500);
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
}

.hero {
  padding: 28px 6vw 40px;
  position: relative;
  overflow: hidden;
}

.hero::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(0,160,255,0.14), transparent 32%),
    radial-gradient(circle at 80% 30%, rgba(15,182,111,0.16), transparent 35%);
  pointer-events: none;
}

.hero__nav {
  position: sticky;
  top: 0;
  z-index: 2;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.04);
  backdrop-filter: blur(8px);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand__logo {
  width: 300px;
  height: 64px;
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
  gap: 18px;
  justify-content: center;
  font-weight: 600;
}

.nav-links a {
  padding: 6px 10px;
  border-radius: 8px;
  transition: color 0.2s ease, background 0.2s ease;
}

.nav-links a:hover {
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

.hero__content {
  display: grid;
  grid-template-columns: 1.2fr 0.9fr;
  gap: 40px;
  margin-top: 36px;
  align-items: center;
  position: relative;
  z-index: 1;
}

.eyebrow {
  letter-spacing: 2px;
  text-transform: uppercase;
  color: #0ea371;
  font-weight: 700;
  margin-bottom: 12px;
}

.hero h1 {
  font-size: 38px;
  margin: 0 0 14px;
  line-height: 1.2;
}

.lead {
  color: #4b5563;
  margin-bottom: 20px;
}

.hero__cta {
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

.hero__card {
  background: linear-gradient(150deg, #0b9248 0%, #0ea371 45%, #16b28f 100%);
  color: #fff;
  padding: 24px;
  border-radius: 18px;
  box-shadow: 0 16px 40px rgba(14, 163, 113, 0.35);
}

.hero__badge {
  display: inline-block;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.15);
  font-size: 12px;
  margin-bottom: 10px;
}

.hero__card h3 {
  margin: 0 0 10px;
}

.hero__card p {
  margin: 0 0 12px;
  color: rgba(255, 255, 255, 0.9);
}

.hero__card ul {
  margin: 0;
  padding-left: 16px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.7;
}

.section {
  padding: 72px 6vw;
  position: relative;
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

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.04);
}

.card__title {
  font-weight: 700;
  margin-bottom: 8px;
}

.card__desc {
  color: #4b5563;
  margin: 0 0 10px;
}

.card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.card__tags span {
  background: #e8f7ef;
  color: #0ea371;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.packaging-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.packaging-card {
  background: linear-gradient(145deg, #ffffff 0%, #f7fbff 100%);
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 18px;
}

.packaging-card h3 {
  margin: 0 0 10px;
}

.packaging-card p {
  margin: 0 0 12px;
  color: #4b5563;
}

.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  background: #e8f7ef;
  color: #0ea371;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
}

.feedback-panel {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 22px;
  align-items: start;
}

.feedback-copy {
  background: #0f172a;
  color: #e5e7eb;
  border-radius: 16px;
  padding: 22px;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.35);
}

.feedback-copy h3 {
  margin: 0 0 10px;
}

.feedback-copy p {
  margin: 0 0 12px;
  color: #cbd5e1;
}

.feedback-copy ul {
  margin: 0;
  padding-left: 16px;
  line-height: 1.8;
  color: #cbd5e1;
}

.feedback-form {
  background: #fff;
  padding: 20px;
  border-radius: 14px;
  box-shadow: 0 10px 26px rgba(0, 0, 0, 0.06);
}

.contact {
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
    row-gap: 10px;
  }
  .nav-links {
    flex-wrap: wrap;
    justify-content: flex-start;
  }
  .hero__content {
    grid-template-columns: 1fr;
  }
  .feedback-panel {
    grid-template-columns: 1fr;
  }
  .contact__content {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
