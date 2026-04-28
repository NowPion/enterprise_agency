<template>
  <div class="tags" v-if="tags.show">
    <div class="tags-scroll">
      <ul ref="tagsUl">
        <li
          class="tags-li"
          v-for="(item, index) in tags.list"
          :class="{ active: isActive(item.path) }"
          :key="index"
          @contextmenu.prevent="openContextMenu($event, index)"
        >
          <router-link :to="item.path" class="tags-li-title">
            {{ item.title }}
          </router-link>
          <el-icon class="tags-li-close" @click.prevent.stop="closeTags(index)" v-if="tags.list.length > 1">
            <Close />
          </el-icon>
        </li>
      </ul>
    </div>
    <div class="tags-close-box">
      <el-dropdown @command="handleTags" trigger="click">
        <el-button size="small" type="primary" plain>
          <el-icon><Operation /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="refresh">
              <el-icon><Refresh /></el-icon>刷新当前
            </el-dropdown-item>
            <el-dropdown-item command="current" :disabled="tags.list.length <= 1" divided>
              <el-icon><Close /></el-icon>关闭当前
            </el-dropdown-item>
            <el-dropdown-item command="other" :disabled="tags.list.length <= 1">
              <el-icon><CircleClose /></el-icon>关闭其他
            </el-dropdown-item>
            <el-dropdown-item command="all">
              <el-icon><FolderDelete /></el-icon>关闭所有
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 右键菜单 -->
    <div v-if="contextMenuVisible" class="context-menu" :style="{ left: contextMenuLeft + 'px', top: contextMenuTop + 'px' }">
      <div class="context-menu-item" @click="handleContextMenu('refresh')">
        <el-icon><Refresh /></el-icon>刷新
      </div>
      <div class="context-menu-item" @click="handleContextMenu('close')" v-if="tags.list.length > 1">
        <el-icon><Close /></el-icon>关闭
      </div>
      <div class="context-menu-item" @click="handleContextMenu('other')" v-if="tags.list.length > 1">
        <el-icon><CircleClose /></el-icon>关闭其他
      </div>
      <div class="context-menu-item" @click="handleContextMenu('left')" v-if="contextMenuIndex > 0">
        <el-icon><DArrowLeft /></el-icon>关闭左侧
      </div>
      <div class="context-menu-item" @click="handleContextMenu('right')" v-if="contextMenuIndex < tags.list.length - 1">
        <el-icon><DArrowRight /></el-icon>关闭右侧
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { useTagsStore } from '../store/tags';
import { onBeforeRouteUpdate, useRoute, useRouter } from 'vue-router';
import { Close, Refresh, CircleClose, FolderDelete, Operation, DArrowLeft, DArrowRight } from '@element-plus/icons-vue';

const route = useRoute();
const router = useRouter();
const tags = useTagsStore();

const contextMenuVisible = ref(false);
const contextMenuLeft = ref(0);
const contextMenuTop = ref(0);
const contextMenuIndex = ref(0);

const isActive = (path: string) => path === route.fullPath;

// 关闭单个标签
const closeTags = (index: number) => {
  const delItem = tags.list[index];
  tags.delTagsItem(index);
  const item = tags.list[index] ? tags.list[index] : tags.list[index - 1];
  if (item) {
    delItem.path === route.fullPath && router.push(item.path);
  } else {
    router.push('/');
  }
};

// 设置标签
const setTags = (route: any) => {
  const isExist = tags.list.some(item => item.path === route.fullPath);
  if (!isExist) {
    if (tags.list.length >= 12) tags.delTagsItem(0);
    tags.setTagsItem({
      name: route.name,
      title: route.meta.title,
      path: route.fullPath
    });
  }
};

setTags(route);
onBeforeRouteUpdate(to => setTags(to));

// 关闭全部标签
const closeAll = () => {
  tags.clearTags();
  router.push('/');
};

// 关闭其他标签
const closeOther = () => {
  const curItem = tags.list.filter(item => item.path === route.fullPath);
  tags.closeTagsOther(curItem);
};

// 关闭当前标签
const closeCurrent = () => {
  const index = tags.list.findIndex(item => item.path === route.fullPath);
  if (index !== -1) closeTags(index);
};

// 刷新当前页面
const refreshCurrent = () => {
  router.replace({ path: '/redirect' + route.fullPath });
};

const handleTags = (command: string) => {
  switch (command) {
    case 'refresh': refreshCurrent(); break;
    case 'current': closeCurrent(); break;
    case 'other': closeOther(); break;
    case 'all': closeAll(); break;
  }
};

// 右键菜单
const openContextMenu = (e: MouseEvent, index: number) => {
  contextMenuIndex.value = index;
  contextMenuLeft.value = e.clientX;
  contextMenuTop.value = e.clientY;
  contextMenuVisible.value = true;
};

const closeContextMenu = () => {
  contextMenuVisible.value = false;
};

const handleContextMenu = (action: string) => {
  const index = contextMenuIndex.value;
  switch (action) {
    case 'refresh':
      if (tags.list[index].path === route.fullPath) {
        refreshCurrent();
      } else {
        router.push(tags.list[index].path);
      }
      break;
    case 'close':
      closeTags(index);
      break;
    case 'other':
      tags.closeTagsOther([tags.list[index]]);
      router.push(tags.list[0].path);
      break;
    case 'left':
      tags.list.splice(0, index);
      break;
    case 'right':
      tags.list.splice(index + 1);
      break;
  }
  closeContextMenu();
};

onMounted(() => {
  document.addEventListener('click', closeContextMenu);
});

onUnmounted(() => {
  document.removeEventListener('click', closeContextMenu);
});
</script>

<style scoped>
.tags {
  position: relative;
  height: 40px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
}

.tags-scroll {
  flex: 1;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  padding: 0 10px;
}

.tags-scroll::-webkit-scrollbar {
  height: 4px;
}

.tags-scroll::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.tags ul {
  display: flex;
  gap: 6px;
  padding: 4px 0;
}

.tags-li {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 14px;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  color: #606266;
  transition: all 0.2s;
}

.tags-li:hover {
  background: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.tags-li.active {
  background: #409eff;
  border-color: #409eff;
  color: #fff;
}

.tags-li-title {
  max-width: 120px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  text-decoration: none;
  color: inherit;
}

.tags-li-close {
  margin-left: 6px;
  font-size: 12px;
  border-radius: 50%;
  padding: 2px;
  transition: all 0.2s;
}

.tags-li-close:hover {
  background: rgba(0, 0, 0, 0.1);
}

.tags-li.active .tags-li-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

.tags-close-box {
  padding: 0 10px;
  border-left: 1px solid #e6e6e6;
  height: 100%;
  display: flex;
  align-items: center;
}

/* 右键菜单 */
.context-menu {
  position: fixed;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 6px 0;
  z-index: 3000;
  min-width: 120px;
}

.context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
}

.context-menu-item:hover {
  background: #ecf5ff;
  color: #409eff;
}

.context-menu-item .el-icon {
  font-size: 14px;
}
</style>
