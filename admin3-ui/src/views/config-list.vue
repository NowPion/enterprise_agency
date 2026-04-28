<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-input v-model="query.keyword" placeholder="搜索配置键/描述" class="handle-input mr10" clearable
                  @keyup.enter="handleSearch"/>
        <el-select v-model="query.configType" placeholder="配置类型" class="handle-select mr10" clearable>
          <el-option label="字符串" value="string"/>
          <el-option label="数字" value="number"/>
          <el-option label="布尔" value="boolean"/>
          <el-option label="JSON" value="json"/>
        </el-select>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd" v-action:config:create>新增</el-button>
      </div>

      <el-table :data="filteredData" border class="table" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" width="80" align="center"/>
        <el-table-column prop="configKey" label="配置键" min-width="180">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.configKey }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="configValue" label="配置值" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="configType" label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.configType)" size="small">{{ row.configType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip/>
        <el-table-column prop="updateTime" label="更新时间" width="180"/>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text :icon="Edit" @click="handleEdit(row)" v-action:config:update>编辑</el-button>
            <el-button text :icon="Delete" class="red" @click="handleDelete(row)" v-action:config:delete>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" :disabled="isEdit" placeholder="如: app_name"/>
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" :type="form.configType === 'json' ? 'textarea' : 'text'"
                    :rows="form.configType === 'json' ? 4 : 1" placeholder="请输入配置值"/>
        </el-form-item>
        <el-form-item label="类型" prop="configType">
          <el-select v-model="form.configType" placeholder="请选择类型" style="width: 100%">
            <el-option label="字符串" value="string"/>
            <el-option label="数字" value="number"/>
            <el-option label="布尔" value="boolean"/>
            <el-option label="JSON" value="json"/>
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="配置项描述"/>
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
import {computed, onMounted, reactive, ref} from 'vue';
import {ElMessage, ElMessageBox, FormInstance, FormRules} from 'element-plus';
import {Delete, Edit, Plus, Search} from '@element-plus/icons-vue';
import {createConfig, deleteConfig, getConfigList, updateConfig} from '../api/config';

interface ConfigItem {
  id: number;
  configKey: string;
  configValue: string;
  configType: string;
  description: string;
  createTime: string;
  updateTime: string;
}

const tableData = ref<ConfigItem[]>([]);
const query = reactive({
  keyword: '',
  configType: ''
});

const filteredData = computed(() => {
  return tableData.value.filter(item => {
    const matchKeyword = !query.keyword ||
        item.configKey.toLowerCase().includes(query.keyword.toLowerCase()) ||
        (item.description && item.description.toLowerCase().includes(query.keyword.toLowerCase()));
    const matchType = !query.configType || item.configType === query.configType;
    return matchKeyword && matchType;
  });
});

const dialogVisible = ref(false);
const isEdit = ref(false);
const dialogTitle = computed(() => isEdit.value ? '编辑配置' : '新增配置');
const formRef = ref<FormInstance>();

const form = reactive({
  id: 0,
  configKey: '',
  configValue: '',
  configType: 'string',
  description: ''
});

const rules: FormRules = {
  configKey: [{required: true, message: '请输入配置键', trigger: 'blur'}],
  configValue: [{required: true, message: '请输入配置值', trigger: 'blur'}],
  configType: [{required: true, message: '请选择类型', trigger: 'change'}]
};

const getTypeTagType = (type: string) => {
  const map: Record<string, string> = {
    string: '',
    number: 'success',
    boolean: 'warning',
    json: 'danger'
  };
  return map[type] || '';
};

const fetchData = async () => {
  const res = await getConfigList();
  tableData.value = res.data;
};

const handleSearch = () => {
  // 前端过滤，无需请求
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(form, {
    id: 0,
    configKey: '',
    configValue: '',
    configType: 'string',
    description: ''
  });
  dialogVisible.value = true;
};

const handleEdit = (row: ConfigItem) => {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        await updateConfig(form.id, {
          configValue: form.configValue,
          configType: form.configType,
          description: form.description
        });
        ElMessage.success('修改成功');
      } else {
        await createConfig({
          configKey: form.configKey,
          configValue: form.configValue,
          configType: form.configType,
          description: form.description
        });
        ElMessage.success('新增成功');
      }
      dialogVisible.value = false;
      await fetchData();
    }
  });
};

const handleDelete = (row: ConfigItem) => {
  ElMessageBox.confirm(`确定要删除配置 "${row.configKey}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteConfig(row.id);
    ElMessage.success('删除成功');
    await fetchData();
  }).catch(() => {
  });
};

onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 250px;
}

.table {
  width: 100%;
  font-size: 14px;
}

.red {
  color: #F56C6C;
}

.mr10 {
  margin-right: 10px;
}
</style>
