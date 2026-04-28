<template>
  <div class="container" v-if="order">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>订单信息</span>
          <el-tag :type="statusType[order.status]" size="large">{{ statusMap[order.status] }}</el-tag>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="产品名称">{{ order.productName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ order.amount }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ order.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ payStatusMap[order.payStatus] }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ order.companyName }}</el-descriptions-item>
        <el-descriptions-item label="信用代码">{{ order.creditCode }}</el-descriptions-item>
        <el-descriptions-item label="法人姓名">{{ order.legalPerson }}</el-descriptions-item>
        <el-descriptions-item label="法人电话">{{ order.legalPhone }}</el-descriptions-item>
        <el-descriptions-item label="办理人员">{{ order.assigneeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="box-card">
          <template #header><span>办理进度</span></template>
          <el-timeline v-if="progressList.length">
            <el-timeline-item v-for="item in progressList" :key="item.id" :timestamp="item.createTime" placement="top">
              <h4>{{ item.title }}</h4>
              <p>{{ item.content }}</p>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无进度记录" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>办理材料</span>
              <el-button type="primary" size="small" :icon="Upload" @click="uploadDialogVisible = true">上传材料</el-button>
            </div>
          </template>
          <el-table :data="materialList" size="small" v-if="materialList.length">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="materialType" label="类型" width="100">
              <template #default="{ row }">{{ materialTypeMap[row.materialType] || row.materialType || '其他' }}</template>
            </el-table-column>
            <el-table-column prop="uploadType" label="上传者" width="80">
              <template #default="{ row }">{{ row.uploadType === 'user' ? '客户' : '管理员' }}</template>
            </el-table-column>
            <el-table-column label="预览" width="100">
              <template #default="{ row }">
                <el-image v-if="isImage(row.fileUrl)" :src="row.fileUrl" :preview-src-list="[row.fileUrl]" fit="cover" style="width: 60px; height: 60px; cursor: pointer;" />
                <el-link v-else type="primary" :href="row.fileUrl" target="_blank">查看文件</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="上传时间" width="160">
              <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="80">
              <template #default="{ row }">
                <el-button type="danger" link size="small" @click="handleDeleteMaterial(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无材料" />
        </el-card>
      </el-col>
    </el-row>

    <div style="margin-top: 20px">
      <el-button @click="router.back()">返回列表</el-button>
    </div>

    <!-- 上传材料弹窗 -->
    <el-dialog v-model="uploadDialogVisible" title="上传材料" width="500px">
      <el-form :model="materialForm" label-width="80px">
        <el-form-item label="材料名称" required>
          <el-input v-model="materialForm.name" placeholder="请输入材料名称" />
        </el-form-item>
        <el-form-item label="材料类型">
          <el-select v-model="materialForm.materialType" style="width: 100%">
            <el-option label="营业执照" value="license" />
            <el-option label="年报截图" value="report" />
            <el-option label="身份证" value="id_card" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件URL" required>
          <el-input v-model="materialForm.fileUrl" placeholder="请输入文件URL" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="materialForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUploadMaterial">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Upload } from '@element-plus/icons-vue';
import { getOrder, getOrderProgress, getOrderMaterials, uploadOrderMaterial, deleteOrderMaterial } from '../api/business';

interface Order {
  id: number; orderNo: string; productName: string; amount: number; payAmount: number;
  status: string; payStatus: string; payTime: string; companyName: string; creditCode: string;
  legalPerson: string; legalPhone: string; assigneeName: string; createTime: string;
}
interface Progress { id: number; title: string; content: string; createTime: string; }
interface Material { id: number; name: string; materialType: string; fileUrl: string; uploadType: string; createTime: string; }

const route = useRoute();
const router = useRouter();
const order = ref<Order | null>(null);
const progressList = ref<Progress[]>([]);
const materialList = ref<Material[]>([]);
const uploadDialogVisible = ref(false);

const materialForm = reactive({ name: '', materialType: 'other', fileUrl: '', remark: '' });

const statusMap: Record<string, string> = {
  pending: '待付款', paid: '已付款', reviewing: '审核中', processing: '办理中',
  completed: '已完成', cancelled: '已取消', refunding: '退款中', refunded: '已退款'
};
const statusType: Record<string, string> = {
  pending: 'warning', paid: 'success', reviewing: 'info', processing: '',
  completed: 'success', cancelled: 'info', refunding: 'warning', refunded: 'info'
};
const payStatusMap: Record<string, string> = { unpaid: '未支付', paid: '已支付', refunded: '已退款' };
const materialTypeMap: Record<string, string> = { license: '营业执照', report: '年报截图', id_card: '身份证', other: '其他' };

const isImage = (url: string) => {
  if (!url) return false;
  const ext = url.split('.').pop()?.toLowerCase();
  return ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext || '');
};

const formatTime = (time: string) => {
  if (!time) return '-';
  return time.replace('T', ' ').substring(0, 19);
};

const fetchData = async () => {
  const id = Number(route.params.id);
  const [orderRes, progressRes, materialRes] = await Promise.all([
    getOrder(id), getOrderProgress(id), getOrderMaterials(id)
  ]);
  order.value = orderRes.data;
  progressList.value = progressRes.data;
  materialList.value = materialRes.data;
};

const handleUploadMaterial = async () => {
  if (!materialForm.name || !materialForm.fileUrl) {
    ElMessage.warning('请填写材料名称和文件URL');
    return;
  }
  await uploadOrderMaterial(Number(route.params.id), materialForm);
  ElMessage.success('上传成功');
  uploadDialogVisible.value = false;
  Object.assign(materialForm, { name: '', materialType: 'other', fileUrl: '', remark: '' });
  fetchData();
};

const handleDeleteMaterial = (row: Material) => {
  ElMessageBox.confirm('确定删除该材料?', '提示', { type: 'warning' }).then(async () => {
    await deleteOrderMaterial(row.id);
    ElMessage.success('删除成功');
    fetchData();
  });
};

onMounted(() => fetchData());
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.box-card { margin-bottom: 20px; }
</style>
