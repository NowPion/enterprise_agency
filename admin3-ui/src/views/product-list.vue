<template>
  <div class="container">
    <div class="handle-box">
      <el-button type="primary" @click="handleAdd">新增产品</el-button>
      <el-button @click="handleManageCategory">管理分类</el-button>
    </div>
    <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="产品名称" />
      <el-table-column prop="code" label="产品编码" width="150" />
      <el-table-column prop="category" label="分类" width="120">
        <template #default="{ row }">
          <el-tag>{{ getCategoryName(row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="processDays" label="办理天数" width="100" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" align="center">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleFields(row)">字段</el-button>
          <el-button type="primary" link @click="handleYearPrices(row)">年度价格</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑产品' : '新增产品'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="产品名称" required>
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="产品编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.category" style="width: 100%" placeholder="请选择分类">
            <el-option v-for="cat in categoryList" :key="cat.code" :label="cat.name" :value="cat.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="现价">
          <el-input-number v-model="form.price" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="办理天数">
          <el-input-number v-model="form.processDays" :min="1" />
        </el-form-item>
        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="办理须知">
          <el-input v-model="form.notice" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="所需材料">
          <el-input v-model="form.materials" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="700px">
      <el-button type="primary" size="small" @click="handleAddCategory" style="margin-bottom: 10px">新增分类</el-button>
      <el-table :data="categoryList" border size="small">
        <el-table-column prop="name" label="分类名称" />
        <el-table-column prop="code" label="分类编码" width="150" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEditCategory(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 分类编辑弹窗 -->
    <el-dialog v-model="categoryEditDialogVisible" :title="isCategoryEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="categoryForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="categoryForm.name" />
        </el-form-item>
        <el-form-item label="编码" required>
          <el-input v-model="categoryForm.code" :disabled="isCategoryEdit" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="categoryForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="categoryForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveCategory">保存</el-button>
      </template>
    </el-dialog>

    <!-- 字段管理弹窗 -->
    <el-dialog v-model="fieldDialogVisible" title="表单字段管理" width="800px">
      <el-button type="primary" size="small" @click="handleAddField" style="margin-bottom: 10px">新增字段</el-button>
      <el-table :data="fieldList" border size="small">
        <el-table-column prop="fieldName" label="字段名称" />
        <el-table-column prop="fieldKey" label="字段标识" />
        <el-table-column prop="fieldType" label="类型" width="100" />
        <el-table-column prop="isRequired" label="必填" width="60">
          <template #default="{ row }">{{ row.isRequired ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEditField(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteField(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 字段编辑弹窗 -->
    <el-dialog v-model="fieldEditDialogVisible" :title="isFieldEdit ? '编辑字段' : '新增字段'" width="500px">
      <el-form :model="fieldForm" label-width="100px">
        <el-form-item label="字段名称" required>
          <el-input v-model="fieldForm.fieldName" placeholder="如：企业名称" />
        </el-form-item>
        <el-form-item label="字段标识" required>
          <el-input v-model="fieldForm.fieldKey" :disabled="isFieldEdit" placeholder="如：companyName" />
        </el-form-item>
        <el-form-item label="字段类型">
          <el-select v-model="fieldForm.fieldType" style="width: 100%">
            <el-option label="文本" value="text" />
            <el-option label="数字" value="number" />
            <el-option label="日期" value="date" />
            <el-option label="文本域" value="textarea" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="fieldForm.isRequired" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="fieldForm.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="fieldEditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveField">保存</el-button>
      </template>
    </el-dialog>

    <!-- 年度价格管理弹窗 -->
    <el-dialog v-model="yearPriceDialogVisible" title="年度价格管理" width="700px">
      <el-button type="primary" size="small" @click="handleAddYearPrice" style="margin-bottom: 10px">新增年度价格</el-button>
      <el-table :data="yearPriceList" border size="small">
        <el-table-column prop="year" label="年度" width="120" />
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">{{ row.originalPrice ? '¥' + row.originalPrice : '-' }}</template>
        </el-table-column>
        <el-table-column prop="price" label="现价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEditYearPrice(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteYearPrice(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getProducts, createProduct, updateProduct, deleteProduct, getProductFields, createProductField, updateProductField, deleteProductField, getProductYearPrices, createProductYearPrice, updateProductYearPrice, deleteProductYearPrice, getProductCategories, createProductCategory, updateProductCategory, deleteProductCategory } from '../api/business';

const tableData = ref([]);
const dialogVisible = ref(false);
const fieldDialogVisible = ref(false);
const fieldEditDialogVisible = ref(false);
const yearPriceDialogVisible = ref(false);
const categoryDialogVisible = ref(false);
const categoryEditDialogVisible = ref(false);
const isEdit = ref(false);
const isFieldEdit = ref(false);
const isCategoryEdit = ref(false);
const currentProduct = ref<any>(null);
const currentField = ref<any>(null);
const currentCategory = ref<any>(null);
const fieldList = ref([]);
const yearPriceList = ref([]);
const categoryList = ref<any[]>([]);

const form = ref({
  name: '', code: '', category: '', description: '',
  originalPrice: 0, price: 0, processDays: 1,
  notice: '', materials: '', sort: 0, status: 1
});

const categoryForm = ref({
  name: '', code: '', sort: 0, status: 1
});

const fieldForm = ref({
  fieldName: '', fieldKey: '', fieldType: 'text', isRequired: 0, sort: 0
});

const getCategoryName = (code: string) => {
  const cat = categoryList.value.find(c => c.code === code);
  return cat ? cat.name : code;
};

const fetchCategories = async () => {
  const res = await getProductCategories();
  categoryList.value = res.data || [];
};

const fetchData = async () => {
  const res = await getProducts();
  tableData.value = res.data;
};

const handleAdd = () => {
  isEdit.value = false;
  form.value = { name: '', code: '', category: '', description: '', originalPrice: 0, price: 0, processDays: 1, notice: '', materials: '', sort: 0, status: 1 };
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEdit.value = true;
  currentProduct.value = row;
  form.value = { ...row };
  dialogVisible.value = true;
};

const handleSave = async () => {
  if (isEdit.value) {
    await updateProduct(currentProduct.value.id, form.value);
    ElMessage.success('更新成功');
  } else {
    await createProduct(form.value);
    ElMessage.success('创建成功');
  }
  dialogVisible.value = false;
  fetchData();
};

const handleDelete = async (row: any) => {
  await ElMessageBox.confirm('确定删除该产品?', '提示');
  await deleteProduct(row.id);
  ElMessage.success('删除成功');
  fetchData();
};

const handleFields = async (row: any) => {
  currentProduct.value = row;
  const res = await getProductFields(row.id);
  fieldList.value = res.data;
  fieldDialogVisible.value = true;
};

const handleAddField = () => {
  isFieldEdit.value = false;
  fieldForm.value = { fieldName: '', fieldKey: '', fieldType: 'text', isRequired: 0, sort: 0 };
  fieldEditDialogVisible.value = true;
};

const handleEditField = (row: any) => {
  isFieldEdit.value = true;
  currentField.value = row;
  fieldForm.value = { ...row };
  fieldEditDialogVisible.value = true;
};

const handleSaveField = async () => {
  if (!fieldForm.value.fieldName || !fieldForm.value.fieldKey) {
    ElMessage.warning('请填写字段名称和字段标识');
    return;
  }
  if (isFieldEdit.value) {
    await updateProductField(currentField.value.id, fieldForm.value);
    ElMessage.success('更新成功');
  } else {
    await createProductField(currentProduct.value.id, fieldForm.value);
    ElMessage.success('创建成功');
  }
  fieldEditDialogVisible.value = false;
  const res = await getProductFields(currentProduct.value.id);
  fieldList.value = res.data;
};

const handleDeleteField = async (row: any) => {
  await ElMessageBox.confirm('确定删除该字段?', '提示');
  await deleteProductField(row.id);
  ElMessage.success('删除成功');
  const res = await getProductFields(currentProduct.value.id);
  fieldList.value = res.data;
};

// 年度价格管理
const handleYearPrices = async (row: any) => {
  currentProduct.value = row;
  const res = await getProductYearPrices(row.id);
  yearPriceList.value = res.data;
  yearPriceDialogVisible.value = true;
};

const handleAddYearPrice = async () => {
  const year = await ElMessageBox.prompt('请输入年度(如: 1年、2年、3年)', '新增年度价格');
  const price = await ElMessageBox.prompt('请输入价格', '新增年度价格');
  await createProductYearPrice(currentProduct.value.id, { year: year.value, price: parseFloat(price.value), sort: 0 });
  ElMessage.success('创建成功');
  const res = await getProductYearPrices(currentProduct.value.id);
  yearPriceList.value = res.data;
};

const handleEditYearPrice = async (row: any) => {
  const price = await ElMessageBox.prompt('请输入价格', '编辑年度价格', { inputValue: String(row.price) });
  await updateProductYearPrice(row.id, { price: parseFloat(price.value) });
  ElMessage.success('更新成功');
  const res = await getProductYearPrices(currentProduct.value.id);
  yearPriceList.value = res.data;
};

const handleDeleteYearPrice = async (row: any) => {
  await ElMessageBox.confirm('确定删除该年度价格?', '提示');
  await deleteProductYearPrice(row.id);
  ElMessage.success('删除成功');
  const res = await getProductYearPrices(currentProduct.value.id);
  yearPriceList.value = res.data;
};

// 分类管理
const handleManageCategory = () => {
  categoryDialogVisible.value = true;
};

const handleAddCategory = () => {
  isCategoryEdit.value = false;
  categoryForm.value = { name: '', code: '', sort: 0, status: 1 };
  categoryEditDialogVisible.value = true;
};

const handleEditCategory = (row: any) => {
  isCategoryEdit.value = true;
  currentCategory.value = row;
  categoryForm.value = { ...row };
  categoryEditDialogVisible.value = true;
};

const handleSaveCategory = async () => {
  if (isCategoryEdit.value) {
    await updateProductCategory(currentCategory.value.id, categoryForm.value);
    ElMessage.success('更新成功');
  } else {
    await createProductCategory(categoryForm.value);
    ElMessage.success('创建成功');
  }
  categoryEditDialogVisible.value = false;
  await fetchCategories();
};

const handleDeleteCategory = async (row: any) => {
  await ElMessageBox.confirm('确定删除该分类?', '提示');
  await deleteProductCategory(row.id);
  ElMessage.success('删除成功');
  await fetchCategories();
};

onMounted(() => {
  fetchCategories();
  fetchData();
});
</script>

<style scoped>
.handle-box {
  margin-bottom: 15px;
}
</style>
