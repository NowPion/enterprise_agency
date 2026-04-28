<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增FAQ</el-button>
      <el-button :icon="Refresh" @click="fetchData">刷新</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="question" label="问题" />
      <el-table-column prop="category" label="分类" width="120">
        <template #default="{ row }"><el-tag>{{ categoryMap[row.category] || row.category }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑FAQ' : '新增FAQ'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="问题" required><el-input v-model="form.question" /></el-form-item>
        <el-form-item label="答案"><el-input v-model="form.answer" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%">
            <el-option label="通用" value="general" /><el-option label="年报" value="annual_report" />
            <el-option label="异常解除" value="abnormal" /><el-option label="注册" value="register" />
            <el-option label="注销" value="cancel" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Refresh } from '@element-plus/icons-vue';
import { getFaqs, createFaq, updateFaq, deleteFaq } from '../api/cms';
const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentItem = ref<any>(null);
const categoryMap: Record<string, string> = { general: '通用', annual_report: '年报', abnormal: '异常解除', register: '注册', cancel: '注销' };
const form = ref({ question: '', answer: '', category: 'general', sort: 0, status: 1 });
const fetchData = async () => { const res = await getFaqs(); tableData.value = res.data; };
const handleAdd = () => { isEdit.value = false; form.value = { question: '', answer: '', category: 'general', sort: 0, status: 1 }; dialogVisible.value = true; };
const handleEdit = (row: any) => { isEdit.value = true; currentItem.value = row; form.value = { ...row }; dialogVisible.value = true; };
const handleSave = async () => {
  if (isEdit.value) { await updateFaq(currentItem.value.id, form.value); ElMessage.success('更新成功'); }
  else { await createFaq(form.value); ElMessage.success('创建成功'); }
  dialogVisible.value = false; fetchData();
};
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除?', '提示'); await deleteFaq(row.id); ElMessage.success('删除成功'); fetchData(); };
onMounted(fetchData);
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.table { width: 100%; }
</style>
