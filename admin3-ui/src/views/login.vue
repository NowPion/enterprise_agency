<template>
  <div class="login-wrap" :style="{ backgroundImage: `url(${loginBg})` }">
    <div class="login-overlay"></div>
    <div class="login-container">
      <div class="login-left">
        <div class="login-brand">
          <img class="brand-logo" src="/logo.png" alt="Logo" />
          <h1>企快办管理后台</h1>
          <p>专业企业代办服务平台</p>
        </div>
        <div class="login-features">
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>订单全流程管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>客户信息管理</span>
          </div>
          <div class="feature-item">
            <el-icon><Check /></el-icon>
            <span>业务数据统计分析</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-box">
          <div class="login-header">
            <h2>欢迎登录</h2>
            <p>管理后台系统</p>
          </div>
          <el-form :model="param" :rules="rules" ref="loginForm" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="param.username" 
                placeholder="请输入用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                type="password"
                placeholder="请输入密码"
                v-model="param.password"
                size="large"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="submitForm(loginForm)"
              />
            </el-form-item>
            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="submitForm(loginForm)">
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-footer">
            <p class="login-tips">
              <el-icon><InfoFilled /></el-icon>
              演示账号: admin / 123456
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useTagsStore } from '../store/tags';
import { useRouter } from 'vue-router';
import type { FormInstance, FormRules } from 'element-plus';
import { ElMessage } from 'element-plus';
import { Lock, User, Check, InfoFilled } from '@element-plus/icons-vue';
import { login as reqLogin } from '../api/login';
import { useBasicStore } from '../store/basic';
import loginBg from '../assets/img/login-bg.jpg';

interface LoginInfo {
  username: string;
  password: string;
}

const router = useRouter();
const param = reactive<LoginInfo>({
  username: '',
  password: ''
});
const rememberMe = ref(false);
const loading = ref(false);

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
};

const basicStore = useBasicStore();
const loginForm = ref<FormInstance>();

const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return;
  formEl.validate((valid: boolean) => {
    if (valid) {
      loading.value = true;
      reqLogin(param).then(res => {
        ElMessage.success('登录成功');
        localStorage.setItem('token', res.data.token);
        router.push('/');
        basicStore.setUserinfo(res.data);
      }).finally(() => {
        loading.value = false;
      });
    }
  });
};

const tags = useTagsStore();
tags.clearTags();
</script>

<style scoped>
.login-wrap {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;
}

.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
}

.login-container {
  display: flex;
  width: 900px;
  min-height: 520px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  z-index: 1;
}

.login-left {
  flex: 1;
  background: #fff;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #2d3748;
  border-right: 1px solid #e2e8f0;
}

.login-brand {
  text-align: center;
  margin-bottom: 40px;
}

.brand-logo {
  width: 88px;
  height: 88px;
  margin-bottom: 20px;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
}

.login-brand h1 {
  font-size: 26px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #2d3748;
}

.login-brand p {
  font-size: 15px;
  color: #718096;
}

.login-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  padding: 14px 20px;
  background: #f7fafc;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
  color: #4a5568;
}

.feature-item:hover {
  background: #edf2f7;
  transform: translateX(5px);
  border-color: #11998e;
}

.feature-item .el-icon {
  font-size: 18px;
  color: #fff;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  padding: 6px;
  border-radius: 50%;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: #fff;
}

.login-box {
  width: 100%;
  max-width: 320px;
}

.login-header {
  text-align: center;
  margin-bottom: 36px;
}

.login-header h2 {
  font-size: 30px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #718096;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 0 0 1px #e2e8f0 inset;
  background: #f7fafc;
  transition: all 0.3s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #11998e inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(17, 153, 142, 0.2), 0 0 0 1px #11998e inset;
  background: #fff;
}

.login-options {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 10px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(17, 153, 142, 0.4);
}

.login-footer {
  margin-top: 28px;
  text-align: center;
}

.login-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #718096;
  padding: 14px;
  background: #f7fafc;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
}

.login-tips .el-icon {
  color: #11998e;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    width: 90%;
    min-height: auto;
  }
  
  .login-left {
    padding: 30px 20px;
  }
  
  .login-features {
    display: none;
  }
}
</style>
