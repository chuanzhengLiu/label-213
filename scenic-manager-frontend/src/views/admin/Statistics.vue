<template>
  <div class="statistics">
    <el-card>
      <template #header>
        <span>统计分析</span>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
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

      <el-row :gutter="20" style="margin-top: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">总销售额</div>
              <div class="stat-value">¥{{ statistics.totalSales || 0 }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">总销量</div>
              <div class="stat-value">{{ statistics.totalNum || 0 }} 张</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-card class="stat-card">
            <div class="stat-item">
              <div class="stat-label">平均单价</div>
              <div class="stat-value">
                ¥{{ statistics.totalNum > 0 ? (statistics.totalSales / statistics.totalNum).toFixed(2) : 0 }}
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card style="margin-top: 20px">
        <template #header>
          <span>票种销量排行</span>
        </template>
        <el-table :data="statistics.ticketTypeRank || []" border>
          <el-table-column type="index" label="排名" width="80" />
          <el-table-column prop="ticketTypeName" label="票种名称" />
          <el-table-column prop="salesCount" label="销量" width="120">
            <template #default="{ row }">{{ row.salesCount }} 张</template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOrderStatistics } from '@/api/order'

const dateRange = ref(null)
const statistics = reactive({
  totalSales: 0,
  totalNum: 0,
  ticketTypeRank: []
})

const searchForm = reactive({
  startTime: null,
  endTime: null
})

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
  try {
    const res = await getOrderStatistics(searchForm)
    if (res.code === 200) {
      Object.assign(statistics, res.data)
    }
  } catch (error) {
    console.error('加载统计数据失败：', error)
  }
}

const resetSearch = () => {
  searchForm.startTime = null
  searchForm.endTime = null
  dateRange.value = null
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.statistics {
  background: white;
  border-radius: 4px;
}

.search-form {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
}
</style>
