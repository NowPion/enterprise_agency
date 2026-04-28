<template>
  <div class="coupon-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>优惠券管理</span>
          <el-button type="primary" @click="handleAdd">新增优惠券</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="优惠券名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'fixed' ? 'success' : 'warning'">
              {{ row.type === 'fixed' ? '满减券' : '折扣券' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="120">
          <template #default="{ row }">
            <span v-if="row.type === 'fixed'">减{{ row.value }}元</span>
            <span v-else>{{ (row.value * 10).toFixed(1) }}折</span>
          </template>
        </el-table-column>
        <el-table-column label="使用门槛" width="120">
          <template #default="{ row }">
            {{ row.minAmount > 0 ? `满${row.minAmount}元` : '无门槛' }}
          </template>
        </el-table-column>
        <el-table-column label="发放/领取" width="100">
          <template #default="{ row }">
            {{ row.receivedCount }}/{{ row.totalCount === -1 ? '不限' : row.totalCount }}
          </template>
        </el-table-column>
        <el-table-column label="有效期" min-width="180">
          <template #default="{ row }">
            <div v-if="row.validType === 'fixed'">
              {{ row.startTime }} ~ {{ row.endTime }}
            </div>
            <div v-else>领取后{{ row.validDays }}天有效</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" 
              @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="fixed">满减券</el-radio>
            <el-radio label="percent">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type === 'fixed'" label="优惠金额" prop="value">
          <el-input-number v-model="form.value" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元</span>
        </el-form-item>
        <el-form-item v-else label="折扣比例" prop="value">
          <el-input-number v-model="form.value" :min="0.1" :max="0.99" :step="0.1" :precision="2" />
          <span style="margin-left: 8px;">（如0.9表示9折）</span>
        </el-form-item>
        <el-form-item label="使用门槛" prop="minAmount">
          <el-input-number v-model="form.minAmount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元（0表示无门槛）</span>
        </el-form-item>
        <el-form-item v-if="form.type === 'percent'" label="最大优惠">
          <el-input-number v-model="form.maxDiscount" :min="0" :precision="2" />
          <span style="margin-left: 8px;">元（可选）</span>
        </el-form-item>
        <el-form-item label="发放总量" prop="totalCount">
          <el-input-number v-model="form.totalCount" :min="-1" />
          <span style="margin-left: 8px;">（-1表示不限量）</span>
        </el-form-item>
        <el-form-item label="每人限领" prop="perLimit">
          <el-input-number v-model="form.perLimit" :min="1" />
          <span style="margin-left: 8px;">张</span>
        </el-form-item>
        <el-form-item label="有效期类型" prop="validType">
          <el-radio-group v-model="form.validType">
            <el-radio label="fixed">固定时间</el-radio>
            <el-radio label="days">领取后N天</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.validType === 'fixed'" label="有效期" prop="dateRange">
          <el-date-picker v-model="form.dateRange" type="datetimerange" 
            start-placeholder="开始时间" end-placeholder="结束时间" 
            value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item v-else label="有效天数" prop="validDays">
          <el-input-number v-model="form.validDays" :min="1" />
          <span style="margin-left: 8px;">天</span>
        </el-form-item>
        <el-form-item label="使用说明">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入使用说明" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCouponList, createCoupon, updateCoupon, updateCouponStatus, deleteCoupon } from '../api/coupon'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()

const queryForm = reactive({
  keyword: '',
  status: undefined as number | undefined,
  page: 1,
  size: 10
})

const form = reactive({
  id: undefined as number | undefined,
  name: '',
  type: 'fixed',
  value: 10,
  minAmount: 0,
  maxDiscount: undefined as number | undefined,
  totalCount: -1,
  perLimit: 1,
  validType: 'fixed',
  validDays: 7,
  dateRange: [] as string[],
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择优惠类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入优惠金额/折扣', trigger: 'blur' }],
  validType: [{ required: true, message: '请选择有效期类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCouponList(queryForm)
    tableData.value = res.data.content || []
    total.value = res.data.totalElements || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryForm.page = 1
  loadData()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.status = undefined
  queryForm.page = 1
  loadData()
}

const resetForm = () => {
  form.id = undefined
  form.name = ''
  form.type = 'fixed'
  form.value = 10
  form.minAmount = 0
  form.maxDiscount = undefined
  form.totalCount = -1
  form.perLimit = 1
  form.validType = 'fixed'
  form.validDays = 7
  form.dateRange = []
  form.description = ''
  form.status = 1
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增优惠券'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  resetForm()
  Object.assign(form, row)
  if (row.startTime && row.endTime) {
    form.dateRange = [row.startTime, row.endTime]
  }
  dialogTitle.value = '编辑优惠券'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  
  const data: any = { ...form }
  if (form.validType === 'fixed' && form.dateRange?.length === 2) {
    data.startTime = form.dateRange[0]
    data.endTime = form.dateRange[1]
  }
  delete data.dateRange
  
  submitLoading.value = true
  try {
    if (form.id) {
      await updateCoupon(form.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCoupon(data)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row: any) => {
  try {
    await updateCouponStatus(row.id, row.status)
    ElMessage.success('状态更新成功')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该优惠券吗？', '提示', { type: 'warning' })
    .then(async () => {
      await deleteCoupon(row.id)
      ElMessage.success('删除成功')
      loadData()
    })
    .catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 16px;
}
</style>
