<template>
  <div class="visitor-layout">
    <el-header class="visitor-header">
      <div class="header-content">
        <h1 class="logo">景区票务管理系统</h1>
        <el-button type="primary" size="large" @click="goToBuyTicket" class="buy-btn">
          <el-icon><ShoppingCart /></el-icon>
          立即购票
        </el-button>
      </div>
    </el-header>
    <el-main class="visitor-main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()

const goToBuyTicket = () => {
  router.push('/visitor/buy-ticket')
}
</script>

<style scoped>
.visitor-layout {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.visitor-header {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 100%;
}

.logo {
  color: #409eff;
  font-size: 24px;
  margin: 0;
}

.buy-btn {
  font-size: 16px;
  padding: 12px 30px;
}

.visitor-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 10px;
  }
  
  .logo {
    font-size: 20px;
    margin-bottom: 10px;
  }
  
  .buy-btn {
    width: 100%;
  }
}
</style>
