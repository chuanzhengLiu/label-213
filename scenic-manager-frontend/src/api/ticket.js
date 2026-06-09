import request from '@/utils/request'

// 分页查询票种
export function getTicketList(params) {
  return request({
    url: '/ticket/list',
    method: 'get',
    params
  })
}

// 新增票种
export function addTicket(data) {
  return request({
    url: '/ticket/add',
    method: 'post',
    data
  })
}

// 修改票种
export function updateTicket(data) {
  return request({
    url: '/ticket/update',
    method: 'put',
    data
  })
}

// 删除票种
export function deleteTicket(id) {
  return request({
    url: `/ticket/delete/${id}`,
    method: 'delete'
  })
}

// 获取票种库存
export function getTicketStock(id) {
  return request({
    url: `/ticket/stock/${id}`,
    method: 'get'
  })
}
