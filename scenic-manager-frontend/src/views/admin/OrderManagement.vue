<template>
  <div class="order-management">
    <el-card>
      <template #header>
        <span>订单管理</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.userPhone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item label="订单状态" >
          <el-select v-model="searchForm.orderStatus" style="width: 200px" placeholder="请选择状态" clearable>
            <el-option label="已下单" :value="1" />
            <el-option label="已核销" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="userName" label="购票人" />
        <el-table-column prop="userPhone" label="手机号" />
        <el-table-column prop="ticketNum" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">¥{{ row.totalAmount }}</template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.orderStatus === 1 ? 'warning' : row.orderStatus === 2 ? 'success' : 'info'">
              {{ row.orderStatus === 1 ? '已下单' : row.orderStatus === 2 ? '已核销' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.orderStatus === 1"
              type="success"
              size="small"
              @click="handleVerify(row)"
            >核销</el-button>
            <el-button
              v-if="row.orderStatus === 1"
              type="warning"
              size="small"
              @click="handleCancel(row)"
            >取消</el-button>
            <el-button
              v-if="row.orderStatus === 3"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑订单"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="购票人姓名" prop="userName">
          <el-input v-model="form.userName" />
        </el-form-item>
        <el-form-item label="手机号" prop="userPhone">
          <el-input v-model="form.userPhone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrderList,
  updateOrder,
  deleteOrder,
  updateOrderStatus
} from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const dateRange = ref(null)

const searchForm = reactive({
  orderNo: '',
  userPhone: '',
  orderStatus: null,
  startTime: null,
  endTime: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  userName: '',
  userPhone: ''
})

const rules = {
  userName: [{ required: true, message: '请输入购票人姓名', trigger: 'blur' }],
  userPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    searchForm.startTime = dates[0]
    searchForm.endTime = dates[1]
  } else {
    searchForm.startTime = null
    searchForm.endTime = null
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const res = await getOrderList(params)
    if (res.code === 200) {
      tableData.value = res.data.records
      pagination.total = res.data.total
    }
  } catch (error) {
    console.error('加载数据失败：', error)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.orderNo = ''
  searchForm.userPhone = ''
  searchForm.orderStatus = null
  searchForm.startTime = null
  searchForm.endTime = null
  dateRange.value = null
  pagination.pageNum = 1
  loadData()
}

const handleEdit = (row) => {
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    userName: row.userName,
    userPhone: row.userPhone
  })
}

const handleVerify = async (row) => {
  try {
    await ElMessageBox.confirm('是否核销该订单？', '提示', {
      type: 'warning'
    })
    
    const res = await updateOrderStatus(row.id, 2)
    if (res.code === 200) {
      ElMessage.success('核销成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('核销失败：', error)
    }
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('是否取消该订单？', '提示', {
      type: 'warning'
    })
    
    const res = await updateOrderStatus(row.id, 3)
    if (res.code === 200) {
      ElMessage.success('取消成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败：', error)
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('是否删除该订单？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteOrder(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败：', error)
    }
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    const res = await updateOrder(form)
    if (res.code === 200) {
      ElMessage.success('修改订单信息成功')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    console.error('提交失败：', error)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    userName: '',
    userPhone: ''
  })
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.order-management {
  background: white;
  border-radius: 4px;
}

.search-form {
  margin-bottom: 20px;
}
</style>
