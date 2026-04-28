<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card-1">
          <div class="stat-icon"><el-icon><UserFilled /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
            <div class="stat-label">总用户数</div>
            <div class="stat-extra">今日新增 +{{ stats.todayUsers || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card-2">
          <div class="stat-icon"><el-icon><List /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            <div class="stat-label">总订单数</div>
            <div class="stat-extra">待处理 {{ stats.pendingOrders || 0 }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card-3">
          <div class="stat-icon"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">¥{{ formatMoney(stats.totalRevenue) }}</div>
            <div class="stat-label">总收入</div>
            <div class="stat-extra">已完成 {{ stats.completedOrders || 0 }} 单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-card-4">
          <div class="stat-icon"><el-icon><Goods /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.activeProducts || 0 }}</div>
            <div class="stat-label">上架产品</div>
            <div class="stat-extra">待退款 {{ stats.pendingRefunds || 0 }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header><span>📈 近7天趋势</span></template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header><span>📊 订单状态分布</span></template>
          <div ref="statusChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>🏆 产品销量排行</span></template>
          <div ref="rankingChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header><span>🚀 快捷入口</span></template>
          <div class="quick-links">
            <el-button type="primary" @click="$router.push('/orders')">订单管理</el-button>
            <el-button type="success" @click="$router.push('/products')">产品管理</el-button>
            <el-button type="warning" @click="$router.push('/refunds')">退款管理</el-button>
            <el-button type="info" @click="$router.push('/miniapp-users')">用户管理</el-button>
            <el-button @click="$router.push('/transactions')">交易记录</el-button>
            <el-button @click="$router.push('/invoices')">发票管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { UserFilled, List, Money, Goods } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStats, getUserTrend, getOrderTrend, getOrderStatus, getProductRanking } from '../api/dashboard'

const stats = ref<any>({})
const trendChartRef = ref<HTMLElement>()
const statusChartRef = ref<HTMLElement>()
const rankingChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null
let rankingChart: echarts.ECharts | null = null

const formatMoney = (val: any) => {
  if (!val) return '0.00'
  return Number(val).toFixed(2)
}

const loadStats = async () => {
  try {
    const res = await getStats()
    stats.value = res.data
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

const loadTrendChart = async () => {
  try {
    const [userRes, orderRes] = await Promise.all([getUserTrend(), getOrderTrend()])
    const dates = userRes.data.map((item: any) => item.date.substring(5))
    const userData = userRes.data.map((item: any) => item.count)
    const orderData = orderRes.data.map((item: any) => item.count)

    if (trendChartRef.value) {
      trendChart = echarts.init(trendChartRef.value)
      trendChart.setOption({
        tooltip: { trigger: 'axis' },
        legend: { data: ['新增用户', '新增订单'] },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates },
        yAxis: { type: 'value' },
        series: [
          { name: '新增用户', type: 'line', smooth: true, data: userData, itemStyle: { color: '#409eff' } },
          { name: '新增订单', type: 'bar', data: orderData, itemStyle: { color: '#67c23a' } }
        ]
      })
    }
  } catch (e) {
    console.error('加载趋势图失败', e)
  }
}

const loadStatusChart = async () => {
  try {
    const res = await getOrderStatus()
    if (statusChartRef.value && res.data.length > 0) {
      statusChart = echarts.init(statusChartRef.value)
      statusChart.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie', radius: ['40%', '70%'],
          data: res.data,
          emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
        }]
      })
    }
  } catch (e) {
    console.error('加载状态分布失败', e)
  }
}

const loadRankingChart = async () => {
  try {
    const res = await getProductRanking()
    if (rankingChartRef.value && res.data.length > 0) {
      rankingChart = echarts.init(rankingChartRef.value)
      rankingChart.setOption({
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'value' },
        yAxis: { type: 'category', data: res.data.map((i: any) => i.name).reverse() },
        series: [{ type: 'bar', data: res.data.map((i: any) => i.count).reverse(), itemStyle: { color: '#e6a23c' } }]
      })
    }
  } catch (e) {
    console.error('加载排行榜失败', e)
  }
}

const handleResize = () => {
  trendChart?.resize()
  statusChart?.resize()
  rankingChart?.resize()
}

onMounted(() => {
  loadStats()
  loadTrendChart()
  loadStatusChart()
  loadRankingChart()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  statusChart?.dispose()
  rankingChart?.dispose()
})
</script>

<style scoped>
.dashboard-container { padding: 20px; }
.stat-cards { margin-bottom: 20px; }
.stat-card { display: flex; align-items: center; padding: 20px; border-radius: 8px; color: #fff; }
.stat-card-1 { background: linear-gradient(135deg, #409eff, #66b1ff); }
.stat-card-2 { background: linear-gradient(135deg, #67c23a, #85ce61); }
.stat-card-3 { background: linear-gradient(135deg, #e6a23c, #ebb563); }
.stat-card-4 { background: linear-gradient(135deg, #f56c6c, #f89898); }
.stat-icon { font-size: 48px; margin-right: 20px; }
.stat-info { flex: 1; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-label { font-size: 14px; opacity: 0.9; margin: 4px 0; }
.stat-extra { font-size: 12px; opacity: 0.8; }
.chart-row { margin-bottom: 20px; }
.chart-container { height: 300px; }
.quick-links { display: flex; gap: 12px; flex-wrap: wrap; }
</style>
