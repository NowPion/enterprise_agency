<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增文章</el-button>
      <el-button :icon="Refresh" @click="fetchData">刷新</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="articleType" label="类型" width="100">
        <template #default="{ row }"><el-tag>{{ typeMap[row.articleType] || row.articleType }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">{{ categoryMap[row.category] || row.category || '-' }}</template>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文章' : '新增文章'" width="900px" :destroy-on-close="true">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.cover" placeholder="封面图片URL" />
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容" required>
          <div ref="editorRef" class="editor-container"></div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8"><el-form-item label="类型">
            <el-select v-model="form.articleType" style="width: 100%">
              <el-option label="办理指南" value="guide" />
              <el-option label="政策说明" value="policy" />
              <el-option label="帮助文档" value="help" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="8"><el-form-item label="分类">
            <el-select v-model="form.category" style="width: 100%" clearable>
              <el-option label="年报" value="annual_report" />
              <el-option label="异常解除" value="abnormal" />
              <el-option label="注册" value="register" />
              <el-option label="注销" value="cancel" />
            </el-select>
          </el-form-item></el-col>
          <el-col :span="4"><el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item></el-col>
          <el-col :span="4"><el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus, Refresh } from '@element-plus/icons-vue';
import { getArticles, createArticle, updateArticle, deleteArticle } from '../api/cms';
import E from 'wangeditor';

const tableData = ref([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const currentItem = ref<any>(null);
const editorRef = ref<HTMLElement>();
let editor: any = null;

const typeMap: Record<string, string> = { guide: '办理指南', policy: '政策说明', help: '帮助文档' };
const categoryMap: Record<string, string> = { annual_report: '年报', abnormal: '异常解除', register: '注册', cancel: '注销' };

const form = ref({
  title: '', content: '', cover: '', summary: '', articleType: 'guide', category: '', sort: 0, status: 1
});

const initEditor = () => {
  nextTick(() => {
    if (editorRef.value) {
      editor = new E(editorRef.value);
      editor.config.height = 350;
      editor.config.placeholder = '请输入文章内容...';
      editor.config.menus = [
        'head', 'bold', 'fontSize', 'fontName', 'italic', 'underline', 'strikeThrough',
        'foreColor', 'backColor', 'link', 'list', 'justify', 'quote', 'image', 'table',
        'code', 'splitLine', 'undo', 'redo'
      ];
      editor.config.onchange = (html: string) => { form.value.content = html; };
      editor.create();
      if (form.value.content) {
        editor.txt.html(form.value.content);
      }
    }
  });
};

const destroyEditor = () => {
  if (editor) {
    editor.destroy();
    editor = null;
  }
};

watch(dialogVisible, (val) => {
  if (val) {
    initEditor();
  } else {
    destroyEditor();
  }
});

const fetchData = async () => {
  const res = await getArticles();
  tableData.value = res.data;
};

const handleAdd = () => {
  isEdit.value = false;
  form.value = { title: '', content: '', cover: '', summary: '', articleType: 'guide', category: '', sort: 0, status: 1 };
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEdit.value = true;
  currentItem.value = row;
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSave = async () => {
  if (!form.value.title) { ElMessage.warning('请输入标题'); return; }
  if (!form.value.content) { ElMessage.warning('请输入内容'); return; }
  
  if (isEdit.value) {
    await updateArticle(currentItem.value.id, form.value);
    ElMessage.success('更新成功');
  } else {
    await createArticle(form.value);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  fetchData();
};

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定删除该文章?', '提示', { type: 'warning' });
  await deleteArticle(row.id);
  ElMessage.success('删除成功');
  fetchData();
};

onMounted(fetchData);
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.table { width: 100%; }
.editor-container { border: 1px solid #dcdfe6; border-radius: 4px; width: 100%; }
.editor-container :deep(.w-e-toolbar) { border-bottom: 1px solid #dcdfe6; }
.editor-container :deep(.w-e-text-container) { min-height: 300px; }
</style>
