<template>
  <div class="buy-ticket">
    <el-card>
      <template #header>
        <h2 style="text-align: center; margin: 0">在线购票</h2>
      </template>

      <!-- 景区列表 -->
      <div class="scenic-list">
        <h3 style="margin-bottom: 20px">选择景区</h3>
        <el-row :gutter="20">
          <el-col
            v-for="scenic in scenicList"
            :key="scenic.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
            style="margin-bottom: 20px"
          >
            <el-card
              :class="['scenic-card', { active: selectedScenic?.id === scenic.id }]"
              @click="selectScenic(scenic)"
              shadow="hover"
            >
              <h4>{{ scenic.name }}</h4>
              <p class="scenic-intro">{{ scenic.intro || '暂无简介' }}</p>
              <p class="scenic-area">区域：{{ scenic.area || '未知' }}</p>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 票种选择 -->
      <div v-if="selectedScenic" class="ticket-list">
        <h3 style="margin-bottom: 20px">{{ selectedScenic.name }} - 票种选择</h3>
        <el-row :gutter="20">
          <el-col
            v-for="ticket in ticketList"
            :key="ticket.id"
            :xs="24"
            :sm="12"
            :md="8"
            style="margin-bottom: 20px"
          >
            <el-card
              :class="['ticket-card', { selected: selectedTicket?.id === ticket.id, disabled: ticket.stock === 0 }]"
              @click="selectTicket(ticket)"
              shadow="hover"
            >
              <h4>{{ ticket.typeName }}</h4>
              <p class="ticket-price">¥{{ ticket.price }}</p>
              <p class="ticket-stock">
                剩余库存：{{ ticket.stock }} 张
                <el-tag v-if="ticket.stock === 0" type="danger" size="small">已售罄</el-tag>
              </p>
              <p class="ticket-valid">
                有效期：{{ ticket.validStart }} 至 {{ ticket.validEnd }}
              </p>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 数量选择和信息填写 -->
      <div v-if="selectedTicket" class="order-form">
        <el-card>
          <h3 style="margin-bottom: 20px">订单信息</h3>
          <el-form :model="orderForm" :rules="rules" ref="orderFormRef" label-width="100px">
            <el-form-item label="票种">
              <span>{{ selectedTicket.typeName }} - ¥{{ selectedTicket.price }}</span>
            </el-form-item>
            <el-form-item label="剩余库存">
              <span>{{ selectedTicket.stock }} 张</span>
            </el-form-item>
            <el-form-item label="购买数量" prop="ticketNum">
              <el-input-number
                v-model="orderForm.ticketNum"
                :min="1"
                :max="selectedTicket.stock"
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item label="总金额">
              <span class="total-amount">
                ¥{{ (selectedTicket.price * orderForm.ticketNum).toFixed(2) }}
              </span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="showBuyDialog" :disabled="selectedTicket.stock === 0">
                立即购票
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 购票信息对话框 -->
      <el-dialog
        v-model="buyDialogVisible"
        title="填写购票信息"
        width="500px"
        @close="resetBuyForm"
      >
        <el-form :model="buyForm" :rules="buyRules" ref="buyFormRef" label-width="100px">
          <el-form-item label="购票人姓名" prop="userName">
            <el-input v-model="buyForm.userName" placeholder="请输入购票人姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="userPhone">
            <el-input v-model="buyForm.userPhone" placeholder="请输入手机号" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="buyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleBuy" :loading="buyLoading">确认购票</el-button>
        </template>
      </el-dialog>

      <!-- 购票成功对话框 -->
      <el-dialog
        v-model="successDialogVisible"
        title="购票成功"
        width="400px"
      >
        <div style="text-align: center; padding: 20px">
          <el-icon style="font-size: 48px; color: #67c23a; margin-bottom: 20px">
            <CircleCheck />
          </el-icon>
          <p style="font-size: 16px; margin-bottom: 10px">购票成功！</p>
          <p style="color: #666">订单编号：{{ orderNo }}</p>
        </div>
        <template #footer>
          <el-button @click="goHome">返回首页</el-button>
          <el-button type="primary" @click="successDialogVisible = false">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import { getScenicList } from '@/api/scenic'
import { getTicketList } from '@/api/ticket'
import { createOrder } from '@/api/order'

const router = useRouter()
const route = useRoute()

