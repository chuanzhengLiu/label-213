<template>
  <div class="scenic-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>景区管理</span>
          <el-button type="primary" @click="handleAdd" v-if="canDelete">新增景区</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="景区名称">
          <el-input v-model="searchForm.name" placeholder="请输入景区名称" clearable />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="searchForm.area" placeholder="请输入区域" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="景区名称" />
        <el-table-column prop="intro" label="简介" show-overflow-tooltip />
        <el-table-column prop="area" label="区域" />
        <el-table-column prop="createTime" label="创建时间" />
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
        <el-form-item label="景区名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.intro" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="区域">
          <el-input v-model="form.area" />
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
import { getScenicList, addScenic, updateScenic, deleteScenic } from '@/api/scenic'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  name: '',
  area: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  name: '',
  intro: '',
  area: ''
})

const rules = {
  name: [{ required: true, message: '请输入景区名称', trigger: 'blur' }]
}

const canDelete = computed(() => {
  return userStore.userInfo?.role === 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑景区' : '新增景区')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getScenicList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
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
  searchForm.name = ''
  searchForm.area = ''
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
  Object.assign(form, row)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('是否删除该景区？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteScenic(row.id)
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
    
    if (isEdit.value) {
      const res = await updateScenic(form)
      if (res.code === 200) {
        ElMessage.success('修改景区成功')
        dialogVisible.value = false
        loadData()
      }
    } else {
      const res = await addScenic(form)
      if (res.code === 200) {
        ElMessage.success('新增景区成功')
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
    name: '',
    intro: '',
    area: ''
  })
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.scenic-management {
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
