
<template>
  <el-card class="ai-chat" :class="{ dark: isDark }">
    <div class="chat-history">
      <div v-for="(msg, idx) in aiStore.history" :key="idx" :class="['msg', msg.role]">
        <span class="role">{{ msg.role === 'user' ? '我' : 'AI' }}：</span>
        <span class="text">{{ msg.text }}</span>
        <span v-if="msg.source" class="tag">{{ msg.source }}</span>
        <span v-if="typeof msg.confidence === 'number'" class="tag">置信度: {{ (msg.confidence * 100).toFixed(0) }}%</span>
      </div>
    </div>
    <el-form @submit.prevent="send" class="chat-form">
      <el-form-item>
        <el-input
          v-model="input"
          placeholder="请输入问题..."
          @keyup.enter="send"
          :disabled="aiStore.loading"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="send" :loading="aiStore.loading">发送</el-button>
        <el-button @click="aiStore.clearHistory" type="info">清空历史</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue';
import { useAIStore } from '../store/ai';
import axios from 'axios';
import { ElMessage } from 'element-plus';
const aiStore = useAIStore();
const input = ref('');
// 适配暗色模式
const isDark = ref(document.documentElement.classList.contains('dark'));
const updateDark = () => {
  isDark.value = document.documentElement.classList.contains('dark');
};
onMounted(() => {
  updateDark();
  window.addEventListener('click', updateDark);
});
onBeforeUnmount(() => {
  window.removeEventListener('click', updateDark);
});
const send = async () => {
  const message = input.value.trim();
  if (!message) return;
  aiStore.addMessage({ role: 'user', text: message });
  aiStore.setLoading(true);
  input.value = '';
  try {
    const res = await axios.post('/api/ai/chat', {
      customerId: 1, // TODO: 根据登录用户传递真实客户ID
      message,
    });
    const data = res.data || {};
    aiStore.addMessage({
      role: 'ai',
      text: data.reply || '暂无回复',
      source: data.source,
      confidence: typeof data.confidence === 'number' ? data.confidence : undefined,
    });
  } catch (e) {
    ElMessage.error('发送失败，请稍后重试');
    aiStore.addMessage({ role: 'ai', text: '（发送失败，请稍后重试）', source: 'error' });
  } finally {
    aiStore.setLoading(false);
  }
};
</script>
   

<style scoped>
.ai-chat {
  max-width: 600px;
  margin: 40px auto;
  background: var(--el-bg-color, #fff);
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.08);
  transition: background 0.3s;
}
.ai-chat.dark {
  background: #23272e;
  color: #fff;
}
.chat-history {
  min-height: 200px;
  margin-bottom: 16px;
  background: #f8f8f8;
  padding: 12px;
  border-radius: 6px;
  transition: background 0.3s;
}
.ai-chat.dark .chat-history {
  background: #181c24;
}
.msg {
  margin-bottom: 8px;
  word-break: break-all;
}
.msg.user {
  text-align: right;
  color: var(--el-color-primary, #409eff);
}
.msg.ai {
  text-align: left;
  color: #67c23a;
}
.tag {
  margin-left: 8px;
  font-size: 12px;
  color: #909399;
}
.ai-chat.dark .msg.ai {
  color: #90caf9;
}
.role {
  font-weight: bold;
}
.chat-form {
  display: flex;
  gap: 8px;
  align-items: flex-end;
}
</style>
