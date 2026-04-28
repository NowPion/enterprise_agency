<template>
  <div class="agreement-config">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="用户协议" name="agreement">
        <div class="editor-container">
          <div class="toolbar-container" ref="agreementToolbar"></div>
          <div class="editor-content" ref="agreementEditor"></div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="隐私政策" name="privacy">
        <div class="editor-container">
          <div class="toolbar-container" ref="privacyToolbar"></div>
          <div class="editor-content" ref="privacyEditor"></div>
        </div>
      </el-tab-pane>
    </el-tabs>
    
    <div class="action-bar">
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      <el-button @click="handlePreview">预览</el-button>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="previewTitle" width="700px">
      <div class="preview-content" v-html="previewContent"></div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { updateConfigByKey, getConfigByPrefix } from '../api/config';
import '@wangeditor/editor/dist/css/style.css';
import { createEditor, createToolbar, IEditorConfig, IToolbarConfig } from '@wangeditor/editor';

const activeTab = ref('agreement');
const agreementContent = ref('');
const privacyContent = ref('');
const saving = ref(false);
const previewVisible = ref(false);
const previewTitle = ref('');
const previewContent = ref('');

const agreementToolbar = ref<HTMLElement>();
const agreementEditor = ref<HTMLElement>();
const privacyToolbar = ref<HTMLElement>();
const privacyEditor = ref<HTMLElement>();

let agreementEditorInstance: any = null;
let privacyEditorInstance: any = null;

const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入内容...',
  MENU_CONF: {}
};

const toolbarConfig: Partial<IToolbarConfig> = {
  excludeKeys: ['uploadVideo', 'insertVideo', 'group-video']
};

const initEditors = () => {
  nextTick(() => {
    // 初始化用户协议编辑器
    if (agreementEditor.value && !agreementEditorInstance) {
      agreementEditorInstance = createEditor({
        selector: agreementEditor.value,
        html: agreementContent.value,
        config: {
          ...editorConfig,
          onChange(editor) {
            agreementContent.value = editor.getHtml();
          }
        },
        mode: 'default'
      });
      if (agreementToolbar.value) {
        createToolbar({
          editor: agreementEditorInstance,
          selector: agreementToolbar.value,
          config: toolbarConfig,
          mode: 'default'
        });
      }
    }

    // 初始化隐私政策编辑器
    if (privacyEditor.value && !privacyEditorInstance) {
      privacyEditorInstance = createEditor({
        selector: privacyEditor.value,
        html: privacyContent.value,
        config: {
          ...editorConfig,
          onChange(editor) {
            privacyContent.value = editor.getHtml();
          }
        },
        mode: 'default'
      });
      if (privacyToolbar.value) {
        createToolbar({
          editor: privacyEditorInstance,
          selector: privacyToolbar.value,
          config: toolbarConfig,
          mode: 'default'
        });
      }
    }
  });
};

const loadConfig = async () => {
  try {
    const res = await getConfigByPrefix('app.');
    if (res.data) {
      res.data.forEach((item: any) => {
        if (item.configKey === 'app.user_agreement') {
          agreementContent.value = item.configValue || '';
        } else if (item.configKey === 'app.privacy_policy') {
          privacyContent.value = item.configValue || '';
        }
      });
    }
    // 加载完数据后初始化编辑器
    initEditors();
  } catch (e) {
    console.error('加载配置失败', e);
    initEditors();
  }
};

const handleSave = async () => {
  saving.value = true;
  try {
    await updateConfigByKey('app.user_agreement', agreementContent.value);
    await updateConfigByKey('app.privacy_policy', privacyContent.value);
    ElMessage.success('保存成功');
  } catch (e) {
    ElMessage.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const handlePreview = () => {
  if (activeTab.value === 'agreement') {
    previewTitle.value = '用户协议预览';
    previewContent.value = agreementContent.value;
  } else {
    previewTitle.value = '隐私政策预览';
    previewContent.value = privacyContent.value;
  }
  previewVisible.value = true;
};

onMounted(() => {
  loadConfig();
});

onBeforeUnmount(() => {
  if (agreementEditorInstance) {
    agreementEditorInstance.destroy();
  }
  if (privacyEditorInstance) {
    privacyEditorInstance.destroy();
  }
});
</script>

<style scoped>
.agreement-config {
  padding: 20px;
  background: #fff;
  border-radius: 4px;
}
.editor-container {
  border: 1px solid #ccc;
  margin-top: 10px;
}
.toolbar-container {
  border-bottom: 1px solid #ccc;
}
.editor-content {
  height: 500px;
  overflow-y: auto;
}
.action-bar {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
.preview-content {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
  line-height: 1.8;
}
.preview-content :deep(p) {
  margin: 10px 0;
}
.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) {
  margin: 15px 0 10px;
}
</style>
