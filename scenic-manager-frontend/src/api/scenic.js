import request from '@/utils/request'

// 分页查询景区
export function getScenicList(params) {
  return request({
    url: '/scenic/list',
    method: 'get',
    params
  })
}

// 新增景区
export function addScenic(data) {
  return request({
    url: '/scenic/add',
    method: 'post',
    data
  })
}

// 修改景区
export function updateScenic(data) {
  return request({
    url: '/scenic/update',
    method: 'put',
    data
  })
}

// 删除景区
export function deleteScenic(id) {
  return request({
    url: `/scenic/delete/${id}`,
    method: 'delete'
  })
}
