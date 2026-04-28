<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增轮播图</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <el-image :src="getFullUrl(row.image)" fit="cover" style="width: 160px; height: 80px; border-radius: 4px;"
            :preview-src-list="[getFullUrl(row.image)]" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="150" show-overflow-tooltip />
      <el-table-column label="链接类型" width="100">
        <template #default="{ row }">
          <el-tag size="small" :type="linkTypeTag(row.linkType)">{{ linkTypeText(row.linkType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="linkUrl" label="链接地址" width="200" show-overflow-tooltip />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="(val: boolean) => handleStatusChange(row, val)" size="small" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑轮播图' : '新增轮播图'" width="600px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="图片" required>
          <el-upload class="banner-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false"
            :on-success="handleUploadSuccess" :before-upload="beforeUpload" accept="image/*" name="files">
            <el-image v-if="form.image" :src="getFullUrl(form.image)" fit="cover" class="banner-image" />
            <el-icon v-else class="banner-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">建议尺寸: 750x300，支持 jpg/png 格式</div>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入标题（可选）" />
        </el-form-item>
        <el-form-item label="链接类型">
          <el-select v-model="form.linkType" style="width: 100%">
            <el-option label="无链接" :value="0" />
            <el-option label="页面" :value="1" />
            <el-option label="文章" :value="2" />
            <el-option label="外链" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="链接地址" v-if="form.linkType !== 0">
          <el-input v-model="form.linkUrl" placeholder="请输入链接地址" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getBannerList, createBanner, updateBanner, deleteBanner } from '../api/banner';
import { getFullUrl, BASE_URI } from '../api/base';

interface Banner {
  id: number; title: string; image: string; linkType: number; linkUrl: string; sort: number; status: number; createTime: string;
}

const tableData = ref<Banner[]>([]);
const dialogVisible = ref(false);
const isEdit = ref(false);
const form = reactive({ id: 0, title: '', image: '', linkType: 0, linkUrl: '', sort: 0, status: 1 });

const uploadUrl = computed(() => `${BASE_URI}/storage/upload`);
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('token')}` }));

const linkTypeText = (type: number) => ['无链接', '页面', '文章', '外链'][type] || '无链接';
const linkTypeTag = (type: number) => ['info', 'success', 'warning', 'danger'][type] || 'info';

const fetchData = async () => {
  const res = await getBannerList();
  tableData.value = res.data;
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(form, { id: 0, title: '', image: '', linkType: 0, linkUrl: '', sort: 0, status: 1 });
  dialogVisible.value = true;
};

const handleEdit = (row: Banner) => {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
};

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false; }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB'); return false; }
  return true;
};

const handleUploadSuccess = (res: any) => {
  const data = res.data || res;
  if (Array.isArray(data) && data.length > 0) {
    form.image = data[0].url;
    ElMessage.success('上传成功');
  }
};

const handleSubmit = async () => {
  if (!form.image) { ElMessage.warning('请上传图片'); return; }
  if (isEdit.value) {
    await updateBanner(form.id, form);
    ElMessage.success('修改成功');
  } else {
    await createBanner(form);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  fetchData();
};

const handleStatusChange = async (row: Banner, val: boolean) => {
  await updateBanner(row.id, { status: val ? 1 : 0 });
  ElMessage.success(val ? '已启用' : '已禁用');
  fetchData();
};

const handleDelete = (row: Banner) => {
  ElMessageBox.confirm('确定要删除该轮播图吗？', '提示', { type: 'warning' }).then(async () => {
    await deleteBanner(row.id);
    ElMessage.success('删除成功');
    fetchData();
  });
};

onMounted(() => fetchData());
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.table { width: 100%; font-size: 14px; }
.banner-uploader { width: 300px; }
.banner-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 300px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.banner-uploader :deep(.el-upload:hover) { border-color: #409eff; }
.banner-uploader-icon { font-size: 28px; color: #8c939d; }
.banner-image { width: 300px; height: 150px; }
.upload-tip { font-size: 12px; color: #909399; margin-top: 8px; }
</style>
