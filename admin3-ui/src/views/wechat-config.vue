<template>
  <div class="wechat-config-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <el-icon class="header-icon"><ChatDotRound /></el-icon>
      <div class="header-text">
        <h2>微信配置管理</h2>
        <p>配置微信小程序和微信支付相关参数</p>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 左侧配置区域 -->
      <el-col :span="16">
        <!-- 小程序配置 -->
        <el-card class="config-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="card-icon miniapp"><Cellphone /></el-icon>
                <span>小程序配置</span>
              </div>
              <el-tag type="success" size="small" v-if="miniappForm.appId">已配置</el-tag>
              <el-tag type="info" size="small" v-else>未配置</el-tag>
            </div>
          </template>
          <el-form :model="miniappForm" label-width="140px" label-position="right">
            <el-form-item label="小程序 AppID">
              <el-input v-model="miniappForm.appId" placeholder="请输入小程序AppID" clearable>
                <template #prefix><el-icon><Key /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="小程序 AppSecret">
              <el-input v-model="miniappForm.appSecret" type="password" show-password placeholder="请输入小程序AppSecret" clearable>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveMiniappConfig" :loading="saving" v-action:config:update>
                <el-icon><Check /></el-icon>保存小程序配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 支付配置 -->
        <el-card class="config-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="card-icon pay"><Money /></el-icon>
                <span>微信支付配置</span>
              </div>
              <el-tag type="success" size="small" v-if="payForm.mchId">已配置</el-tag>
              <el-tag type="info" size="small" v-else>未配置</el-tag>
            </div>
          </template>
          <el-form :model="payForm" label-width="140px" label-position="right">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="商户号">
                  <el-input v-model="payForm.mchId" placeholder="请输入商户号" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="证书序列号">
                  <el-input v-model="payForm.certSerialNo" placeholder="请输入商户证书序列号" clearable />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="API密钥(V2)">
                  <el-input v-model="payForm.mchKey" type="password" show-password placeholder="请输入商户API密钥" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="APIv3密钥">
                  <el-input v-model="payForm.apiV3Key" type="password" show-password placeholder="请输入APIv3密钥" clearable />
                </el-form-item>
              </el-col>
            </el-row>
            <el-divider content-position="left">
              <el-icon><Document /></el-icon> 证书配置
            </el-divider>
            <el-form-item label="商户私钥">
              <el-input v-model="payForm.privateKey" type="textarea" :rows="5" placeholder="请粘贴商户私钥内容(PEM格式)&#10;-----BEGIN PRIVATE KEY-----&#10;...&#10;-----END PRIVATE KEY-----" />
            </el-form-item>
            <el-form-item label="商户证书">
              <el-input v-model="payForm.privateCert" type="textarea" :rows="5" placeholder="请粘贴商户证书内容(PEM格式)&#10;-----BEGIN CERTIFICATE-----&#10;...&#10;-----END CERTIFICATE-----" />
            </el-form-item>
            <el-divider content-position="left">
              <el-icon><Link /></el-icon> 回调地址
            </el-divider>
            <el-form-item label="支付回调地址">
              <el-input v-model="payForm.notifyUrl" placeholder="https://your-domain.com/api/pay/notify" clearable>
                <template #prefix><el-icon><Link /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="退款回调地址">
              <el-input v-model="payForm.refundNotifyUrl" placeholder="https://your-domain.com/api/pay/refund/notify" clearable>
                <template #prefix><el-icon><Link /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="savePayConfig" :loading="saving" v-action:config:update>
                <el-icon><Check /></el-icon>保存支付配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧说明区域 -->
      <el-col :span="8">
        <el-card class="help-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-icon class="card-icon help"><QuestionFilled /></el-icon>
                <span>配置说明</span>
              </div>
            </div>
          </template>
          <div class="help-content">
            <div class="help-section">
              <h4><el-icon><Cellphone /></el-icon> 小程序配置</h4>
              <ol>
                <li>登录 <a href="https://mp.weixin.qq.com" target="_blank">微信公众平台</a></li>
                <li>进入「开发」-「开发管理」</li>
                <li>在「开发设置」中获取 AppID</li>
                <li>生成并复制 AppSecret</li>
              </ol>
            </div>
            <el-divider />
            <div class="help-section">
              <h4><el-icon><Money /></el-icon> 支付配置</h4>
              <ol>
                <li>登录 <a href="https://pay.weixin.qq.com" target="_blank">微信商户平台</a></li>
                <li>在「账户中心」获取商户号</li>
                <li>在「API安全」中设置密钥</li>
                <li>申请并下载 API 证书</li>
              </ol>
            </div>
            <el-divider />
            <div class="help-section">
              <h4><el-icon><Warning /></el-icon> 注意事项</h4>
              <ul>
                <li>AppSecret 和密钥请妥善保管</li>
                <li>证书内容需包含完整的 PEM 格式头尾</li>
                <li>回调地址需要配置 HTTPS</li>
                <li>修改配置后需重启服务生效</li>
              </ul>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getConfigList, updateConfigByKey } from '../api/config'
