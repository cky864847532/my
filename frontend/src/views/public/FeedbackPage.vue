<template>
  <section class="section section--alt">
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
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const feedbackForm = ref({ name: '', phone: '', email: '', message: '' });
const feedbackSubmitting = ref(false);

const submitFeedback = async () => {
  feedbackSubmitting.value = true;
  setTimeout(() => {
    feedbackSubmitting.value = false;
    ElMessage.success('感谢您的反馈，我们会尽快联系您');
  }, 500);
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

@media (max-width: 960px) {
  .feedback-panel {
    grid-template-columns: 1fr;
  }
}
</style>
