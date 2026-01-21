<template>
  <section class="section section--alt">
    <div class="section__head">
      <h2>AI咨询</h2>
      <p>智能助手在线解答产品、包装、应用技术等常见问题。</p>
    </div>
    <AIChat />
  </section>
</template>

<script setup>
import AIChat from '../AIChat.vue';
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
  margin: 0 auto 28px;
  text-align: center;
}

.section__head h2 {
  margin: 0 0 10px;
  font-size: 28px;
}

.section__head p {
  margin: 0;
  color: #4b5563;
}
</style>
