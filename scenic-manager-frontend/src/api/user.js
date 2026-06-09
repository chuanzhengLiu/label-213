import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}

// 分页查询用户
export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/user/add',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/delete/${id}`,
    method: 'delete'
  })
}
