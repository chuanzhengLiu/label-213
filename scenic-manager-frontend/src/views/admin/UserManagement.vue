<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="handleAdd" v-if="canDelete">新增用户</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色"  style="width: 200px" clearable>
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'info'">
              {{ row.role === 1 ? '超级管理员' : '普通管理员' }}
            </el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" placeholder="默认密码：123456" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通管理员" :value="2" />
          </el-select>
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
import { getUserList, addUser, updateUser, deleteUser } from '@/api/user'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  username: '',
  role: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  username: '',
  password: '123456',
  role: null
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const canDelete = computed(() => {
  return userStore.userInfo?.role === 1
})

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
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
  searchForm.username = ''
  searchForm.role = null
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
    username: row.username,
    password: '',
    role: row.role
  })
}

const handleDelete = async (row) => {
  if (row.role === 1) {
    ElMessage.warning('禁止删除超级管理员账号')
    return
  }
  
  try {
    await ElMessageBox.confirm('是否删除该用户？', '提示', {
      type: 'warning'
    })
    
    const res = await deleteUser(row.id)
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
      const res = await updateUser({
        id: form.id,
        username: form.username,
        role: form.role
      })
      if (res.code === 200) {
        ElMessage.success('修改用户成功')
        dialogVisible.value = false
        loadData()
      }
    } else {
      const res = await addUser(form)
      if (res.code === 200) {
        ElMessage.success('新增用户成功')
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
    username: '',
    password: '123456',
    role: null
  })
  formRef.value?.clearValidate()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-management {
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
