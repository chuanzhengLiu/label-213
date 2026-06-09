import axios from 'axios'
import { ElMessage } from 'element-plus'
import Cookies from 'js-cookie'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从cookie获取token
    const token = Cookies.get('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误：', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回码不是200，说明有错误
    if (res.code !== 200) {
      // 401未授权，跳转登录页
      if (res.code === 401) {
        Cookies.remove('token')
        router.push('/login')
        ElMessage.error(res.msg || '未登录或token已过期')
      } else if (res.code === 403) {
        ElMessage.error(res.msg || '无操作权限')
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
      return Promise.reject(new Error(res.msg || '操作失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误：', error)
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