const scenicList = ref([])
const ticketList = ref([])
const selectedScenic = ref(null)
const selectedTicket = ref(null)
const buyDialogVisible = ref(false)
const successDialogVisible = ref(false)
const buyLoading = ref(false)
const orderNo = ref('')
const orderFormRef = ref(null)
const buyFormRef = ref(null)

const orderForm = reactive({
  ticketNum: 1
})

const buyForm = reactive({
  userName: '',
  userPhone: ''
})

const rules = {
  ticketNum: [
    { required: true, message: '请输入购买数量', trigger: 'blur' },
    { type: 'number', min: 1, message: '购买数量至少为1', trigger: 'blur' }
  ]
}

const buyRules = {
  userName: [{ required: true, message: '请输入购票人姓名', trigger: 'blur' }],
  userPhone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const loadScenicList = async () => {
  try {
    const res = await getScenicList({ pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      scenicList.value = res.data.records.sort((a, b) => a.name.localeCompare(b.name))
    }
  } catch (error) {
    console.error('加载景区列表失败：', error)
  }
}

const selectScenic = async (scenic) => {
  selectedScenic.value = scenic
  selectedTicket.value = null
  orderForm.ticketNum = 1
  
  try {
    const res = await getTicketList({ scenicId: scenic.id, pageNum: 1, pageSize: 1000 })
    if (res.code === 200) {
      ticketList.value = res.data.records
    }
  } catch (error) {
    console.error('加载票种列表失败：', error)
  }
}

const selectTicket = (ticket) => {
  if (ticket.stock === 0) {
    ElMessage.warning('该票种已售罄')
    return
  }
  selectedTicket.value = ticket
  orderForm.ticketNum = 1
}

const showBuyDialog = () => {
  if (!selectedTicket.value) {
    ElMessage.warning('请先选择票种')
    return
  }
  
  if (selectedTicket.value.stock < orderForm.ticketNum) {
    ElMessage.warning(`库存不足，当前剩余${selectedTicket.value.stock}张`)
    return
  }
  
  buyDialogVisible.value = true
}

const handleBuy = async () => {
  try {
    await buyFormRef.value.validate()
    
    if (selectedTicket.value.stock < orderForm.ticketNum) {
      ElMessage.warning(`库存不足，当前剩余${selectedTicket.value.stock}张`)
      return
    }
    
    buyLoading.value = true
    
    const res = await createOrder({
      scenicId: selectedScenic.value.id,
      ticketTypeId: selectedTicket.value.id,
      userName: buyForm.userName,
      userPhone: buyForm.userPhone,
      ticketNum: orderForm.ticketNum
    })
    
    if (res.code === 200) {
      orderNo.value = res.data.orderNo
      buyDialogVisible.value = false
      successDialogVisible.value = true
      resetBuyForm()
      
      // 刷新票种列表
      await selectScenic(selectedScenic.value)
    }
  } catch (error) {
    ElMessage.error(error.message || '下单失败')
  } finally {
    buyLoading.value = false
  }
}

const resetBuyForm = () => {
  buyForm.userName = ''
  buyForm.userPhone = ''
  buyFormRef.value?.clearValidate()
}

const goHome = () => {
  // 如果在后台管理系统中，跳转到订单管理页面；否则跳转到游客页面
  if (route.path.startsWith('/admin')) {
    router.push('/admin/order')
  } else {
    router.push('/visitor')
  }
  successDialogVisible.value = false
}

onMounted(() => {
  loadScenicList()
})
</script>

<style scoped>
.buy-ticket {
  max-width: 1200px;
  margin: 0 auto;
}

.scenic-list,
.ticket-list,
.order-form {
  margin-top: 30px;
}

.scenic-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
}

.scenic-card:hover {
  transform: translateY(-5px);
}

.scenic-card.active {
  border-color: #409eff;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.3);
}

.scenic-intro {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.scenic-area {
  color: #999;
  font-size: 12px;
  margin: 5px 0 0;
}

.ticket-card {
  cursor: pointer;
  transition: all 0.3s;
  height: 100%;
  text-align: center;
}

.ticket-card:hover:not(.disabled) {
  transform: translateY(-5px);
}

.ticket-card.selected {
  border-color: #409eff;
  box-shadow: 0 0 10px rgba(64, 158, 255, 0.3);
}

.ticket-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ticket-price {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin: 15px 0;
}

.ticket-stock {
  color: #666;
  margin: 10px 0;
}

.ticket-valid {
  color: #999;
  font-size: 12px;
  margin: 5px 0 0;
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}
</style>
