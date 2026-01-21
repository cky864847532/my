import { defineStore } from 'pinia';

export const useAIStore = defineStore('ai', {
  state: () => ({
    history: [], // 聊天历史
    loading: false,
  }),
  actions: {
    addMessage(msg) {
      this.history.push(msg);
    },
    clearHistory() {
      this.history = [];
    },
    setLoading(val) {
      this.loading = val;
    },
  },
});
