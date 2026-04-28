<template>
  <div class="container">
    <div class="handle-box">
      <el-select v-model="query.status" placeholder="退款状态" class="handle-select mr10" clearable>
        <el-option label="待处理" value="pending" />
        <el-option label="处理中" value="processing" />
        <el-option label="成功" value="success" />
        <el-option label="失败" value="failed" />
        <el-option label="已拒绝" value="rejected" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="refundNo" label="退款单号" width="200" />
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="refundAmount" label="退款金额" width="100">
        <template #default="{ row }">¥{{ row.refundAmount }}</template>
      </el-table-column>
      <el-table-column prop="refundReason" label="退款原因" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="申请时间" width="170" />
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <template v-if="row.status === 'pending'">
            <el-button type="success" link @click="handleApprove(row)">同意</el-button>
            <el-button type="danger" link @click="handleReject(row)">拒绝</el-button>
          </template>
          <template v-else-if="row.status === 'processing'">
            <el-button type="primary" link @click="handleSync(row)">查询状态</el-button>
            <el-button type="success" link @click="handleComplete(row)">完成退款</el-button>
          </template>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getRefunds, approveRefund, rejectRefund, completeRefund, syncRefundStatus } from '../api/business';

const tableData = ref([]);
const query = ref({ status: '', page: 0, size: 10 });

const statusMap: Record<string, string> = {
  pending: '待处理', processing: '处理中', success: '成功', failed: '失败', rejected: '已拒绝'
};
const statusType: Record<string, string> = {
  pending: 'warning', processing: 'primary', success: 'success', failed: 'danger', rejected: 'info'
};

const fetchData = async () => {
  const res = await getRefunds(query.value);
  tableData.value = res.data.content;
};

const handleSearch = () => { query.value.page = 0; fetchData(); };

const handleApprove = async (row: any) => {
  await ElMessageBox.confirm('确定同意该退款申请?', '提示');
  try {
    await approveRefund(row.id, { operatorId: 1, operatorName: 'admin' });
    ElMessage.success('操作成功，已调用微信退款API');
    fetchData();
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败');
  }
};

const handleReject = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝退款');
  await rejectRefund(row.id, { reason: value, operatorId: 1, operatorName: 'admin' });
  ElMessage.success('操作成功');
  fetchData();
};

const handleSync = async (row: any) => {
  try {
    await syncRefundStatus(row.id);
    ElMessage.success('状态同步成功');
    fetchData();
  } catch (error: any) {
    ElMessage.error(error.message || '同步失败');
  }
};

const handleComplete = async (row: any) => {
  await ElMessageBox.confirm('确定完成退款?', '提示');
  await completeRefund(row.id, {});
  ElMessage.success('退款完成');
  fetchData();
};

onMounted(fetchData);
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.handle-select { width: 150px; }
.mr10 { margin-right: 10px; }
</style>
