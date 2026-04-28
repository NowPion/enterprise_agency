<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增公告</el-button>
      <el-button :icon="Refresh" @click="fetchData">刷新</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="noticeType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag>{{ typeMap[row.noticeType] || row.noticeType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isTop" label="置顶" width="80">
        <template #default="{ row }">{{ row.isTop ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170" />
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.noticeType" style="width: 100%">
            <el-option label="系统公告" value="system" />
            <el-option label="活动公告" value="activity" />
            <el-option label="政策公告" value="policy" />
          </el-select>
        </el-form-item>
        <el-form-item label="置顶"><el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Refresh } from '@element-plus/icons-vue';
import { getNotices, createNotice, updateNotice, deleteNotice } from '../api/cms';

const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentItem = ref<any>(null);
const typeMap: Record<string, string> = { system: '系统公告', activity: '活动公告', policy: '政策公告' };
const form = ref({ title: '', content: '', noticeType: 'system', isTop: 0, sort: 0, status: 1 });

const fetchData = async () => { const res = await getNotices(); tableData.value = res.data; };
const handleAdd = () => { isEdit.value = false; form.value = { title: '', content: '', noticeType: 'system', isTop: 0, sort: 0, status: 1 }; dialogVisible.value = true; };
const handleEdit = (row: any) => { isEdit.value = true; currentItem.value = row; form.value = { ...row }; dialogVisible.value = true; };
const handleSave = async () => {
  if (isEdit.value) { await updateNotice(currentItem.value.id, form.value); ElMessage.success('更新成功'); }
  else { await createNotice(form.value); ElMessage.success('创建成功'); }
  dialogVisible.value = false; fetchData();
};
const handleDelete = async (row: any) => { await ElMessageBox.confirm('确定删除?', '提示'); await deleteNotice(row.id); ElMessage.success('删除成功'); fetchData(); };
onMounted(fetchData);
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.table { width: 100%; }
</style>
