<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="sidebar">
      <div class="logo">景区管理系统</div>
      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/scenic">
          <el-icon><Location /></el-icon>
          <span>景区管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/ticket">
          <el-icon><Ticket /></el-icon>
          <span>票种管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
        <el-menu-item index="/admin/buy-ticket">
          <el-icon><ShoppingCart /></el-icon>
          <span>在线购票</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span>欢迎，{{ userInfo?.username || '管理员' }}</span>
        </div>
        <div class="header-right">
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getCurrentUser } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Location, Ticket, Document, DataAnalysis, ShoppingCart } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const userInfo = computed(() => userStore.userInfo)

onMounted(async () => {
  if (!userStore.userInfo) {
    try {
      const res = await getCurrentUser()
      if (res.code === 200) {
        userStore.setUserInfo(res.data)
      }
    } catch (error) {
      console.error('获取用户信息失败：', error)
    }
  }
})

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background-color: #304156;
  color: white;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  background-color: #2b3a4a;
}

.sidebar-menu {
  border-right: none;
  background-color: #304156;
}

.sidebar-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.7);
}

.sidebar-menu .el-menu-item:hover,
.sidebar-menu .el-menu-item.is-active {
  background-color: #409eff;
  color: white;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

@media (max-width: 768px) {
  .sidebar {
    width: 100% !important;
  }
  
  .admin-layout {
    flex-direction: column;
  }
}
</style>