import { ChatDotRound, Cellphone, Money, Key, Lock, Check, Document, Link, QuestionFilled, Warning } from '@element-plus/icons-vue'

const saving = ref(false)

const miniappForm = ref({
  appId: '',
  appSecret: ''
})

const payForm = ref({
  mchId: '',
  mchKey: '',
  apiV3Key: '',
  certSerialNo: '',
  privateKey: '',
  privateCert: '',
  notifyUrl: '',
  refundNotifyUrl: ''
})

const loadConfig = async () => {
  const res = await getConfigList()
  const configs = res.data as any[]
  
  const configMap: Record<string, string> = {}
  configs.forEach((c: any) => {
    configMap[c.configKey] = c.configValue
  })

  miniappForm.value = {
    appId: configMap['wechat.miniapp.appId'] || '',
    appSecret: configMap['wechat.miniapp.appSecret'] || ''
  }

  payForm.value = {
    mchId: configMap['wechat.pay.mchId'] || '',
    mchKey: configMap['wechat.pay.mchKey'] || '',
    apiV3Key: configMap['wechat.pay.apiV3Key'] || '',
    certSerialNo: configMap['wechat.pay.certSerialNo'] || '',
    privateKey: configMap['wechat.pay.privateKey'] || '',
    privateCert: configMap['wechat.pay.privateCert'] || '',
    notifyUrl: configMap['wechat.pay.notifyUrl'] || '',
    refundNotifyUrl: configMap['wechat.pay.refundNotifyUrl'] || ''
  }
}

const saveMiniappConfig = async () => {
  saving.value = true
  try {
    await updateConfigByKey('wechat.miniapp.appId', miniappForm.value.appId)
    await updateConfigByKey('wechat.miniapp.appSecret', miniappForm.value.appSecret)
    ElMessage.success('小程序配置保存成功')
  } finally {
    saving.value = false
  }
}

const savePayConfig = async () => {
  saving.value = true
  try {
    await updateConfigByKey('wechat.pay.mchId', payForm.value.mchId)
    await updateConfigByKey('wechat.pay.mchKey', payForm.value.mchKey)
    await updateConfigByKey('wechat.pay.apiV3Key', payForm.value.apiV3Key)
    await updateConfigByKey('wechat.pay.certSerialNo', payForm.value.certSerialNo)
    await updateConfigByKey('wechat.pay.privateKey', payForm.value.privateKey)
    await updateConfigByKey('wechat.pay.privateCert', payForm.value.privateCert)
    await updateConfigByKey('wechat.pay.notifyUrl', payForm.value.notifyUrl)
    await updateConfigByKey('wechat.pay.refundNotifyUrl', payForm.value.refundNotifyUrl)
    ElMessage.success('支付配置保存成功')
  } finally {
    saving.value = false
  }
}

onMounted(() => loadConfig())
</script>

<style scoped>
.wechat-config-container {
  padding: 20px;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #07c160 0%, #1aad19 100%);
  border-radius: 12px;
  color: #fff;
}

.header-icon {
  font-size: 48px;
  margin-right: 16px;
  opacity: 0.9;
}

.header-text h2 {
  margin: 0 0 4px 0;
  font-size: 22px;
  font-weight: 600;
}

.header-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
}

.config-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.config-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-icon {
  font-size: 22px;
  margin-right: 10px;
  padding: 6px;
  border-radius: 6px;
}

.card-icon.miniapp {
  background: #e8f5e9;
  color: #07c160;
}

.card-icon.pay {
  background: #fff3e0;
  color: #ff9800;
}

.card-icon.help {
  background: #e3f2fd;
  color: #2196f3;
}

.config-card :deep(.el-form-item) {
  margin-bottom: 18px;
}

.config-card :deep(.el-divider__text) {
  font-size: 13px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 6px;
}

.help-card {
  border-radius: 8px;
  position: sticky;
  top: 20px;
}

.help-content {
  font-size: 14px;
  color: #606266;
}

.help-section h4 {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 0 0 12px 0;
  font-size: 15px;
  color: #303133;
}

.help-section ol, .help-section ul {
  margin: 0;
  padding-left: 20px;
  line-height: 2;
}

.help-section a {
  color: #07c160;
  text-decoration: none;
}

.help-section a:hover {
  text-decoration: underline;
}

:deep(.el-divider) {
  margin: 16px 0;
}

:deep(.el-button .el-icon) {
  margin-right: 6px;
}
</style>
