<template>
  <div class="container">
    <div class="handle-box">
      <el-select v-model="query.status" placeholder="发票状态" class="handle-select mr10" clearable>
        <el-option label="待开票" value="pending" />
        <el-option label="已开票" value="issued" />
        <el-option label="开票失败" value="failed" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">{{ stats.pendingCount }}</div><div>待开票</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">{{ stats.issuedCount }}</div><div>已开票</div></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-item"><div class="stat-num">{{ stats.failedCount }}</div><div>开票失败</div></div>
        </el-card>
      </el-col>
    </el-row>

    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="title" label="发票抬头" />
      <el-table-column prop="invoiceType" label="类型" width="80">
        <template #default="{ row }">{{ row.invoiceType === 'personal' ? '个人' : '企业' }}</template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{ row }">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="180" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="170" />
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <template v-if="row.status === 'pending'">
            <el-button type="success" link @click="handleIssue(row)">开票</el-button>
            <el-button type="danger" link @click="handleFail(row)">失败</el-button>
          </template>
          <el-link v-else-if="row.invoiceUrl" type="primary" :href="row.invoiceUrl" target="_blank">查看发票</el-link>
        </template>
      </el-table-column>
    </el-table>

    <!-- 开票对话框 -->
    <el-dialog v-model="issueDialogVisible" title="开具发票" width="500px">
      <el-form :model="issueForm" label-width="100px">
        <el-form-item label="发票号码" required>
          <el-input v-model="issueForm.invoiceNo" placeholder="请输入发票号码" />
        </el-form-item>
        <el-form-item label="发票链接" required>
          <el-input v-model="issueForm.invoiceUrl" placeholder="请输入发票下载链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="issueDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitIssue">确定</el-button>
      </template>
    </el-dialog>

    <!-- 失败原因对话框 -->
    <el-dialog v-model="failDialogVisible" title="开票失败" width="500px">
      <el-form :model="failForm" label-width="100px">
        <el-form-item label="失败原因" required>
          <el-input v-model="failForm.reason" type="textarea" :rows="3" placeholder="请输入失败原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="failDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="submitFail">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getInvoices, getInvoiceStats, issueInvoice, failInvoice } from '../api/finance';

interface Invoice {
  id: number;
  orderNo: string;
  title: string;
  invoiceType: string;
  taxNo: string;
  amount: number;
  email: string;
  status: string;
  invoiceNo: string;
  invoiceUrl: string;
  createTime: string;
}

const tableData = ref<Invoice[]>([]);
const query = reactive({ status: '' });
const stats = reactive({ pendingCount: 0, issuedCount: 0, failedCount: 0 });

const statusMap: Record<string, string> = { pending: '待开票', issued: '已开票', failed: '开票失败' };
const statusType: Record<string, string> = { pending: 'warning', issued: 'success', failed: 'danger' };

const issueDialogVisible = ref(false);
const failDialogVisible = ref(false);
const currentInvoice = ref<Invoice | null>(null);
const issueForm = reactive({ invoiceNo: '', invoiceUrl: '' });
const failForm = reactive({ reason: '' });

const fetchData = async () => {
  const res = await getInvoices({ status: query.status || undefined });
  tableData.value = res.data?.content || res.data || [];
};

const fetchStats = async () => {
  const res = await getInvoiceStats();
  Object.assign(stats, res.data);
};

const handleSearch = () => fetchData();

const handleIssue = (row: Invoice) => {
  currentInvoice.value = row;
  issueForm.invoiceNo = '';
  issueForm.invoiceUrl = '';
  issueDialogVisible.value = true;
};

const handleFail = (row: Invoice) => {
  currentInvoice.value = row;
  failForm.reason = '';
  failDialogVisible.value = true;
};

const submitIssue = async () => {
  if (!issueForm.invoiceNo || !issueForm.invoiceUrl) {
    ElMessage.warning('请填写完整信息');
    return;
  }
  await issueInvoice(currentInvoice.value!.id, issueForm);
  ElMessage.success('开票成功');
  issueDialogVisible.value = false;
  fetchData();
  fetchStats();
};

const submitFail = async () => {
  if (!failForm.reason) {
    ElMessage.warning('请填写失败原因');
    return;
  }
  await failInvoice(currentInvoice.value!.id, failForm);
  ElMessage.success('已标记为失败');
  failDialogVisible.value = false;
  fetchData();
  fetchStats();
};

onMounted(() => {
  fetchData();
  fetchStats();
});
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.handle-select { width: 150px; }
.mr10 { margin-right: 10px; }
.table { width: 100%; font-size: 14px; }
.stat-item { text-align: center; }
.stat-num { font-size: 28px; font-weight: bold; color: #409eff; }
</style>
