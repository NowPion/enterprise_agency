<template>
  <div>
    <div class="container">
      <div class="handle-box">
        <el-input v-model="query.keyword" placeholder="昵称/手机号" class="handle-input mr10" clearable @keyup.enter="fetchData"/>
        <el-select v-model="query.status" placeholder="状态" class="handle-select mr10" clearable>
          <el-option label="正常" :value="1"/>
          <el-option label="禁用" :value="0"/>
        </el-select>
        <el-button type="primary" :icon="Search" @click="fetchData">搜索</el-button>
      </div>

      <el-table :data="tableData" border class="table" header-cell-class-name="table-header">
        <el-table-column prop="id" label="ID" width="70" align="center"/>
        <el-table-column label="用户信息" min-width="180">
          <template #default="{ row }">
            <div class="user-info">
              <el-avatar :src="getFullUrl(row.avatar)" :size="40"/>
              <div class="user-detail">
                <div class="nickname">{{ row.nickname || '微信用户' }}</div>
                <div class="phone">{{ row.phone || '-' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="openid" label="OpenID" width="200" show-overflow-tooltip/>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.blocked === 1" type="danger" size="small">已拉黑</el-tag>
            <el-switch v-else :model-value="row.status === 1" @change="(val: boolean) => handleStatusChange(row, val)" size="small"/>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160"/>
        <el-table-column prop="createTime" label="注册时间" width="160"/>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link size="small" @click="handleViewOrders(row)">订单</el-button>
            <el-button v-if="row.blocked !== 1" type="danger" link size="small" @click="handleBlock(row)">拉黑</el-button>
            <el-button v-else type="success" link size="small" @click="handleUnblock(row)">解除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="total, prev, pager, next" :total="total"
                       :page-size="pageSize" v-model:current-page="currentPage" @current-change="handlePageChange"/>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑用户" v-model="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="form.nickname"/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone"/>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="正常" inactive-text="禁用"/>
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
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import { getMiniappUserList, updateMiniappUser, updateMiniappUserStatus, blockMiniappUser, unblockMiniappUser } from '../api/miniapp-user';
import { getFullUrl } from '../api/base';

interface User {
  id: number;
  openid: string;
  nickname: string;
  avatar: string;
  phone: string;
  status: number;
  blocked: number;
  blockReason: string;
  lastLoginTime: string;
  createTime: string;
}

const router = useRouter();
const tableData = ref<User[]>([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = 10;
const query = reactive({ keyword: '', status: null as number | null });

const dialogVisible = ref(false);
const form = reactive({ id: 0, nickname: '', phone: '', status: 1 });

const fetchData = async () => {
  const params: any = { page: currentPage.value - 1, size: pageSize };
  if (query.keyword) params.keyword = query.keyword;
  if (query.status !== null) params.status = query.status;
  const res = await getMiniappUserList(params);
  tableData.value = res.data.content;
  total.value = res.data.totalElements;
};

const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchData();
};

const handleStatusChange = async (row: User, val: boolean) => {
  await updateMiniappUserStatus(row.id, val ? 1 : 0);
  ElMessage.success(val ? '已启用' : '已禁用');
  fetchData();
};

const handleEdit = (row: User) => {
  Object.assign(form, { id: row.id, nickname: row.nickname, phone: row.phone, status: row.status });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  await updateMiniappUser(form.id, form);
  ElMessage.success('修改成功');
  dialogVisible.value = false;
  fetchData();
};

const handleViewOrders = (row: User) => {
  router.push({ path: '/orders', query: { userId: row.id } });
};

const handleBlock = async (row: User) => {
  const { value } = await ElMessageBox.prompt('请输入拉黑原因', '拉黑用户 - ' + (row.nickname || '微信用户'), {
    confirmButtonText: '确定拉黑',
    cancelButtonText: '取消',
    inputPlaceholder: '请输入拉黑原因',
    type: 'warning'
  });
  await blockMiniappUser(row.id, value || '违规操作');
  ElMessage.success('已拉黑该用户');
  fetchData();
};

const handleUnblock = async (row: User) => {
  await ElMessageBox.confirm('确定解除该用户的拉黑状态吗？', '解除拉黑', { type: 'warning' });
  await unblockMiniappUser(row.id);
  ElMessage.success('已解除拉黑');
  fetchData();
};

onMounted(() => fetchData());
</script>

<style scoped>
.handle-box { margin-bottom: 20px; }
.handle-input { width: 200px; }
.handle-select { width: 100px; }
.table { width: 100%; font-size: 14px; }
.mr10 { margin-right: 10px; }
.pagination { margin-top: 20px; text-align: right; }
.user-info { display: flex; align-items: center; gap: 10px; }
.user-detail { line-height: 1.4; }
.nickname { font-weight: 500; }
.phone { font-size: 12px; color: #909399; }
</style>
