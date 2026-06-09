import { createRouter, createWebHistory } from 'vue-router'
import Cookies from 'js-cookie'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/user',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'user',
        name: 'UserManagement',
        component: () => import('@/views/admin/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'scenic',
        name: 'ScenicManagement',
        component: () => import('@/views/admin/ScenicManagement.vue'),
        meta: { title: '景区管理' }
      },
      {
        path: 'ticket',
        name: 'TicketManagement',
        component: () => import('@/views/admin/TicketManagement.vue'),
        meta: { title: '票种管理' }
      },
      {
        path: 'order',
        name: 'OrderManagement',
        component: () => import('@/views/admin/OrderManagement.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '统计分析' }
      },
      {
        path: 'buy-ticket',
        name: 'AdminBuyTicket',
        component: () => import('@/views/visitor/BuyTicket.vue'),
        meta: { title: '在线购票' }
      }
    ]
  },
  {
    path: '/visitor',
    component: () => import('@/layouts/VisitorLayout.vue'),
    redirect: '/visitor/buy-ticket',
    children: [
      {
        path: 'buy-ticket',
        name: 'BuyTicket',
        component: () => import('@/views/visitor/BuyTicket.vue'),
        meta: { title: '购票' }
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = Cookies.get('token')
  
  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 如果没有token，重定向到登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 如果有token，正常访问
  next()
})

export default router
