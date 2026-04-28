<template>
  <div class="container">
    <div class="handle-box">
      <el-input v-model="query.keyword" placeholder="订单号/企业名称" class="handle-input mr10" clearable />
      <el-select v-model="query.status" placeholder="订单状态" class="handle-select mr10" clearable>
        <el-option label="待付款" value="pending" />
        <el-option label="已付款" value="paid" />
        <el-option label="审核中" value="reviewing" />
        <el-option label="办理中" value="processing" />
        <el-option label="已完成" value="completed" />
        <el-option label="已取消" value="cancelled" />
        <el-option label="退款中" value="refunding" />
        <el-option label="已退款" value="refunded" />
      </el-select>
      <el-button type="primary" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="4"><el-card shadow="hover"><div class="stat-item"><div class="stat-num">{{ stats.total }}</div><div>总订单</div></div></el-card></el-col>
      <el-col :span="4"><el-card shadow="hover"><div class="stat-item"><div class="stat-num">{{ stats.pending }}</div><div>待付款</div></div></el-card></el-col>
      <el-col :span="4"><el-card shadow="hover"><div class="stat-item"><div class="stat-num">{{ stats.paid }}</div><div>已付款</div></div></el-card></el-col>
      <el-col :span="4"><el-card shadow="hover"><div class="stat-item"><div class="stat-num">{{ stats.processing }}</div><div>办理中</div></div></el-card></el-col>
      <el-col :span="4"><el-card shadow="hover"><div class="stat-item"><div class="stat-num">{{ stats.completed }}</div><div>已完成</div></div></el-card></el-col>
    </el-row>

    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="productName" label="产品" width="150" />
      <el-table-column label="企业名称">
        <template #default="{ row }">{{ getCompanyName(row) }}</template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template #default="{ row }">¥{{ row.payAmount || row.amount }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="assigneeName" label="办理人" width="100" />
      <el-table-column label="创建时间" width="170">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
          <el-button v-if="row.status === 'paid'" type="success" link size="small" @click="handleReview(row)">审核</el-button>
          <el-button v-if="['paid', 'reviewing'].includes(row.status)" type="warning" link size="small" @click="openAssignDialog(row)">派单</el-button>
          <el-button v-if="row.status === 'processing'" type="primary" link size="small" @click="handleProgress(row)">进度</el-button>
          <el-button v-if="row.status === 'processing'" type="success" link size="small" @click="handleComplete(row)">完成</el-button>
          <el-button v-if="['paid', 'reviewing', 'processing'].includes(row.status)" type="danger" link size="small" @click="handleCancel(row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      class="pagination"
      background
      layout="total, prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page + 1"
      @current-change="handlePageChange"
    />

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="1000px" destroy-on-close>
      <div v-if="currentOrder" class="order-detail">
        <el-row :gutter="20">
          <!-- 左侧：办理进度 -->
          <el-col :span="10">
            <div class="detail-section">
              <div class="section-title">
                办理进度
                <el-button type="primary" link size="small" style="float: right" @click="viewMaterials">查看材料</el-button>
              </div>
              <div class="progress-container">
                <el-timeline v-if="progressList.length">
                  <el-timeline-item v-for="item in progressList" :key="item.id" :timestamp="formatTime(item.createTime)" placement="top" :color="item.status === 'completed' ? '#67c23a' : '#409eff'">
                    <div class="progress-item">
                      <div class="progress-title">{{ item.title }}</div>
                      <div class="progress-content" v-if="item.content">{{ item.content }}</div>
                      <div class="progress-operator">操作人: {{ item.operatorName || '系统' }}</div>
                    </div>
                  </el-timeline-item>
                </el-timeline>
                <el-empty v-else description="暂无进度记录" :image-size="80" />
              </div>
            </div>
          </el-col>
          <!-- 右侧：订单信息 -->
          <el-col :span="14">
            <div class="detail-section">
              <div class="section-title">订单信息</div>
              <el-descriptions :column="2" border size="small">
                <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
                <el-descriptions-item label="订单状态">
                  <el-tag :type="statusType[currentOrder.status]" size="small">{{ statusMap[currentOrder.status] }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="产品名称">{{ currentOrder.productName }}</el-descriptions-item>
                <el-descriptions-item label="订单金额">¥{{ currentOrder.amount }}</el-descriptions-item>
                <el-descriptions-item label="优惠金额">¥{{ currentOrder.discountAmount || 0 }}</el-descriptions-item>
                <el-descriptions-item label="实付金额">
                  <span class="pay-amount">¥{{ currentOrder.payAmount || currentOrder.amount }}</span>
                </el-descriptions-item>
                <el-descriptions-item label="办理人员">{{ currentOrder.assigneeName || '未分配' }}</el-descriptions-item>
                <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
                <el-descriptions-item label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
                <el-descriptions-item label="完成时间">{{ formatTime(currentOrder.completeTime) }}</el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
              </el-descriptions>

              <div class="section-title" style="margin-top: 16px">提交信息</div>
              <el-descriptions :column="1" border size="small" v-if="formData">
                <el-descriptions-item v-for="(value, key) in formData" :key="key" :label="fieldLabels[key] || key">
                  {{ value || '-' }}
                </el-descriptions-item>
              </el-descriptions>
              <el-empty v-else description="暂无提交信息" :image-size="60" />
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button v-if="currentOrder && ['paid', 'reviewing'].includes(currentOrder.status)" type="primary" @click="openAssignDialog(currentOrder)">派单</el-button>
      </template>
    </el-dialog>

    <!-- 派单弹窗 -->
    <el-dialog v-model="assignVisible" title="派单" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="办理人" required>
          <el-select v-model="assignForm.assigneeId" placeholder="请选择办理人员" style="width: 100%" filterable @change="onAssigneeChange">
            <el-option v-for="user in assigneeList" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssign" :loading="assignLoading">确定派单</el-button>
      </template>
    </el-dialog>

    <!-- 更新进度弹窗 -->
    <el-dialog v-model="progressVisible" title="更新进度" width="600px">
      <el-form :model="progressForm" label-width="80px">
        <el-form-item label="进度标题" required>
          <el-input v-model="progressForm.title" placeholder="请输入进度标题" />
        </el-form-item>
        <el-form-item label="进度描述">
          <el-input v-model="progressForm.content" type="textarea" :rows="3" placeholder="请输入进度描述" />
        </el-form-item>
        <el-form-item label="上传材料">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :on-success="onUploadSuccess"
            :on-remove="onUploadRemove"
            :before-upload="beforeUpload"
            :file-list="progressForm.fileList"
            multiple
            name="files"
          >
            <el-button type="primary"><el-icon><Plus /></el-icon>选择文件</el-button>
            <template #tip>
              <div class="upload-tip">支持图片、PDF、Word等文件，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProgress" :loading="progressLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看材料弹窗 -->
    <el-dialog v-model="materialsVisible" title="订单材料" width="750px">
      <div v-if="groupedMaterials.length">
        <div v-for="group in groupedMaterials" :key="group.progressId" class="material-group">
          <div class="group-title">{{ group.progressTitle }}</div>
          <el-table :data="group.materials" border size="small">
            <el-table-column prop="name" label="文件名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="materialType" label="类型" width="90">
              <template #default="{ row }">{{ materialTypeMap[row.materialType] || row.materialType || '-' }}</template>
            </el-table-column>
            <el-table-column prop="uploaderName" label="上传人" width="90" />
            <el-table-column label="上传时间" width="150">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="110" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="previewFile(row)">预览</el-button>
                <el-button type="success" link size="small" @click="downloadFile(row)">下载</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <el-empty v-else description="暂无材料" />
      <template #footer>
        <el-button @click="materialsVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';
import { getOrders, getOrder, reviewOrder, assignOrder, updateOrderProgress, completeOrder, cancelOrder, getOrderStats, getOrderProgress, uploadOrderMaterial, getOrderMaterials } from '@/api/business';
import { getAssignees } from '@/api/user';
import { BASE_URI, getFullUrl } from '@/api/base';

const route = useRoute();
const tableData = ref([]);
const total = ref(0);
const stats = ref({ total: 0, pending: 0, paid: 0, processing: 0, completed: 0 });
const query = ref({ keyword: '', status: '', page: 0, size: 10, userId: null as number | null });

// 详情弹窗
const detailVisible = ref(false);
const currentOrder = ref<any>(null);
const progressList = ref<any[]>([]);
const formData = computed(() => {
  if (!currentOrder.value?.formData) return null;
  try { return JSON.parse(currentOrder.value.formData); } catch { return null; }
});

// 派单弹窗
const assignVisible = ref(false);
const assignLoading = ref(false);
const assignForm = ref({ assigneeId: null as number | null, assigneeName: '' });
const assigneeList = ref<any[]>([]);
const assignOrderId = ref<number | null>(null);

// 更新进度弹窗
const progressVisible = ref(false);
const progressLoading = ref(false);
const progressOrderId = ref<number | null>(null);
const progressForm = ref({ title: '', content: '', fileList: [] as any[] });
const uploadedFiles = ref<any[]>([]);

// 材料查看弹窗
const materialsVisible = ref(false);
const materialsList = ref<any[]>([]);
const materialTypeMap: Record<string, string> = {
  license: '营业执照', report: '年报截图', id_card: '身份证', other: '其他'
};

// 按进度分组的材料
const groupedMaterials = computed(() => {
  const groups: Record<string, any[]> = {};
  const progressMap: Record<number, string> = {};
  // 构建进度ID到标题的映射
  progressList.value.forEach((p: any) => {
    progressMap[p.id] = p.title;
  });
  // 分组材料
  materialsList.value.forEach((m: any) => {
    const key = m.progressId ? `${m.progressId}` : 'other';
    const title = m.progressId ? (progressMap[m.progressId] || `进度#${m.progressId}`) : '其他材料';
    if (!groups[key]) {
      groups[key] = [];
    }
    groups[key].push({ ...m, progressTitle: title });
  });
  // 转换为数组
  return Object.entries(groups).map(([key, items]) => ({
    progressId: key,
    progressTitle: items[0]?.progressTitle || '其他材料',
    materials: items
  }));
});

// 上传配置
const uploadUrl = computed(() => `${BASE_URI}/storage/upload`);
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('token')}` }));

// 字段标签映射
const fieldLabels: Record<string, string> = {
  company_name: '企业名称', companyName: '企业名称',
  credit_code: '统一社会信用代码', creditCode: '统一社会信用代码',
  legal_person: '法人姓名', legalPerson: '法人姓名',
  legal_phone: '法人手机号', legalPhone: '法人手机号',
  abnormal_reason: '异常原因', abnormalReason: '异常原因'
};

const statusMap: Record<string, string> = {
  pending: '待付款', paid: '已付款', reviewing: '审核中', processing: '办理中',
  completed: '已完成', cancelled: '已取消', refunding: '退款中', refunded: '已退款'
};
const statusType: Record<string, string> = {
  pending: 'warning', paid: 'success', reviewing: 'info', processing: 'primary',
  completed: 'success', cancelled: 'info', refunding: 'warning', refunded: 'info'
};

const fetchData = async () => {
  const res = await getOrders(query.value);
  tableData.value = res.data.content;
  total.value = res.data.totalElements;
};

const fetchStats = async () => {
  const res = await getOrderStats();
  stats.value = res.data;
};

const fetchAssignees = async () => {
  try {
    const res = await getAssignees();
    assigneeList.value = res.data.content || res.data || [];
  } catch { assigneeList.value = []; }
};

const handleSearch = () => { query.value.page = 0; fetchData(); };
const handlePageChange = (page: number) => { query.value.page = page - 1; fetchData(); };

// 查看详情
const handleDetail = async (row: any) => {
  const res = await getOrder(row.id);
  currentOrder.value = res.data;
  // 获取进度
  try {
    const progressRes = await getOrderProgress(row.id);
    progressList.value = progressRes.data || [];
  } catch { progressList.value = []; }
  detailVisible.value = true;
};

// 打开派单弹窗
const openAssignDialog = (row: any) => {
  assignOrderId.value = row.id;
  assignForm.value = { assigneeId: null, assigneeName: '' };
  fetchAssignees();
  assignVisible.value = true;
};

// 选择办理人
const onAssigneeChange = (id: number) => {
  const user = assigneeList.value.find((u: any) => u.id === id);
  assignForm.value.assigneeName = user?.username || '';
};

// 确认派单
const handleAssign = async () => {
  if (!assignForm.value.assigneeId) {
    ElMessage.warning('请选择办理人员');
    return;
  }
  assignLoading.value = true;
  try {
    await assignOrder(assignOrderId.value!, { assigneeId: assignForm.value.assigneeId, assigneeName: assignForm.value.assigneeName });
    ElMessage.success('派单成功');
    assignVisible.value = false;
    detailVisible.value = false;
    fetchData();
  } finally {
    assignLoading.value = false;
  }
};

const handleReview = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入审核备注', '审核订单');
  await reviewOrder(row.id, { approved: true, remark: value });
  ElMessage.success('审核成功');
  fetchData();
};

const handleProgress = (row: any) => {
  progressOrderId.value = row.id;
  progressForm.value = { title: '', content: '', fileList: [] };
  uploadedFiles.value = [];
  progressVisible.value = true;
};

// 查看材料
const viewMaterials = async () => {
  if (!currentOrder.value) return;
  try {
    const res = await getOrderMaterials(currentOrder.value.id);
    materialsList.value = res.data || [];
    materialsVisible.value = true;
  } catch {
    materialsList.value = [];
    materialsVisible.value = true;
  }
};

// 预览文件
const previewFile = (row: any) => {
  const url = getFullUrl(row.fileUrl);
  window.open(url, '_blank');
};

// 下载文件
const downloadFile = (row: any) => {
  const url = getFullUrl(row.fileUrl);
  const link = document.createElement('a');
  link.href = url;
  link.download = row.name;
  link.target = '_blank';
  link.click();
};

// 上传前验证
const beforeUpload = (file: File) => {
  const isLt10M = file.size / 1024 / 1024 < 10;
  if (!isLt10M) { ElMessage.error('文件大小不能超过 10MB'); return false; }
  return true;
};

// 上传成功
const onUploadSuccess = (res: any, file: any) => {
  const data = res.data || res;
  if (Array.isArray(data) && data.length > 0) {
    uploadedFiles.value.push({
      name: file.name,
      fileUrl: data[0].url,
      fileType: file.raw?.type?.startsWith('image') ? 'image' : 'file',
      fileSize: file.size
    });
    ElMessage.success('上传成功');
  }
};

// 移除文件
const onUploadRemove = (file: any) => {
  const index = uploadedFiles.value.findIndex(f => f.name === file.name);
  if (index > -1) uploadedFiles.value.splice(index, 1);
};

// 提交进度
const submitProgress = async () => {
  if (!progressForm.value.title) {
    ElMessage.warning('请输入进度标题');
    return;
  }
  progressLoading.value = true;
  try {
    // 更新进度，获取返回的进度ID
    const progressRes = await updateOrderProgress(progressOrderId.value!, { 
      title: progressForm.value.title, 
      content: progressForm.value.content 
    });
    const progressId = progressRes.data?.id;
    // 上传材料，关联进度ID
    for (const file of uploadedFiles.value) {
      await uploadOrderMaterial(progressOrderId.value!, {
        name: file.name,
        materialType: 'other',
        fileUrl: file.fileUrl,
        fileType: file.fileType,
        fileSize: file.fileSize,
        progressId: progressId
      });
    }
    ElMessage.success('更新成功');
    progressVisible.value = false;
    fetchData();
  } finally {
    progressLoading.value = false;
  }
};

const handleComplete = async (row: any) => {
  await ElMessageBox.confirm('确定完成该订单?', '提示');
  await completeOrder(row.id, { remark: '办理完成' });
  ElMessage.success('操作成功');
  fetchData();
};

const handleCancel = async (row: any) => {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单');
  await cancelOrder(row.id, { reason: value });
  ElMessage.success('取消成功');
  fetchData();
};

const getCompanyName = (row: any) => {
  if (row.companyName) return row.companyName;
  if (row.formData) {
    try {
      const data = JSON.parse(row.formData);
      return data.company_name || data.companyName || '-';
    } catch { return '-'; }
  }
  return '-';
};

const formatTime = (time: any) => {
  if (!time) return '-';
  if (Array.isArray(time)) {
    const [y, m, d, h, min, s] = time;
    return `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')} ${String(h).padStart(2, '0')}:${String(min).padStart(2, '0')}:${String(s || 0).padStart(2, '0')}`;
  }
  return time;
};

