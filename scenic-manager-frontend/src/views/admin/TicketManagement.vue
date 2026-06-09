<template>
  <div class="ticket-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>票种管理</span>
          <el-button type="primary" @click="handleAdd" v-if="canDelete">新增票种</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="景区">
          <el-select v-model="searchForm.scenicId" placeholder="请选择景区（可选）" clearable @change="loadData" style="width: 200px">
            <el-option
              v-for="item in scenicList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="票种名称">
          <el-input v-model="searchForm.typeName" placeholder="请输入票种名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="scenicName" label="景区" width="150" />
        <el-table-column prop="typeName" label="票种名称" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column prop="validStart" label="有效期开始" />
        <el-table-column prop="validEnd" label="有效期结束" />
        <el-table-column label="操作" width="200" v-if="canDelete">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="景区" prop="scenicId">
          <el-select v-model="form.scenicId" placeholder="请选择景区" style="width: 100%">
            <el-option
              v-for="item in scenicList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="票种名称" prop="typeName">
          <el-input v-model="form.typeName" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期开始">
          <el-date-picker v-model="form.validStart" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期结束">
          <el-date-picker v-model="form.validEnd" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getTicketList, addTicket, updateTicket, deleteTicket } from '@/api/ticket'
import { getScenicList } from '@/api/scenic'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const scenicList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  scenicId: null,
  typeName: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  scenicId: null,
  typeName: '',
  price: null,
  stock: 0,
  validStart: null,
  validEnd: null
})

const rules = {
  scenicId: [{ required: true, message: '请选择景区', trigger: 'change' }],
  typeName: [{ required: true, message: '请输入票种名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const canDelete = computed(() => {
  return userStore.userInfo?.role === 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑票种' : '新增票种')

const loadScenicList = async () => {
  try {
    const res = await getScenicList({ pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      scenicList.value = res.data.records
    }
  } catch (error) {
    console.error('加载景区列表失败：', error)
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
    // 如果scenicId为空，不传该参数（查询全部）
    if (!params.scenicId) {
      delete params.scenicId
    }
    const res = await getTicketList(params)
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
  searchForm.scenicId = null
  searchForm.typeName = ''
  pagination.pageNum = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
  resetForm()
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogVisible.value = true
  Object.assign(form, {
    id: row.id,
    scenicId: row.scenicId,
    typeName: row.typeName,
    price: row.price,
    stock: row.stock,
    // 日期选择器使用value-format="YYYY-MM-DD"，后端返回的字符串格式可以直接使用
    validStart: row.validStart,
    validEnd: row.validEnd
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('是否删除该票种？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteTicket(row.id)
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
    
    // 日期选择器已使用value-format="YYYY-MM-DD"，直接提交即可
    const submitData = {
      ...form
    }
    
    if (isEdit.value) {
      const res = await updateTicket(submitData)
      if (res.code === 200) {
        ElMessage.success('修改票种成功')
        dialogVisible.value = false
        loadData()
      }
    } else {
      const res = await addTicket(submitData)
      if (res.code === 200) {
        ElMessage.success('新增票种成功')
        dialogVisible.value = false
        loadData()
      }
    }
  } catch (error) {
    console.error('提交失败：', error)
  }
}

const resetForm = () => {
  Object.assign(form, {
    id: null,
    scenicId: null,
    typeName: '',
    price: null,
    stock: 0,
    validStart: null,
    validEnd: null
  })
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadScenicList()
  // 默认加载全部数据
  loadData()
})
</script>

<style scoped>
.ticket-management {
  background: white;
  border-radius: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}
</style>
