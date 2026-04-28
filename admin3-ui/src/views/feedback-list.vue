<template>
  <div class="feedback-list">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-select v-model="searchStatus" placeholder="状态筛选" clearable @change="loadData">
        <el-option label="待处理" value="pending" />
        <el-option label="处理中" value="processing" />
        <el-option label="已解决" value="resolved" />
        <el-option label="已关闭" value="closed" />
      </el-select>
    </div>

    <!-- 列表 -->
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type)">{{ getTypeText(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
      <el-table-column prop="contact" label="联系方式" width="140" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
          <el-button type="success" link @click="handleReply(row)" v-if="row.status === 'pending'">回复</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      @size-change="loadData"
      @current-change="loadData"
    />

    <!-- 查看/回复弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="反馈类型">{{ getTypeText(currentItem.type) }}</el-descriptions-item>
        <el-descriptions-item label="反馈内容">{{ currentItem.content }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentItem.contact || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentItem.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ getStatusText(currentItem.status) }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" v-if="currentItem.reply">{{ currentItem.reply }}</el-descriptions-item>
        <el-descriptions-item label="回复时间" v-if="currentItem.replyTime">{{ currentItem.replyTime }}</el-descriptions-item>
      </el-descriptions>
      
      <div v-if="isReplyMode" style="margin-top: 20px;">
        <el-input v-model="replyContent" type="textarea" :rows="4" placeholder="请输入回复内容" />
      </div>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" v-if="isReplyMode" @click="submitReply" :loading="submitting">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getFeedbackList, replyFeedback, deleteFeedback } from '../api/feedback';

const list = ref<any[]>([]);
const loading = ref(false);
const page = ref(1);
const size = ref(10);
const total = ref(0);
const searchStatus = ref('');

const dialogVisible = ref(false);
const dialogTitle = ref('');
const currentItem = ref<any>({});
const isReplyMode = ref(false);
const replyContent = ref('');
const submitting = ref(false);

const typeMap: Record<string, string> = {
  '功能建议': 'suggestion',
  '问题反馈': 'question',
  '投诉': 'complaint',
  '其他': 'other'
};

const getTypeText = (type: string) => {
  const map: Record<string, string> = { suggestion: '功能建议', question: '问题反馈', complaint: '投诉', other: '其他' };
  return map[type] || type;
};

const getTypeTag = (type: string) => {
  const map: Record<string, string> = { suggestion: 'success', question: 'warning', complaint: 'danger', other: 'info' };
  return map[type] || 'info';
};

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待处理', processing: '处理中', resolved: '已解决', closed: '已关闭' };
  return map[status] || status;
};

const getStatusTag = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', processing: 'primary', resolved: 'success', closed: 'info' };
  return map[status] || 'info';
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getFeedbackList({ status: searchStatus.value || undefined, page: page.value, size: size.value });
    list.value = res.data.content || [];
    total.value = res.data.totalElements || 0;
  } catch (e) {
    console.error(e);
  } finally {
    loading.value = false;
  }
};

const handleView = (row: any) => {
  currentItem.value = row;
  dialogTitle.value = '反馈详情';
  isReplyMode.value = false;
  dialogVisible.value = true;
};

const handleReply = (row: any) => {
  currentItem.value = row;
  dialogTitle.value = '回复反馈';
  isReplyMode.value = true;
  replyContent.value = '';
  dialogVisible.value = true;
};

const submitReply = async () => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容');
    return;
  }
  submitting.value = true;
  try {
    await replyFeedback(currentItem.value.id, replyContent.value);
    ElMessage.success('回复成功');
    dialogVisible.value = false;
    loadData();
  } catch (e) {
    ElMessage.error('回复失败');
  } finally {
    submitting.value = false;
  }
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这条反馈吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteFeedback(row.id);
      ElMessage.success('删除成功');
      loadData();
    })
    .catch(() => {});
};

onMounted(() => {
  loadData();
});
</script>

<style scoped>
.feedback-list {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}
.search-bar {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
