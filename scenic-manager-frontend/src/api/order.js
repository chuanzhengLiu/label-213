import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
}

// 分页查询订单
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/order/update',
    method: 'put',
    data
  })
}

// 删除订单
export function deleteOrder(id) {
  return request({
    url: `/order/delete/${id}`,
    method: 'delete'
  })
}

// 修改订单状态
export function updateOrderStatus(id, orderStatus) {
  return request({
    url: `/order/updateStatus?id=${id}&orderStatus=${orderStatus}`,
    method: 'put'
  })
}

// 订单统计
export function getOrderStatistics(params) {
  return request({
    url: '/order/statistics',
    method: 'get',
    params
  })
}