onMounted(() => {
  // 从路由获取userId参数
  if (route.query.userId) {
    query.value.userId = Number(route.query.userId);
  }
  fetchData();
  fetchStats();
});
</script>

<style scoped>
.stat-item { text-align: center; }
.stat-num { font-size: 24px; font-weight: bold; color: #409eff; }
.handle-box { margin-bottom: 20px; }
.handle-input { width: 200px; }
.handle-select { width: 150px; }
.mr10 { margin-right: 10px; }
.pagination { margin-top: 20px; }
.order-detail { min-height: 400px; }
.detail-section { background: #fafafa; border-radius: 8px; padding: 16px; height: 100%; }
.section-title { font-size: 15px; font-weight: 600; color: #303133; margin-bottom: 12px; padding-left: 8px; border-left: 3px solid #409eff; }
.progress-container { max-height: 450px; overflow-y: auto; padding-right: 8px; }
.progress-item { padding: 4px 0; }
.progress-title { font-weight: 500; color: #303133; }
.progress-content { font-size: 13px; color: #606266; margin-top: 4px; }
.progress-operator { font-size: 12px; color: #909399; margin-top: 4px; }
.pay-amount { color: #f56c6c; font-weight: 600; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 8px; }
.material-group { margin-bottom: 16px; }
.material-group:last-child { margin-bottom: 0; }
.group-title { font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 8px; padding: 6px 10px; background: #f5f7fa; border-radius: 4px; border-left: 3px solid #409eff; }
</style>
