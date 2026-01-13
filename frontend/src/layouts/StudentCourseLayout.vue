<template>
  <div class="content">
    <RouterView />
  </div>
</template>

<script setup lang="ts">
// 左侧“章节”导航已移除，课程内直接展示内容
</script>

<style scoped>
.content {
  min-width: 0;
  display: grid;
  gap: 12px;
}
</style>
