<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">景区票务管理系统</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 测试账号提示 -->
      <el-divider content-position="center">测试账号</el-divider>
      <div class="test-accounts">
        <div class="account-item" @click="fillAccount('admin', '123456')">
          <div class="account-label">超级管理员：</div>
          <div class="account-info">
            <span class="account-text">账号：<strong>admin</strong></span>
            <span class="account-text">密码：<strong>123456</strong></span>
          </div>
          <el-button type="text" size="small" @click.stop="fillAccount('admin', '123456')">点击填充</el-button>
        </div>
        <div class="account-item" @click="fillAccount('user', '123456')">
          <div class="account-label">普通管理员：</div>
          <div class="account-info">
            <span class="account-text">账号：<strong>user</strong></span>
            <span class="account-text">密码：<strong>123456</strong></span>
          </div>
          <el-button type="text" size="small" @click.stop="fillAccount('user', '123456')">点击填充</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/store/user'
import Cookies from 'js-cookie'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const rememberPassword = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 填充账号密码
const fillAccount = (username, password) => {
  loginForm.value.username = username
  loginForm.value.password = password
  ElMessage.success('账号密码已填充')
}

// 加载记住的密码
onMounted(() => {
  const savedUsername = localStorage.getItem('savedUsername')
  const savedPassword = localStorage.getItem('savedPassword')
  if (savedUsername && savedPassword) {
    loginForm.value.username = savedUsername
    loginForm.value.password = savedPassword
    rememberPassword.value = true
  }
})

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入账号/密码')
    return
  }

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const res = await login(loginForm.value)
    
    if (res.code === 200) {
      // 保存token
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data.userInfo)

      // 记住密码
      if (rememberPassword.value) {
        localStorage.setItem('savedUsername', loginForm.value.username)
        localStorage.setItem('savedPassword', loginForm.value.password)
      } else {
        localStorage.removeItem('savedUsername')
        localStorage.removeItem('savedPassword')
      }

      ElMessage.success('登录成功')
      router.push('/admin')
    }
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.test-accounts {
  margin-top: 20px;
}

.account-item {
  display: flex;
  align-items: center;
  padding: 12px;
  margin-bottom: 10px;
  background: #f5f7fa;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

.account-item:hover {
  background: #e4e7ed;
}

.account-label {
  font-size: 14px;
  color: #606266;
  min-width: 90px;
  font-weight: 500;
}

.account-info {
  flex: 1;
  display: flex;
  gap: 20px;
}

.account-text {
  font-size: 14px;
  color: #303133;
}

.account-text strong {
  color: #409eff;
  font-weight: 600;
}

@media (max-width: 768px) {
  .login-box {
    margin: 20px;
    padding: 30px 20px;
  }
  
  .account-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .account-info {
    flex-direction: column;
    gap: 5px;
    width: 100%;
  }
  
  .account-label {
    min-width: auto;
  }
}
</style>
