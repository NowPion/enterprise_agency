<template>
  <div class="container">
    <div class="handle-box">
      <el-select v-model="query.type" placeholder="交易类型" class="handle-select mr10" clearable>
        <el-option label="支付" value="pay" />
        <el-option label="退款" value="refund" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">¥{{ stats.totalPayAmount }}</div><div>总收入</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">¥{{ stats.totalRefundAmount }}</div><div>总退款</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">{{ stats.successCount }}</div><div>成功交易</div></div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">{{ stats.pendingCount }}</div><div>处理中</div></div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="transactionNo" label="交易流水号" width="200" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="transactionType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.transactionType === 'pay' ? 'success' : 'warning'">
            {{ row.transactionType === 'pay' ? '支付' : '退款' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="payChannel" label="渠道" width="100">
        <template #default="{ row }">{{ row.payChannel === 'wechat' ? '微信支付' : row.payChannel }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
    </el-table>

    <el-pagination class="pagination" background layout="total, prev, pager, next"
      :total="total" :page-size="query.size" :current-page="query.page + 1" @current-change="handlePageChange" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { getTransactions, getTransactionStats } from '../api/finance';

const tableData = ref([]);
const total = ref(0);
const stats = ref({ totalPayAmount: 0, totalRefundAmount: 0, successCount: 0, pendingCount: 0 });
const query = reactive({ type: '', page: 0, size: 10 });

const statusMap: Record<string, string> = { pending: '处理中', success: '成功', failed: '失败' };
const statusType: Record<string, string> = { pending: 'warning', success: 'success', failed: 'danger' };

const fetchData = async () => {
  const res = await getTransactions(query);
  tableData.value = res.data.content;
  total.value = res.data.totalElements;
};

const fetchStats = async () => {
  const res = await getTransactionStats();
  stats.value = res.data;
};

const handleSearch = () => { query.page = 0; fetchData(); };
const handlePageChange = (page: number) => { query.page = page - 1; fetchData(); };

onMounted(() => { fetchData(); fetchStats(); });
</script>

<style scoped>
.stat-item { text-align: center; }
.stat-num { font-size: 24px; font-weight: bold; color: #409eff; }
.handle-box { margin-bottom: 20px; }
.handle-select { width: 150px; }
.mr10 { margin-right: 10px; }
.pagination { margin-top: 20px; }
</style>
