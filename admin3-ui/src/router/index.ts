import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import Home from '../views/home.vue';
import {useBasicStore} from "../store/basic";

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard',
  },
  {
    path: '/redirect/:path(.*)',
    component: () => import('../views/redirect.vue'),
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    children: [
      {
        path: '/dashboard',
        name: 'dashboard',
        meta: {
          title: '系统首页',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/dashboard.vue'),
      },
      {
        path: '/users',
        name: 'users',
        meta: {
          title: '用户列表',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/user-list.vue'),
      },
      {
        path: '/roles',
        name: 'roles',
        meta: {
          title: '角色管理',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/role-list.vue'),
      },
      {
        path: '/resources',
        name: 'resources',
        meta: {
          title: '权限资源',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/resource-list.vue'),
      },
      {
        path: '/logs',
        name: 'logs',
        meta: {
          title: '系统日志',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/log-list.vue'),
      },
      {
        path: '/storage',
        name: 'storage',
        meta: {
          title: '对象存储',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/storage-list.vue'),
      },
      {
        path: '/configs',
        name: 'configs',
        meta: {
          title: '系统配置',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/config-list.vue'),
      },
      {
        path: '/wechat-config',
        name: 'wechat-config',
        meta: {
          title: '微信配置',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/wechat-config.vue'),
      },
      {
        path: '/miniapp-users',
        name: 'miniapp-users',
        meta: {
          title: '小程序用户',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/miniapp-user-list.vue'),
      },
      {
        path: '/banners',
        name: 'banners',
        meta: {
          title: '轮播图管理',
        },
        component: () => import(/* webpackChunkName: "dashboard" */ '../views/banner-list.vue'),
      },
      // 业务管理
      {
        path: '/products',
        name: 'products',
        meta: {
          title: '产品管理',
        },
        component: () => import(/* webpackChunkName: "business" */ '../views/product-list.vue'),
      },
      {
        path: '/orders',
        name: 'orders',
        meta: {
          title: '订单管理',
        },
        component: () => import(/* webpackChunkName: "business" */ '../views/order-list.vue'),
      },
      {
        path: '/orders/:id',
        name: 'order-detail',
        meta: {
          title: '订单详情',
        },
        component: () => import(/* webpackChunkName: "business" */ '../views/order-detail.vue'),
      },
      {
        path: '/refunds',
        name: 'refunds',
        meta: {
          title: '退款管理',
        },
        component: () => import(/* webpackChunkName: "business" */ '../views/refund-list.vue'),
      },
      // 内容管理
      {
        path: '/notices',
        name: 'notices',
        meta: {
          title: '公告管理',
        },
        component: () => import(/* webpackChunkName: "cms" */ '../views/notice-list.vue'),
      },
      {
        path: '/faqs',
        name: 'faqs',
        meta: {
          title: '常见问题',
        },
        component: () => import(/* webpackChunkName: "cms" */ '../views/faq-list.vue'),
      },
      {
        path: '/articles',
        name: 'articles',
        meta: {
          title: '文章管理',
        },
        component: () => import(/* webpackChunkName: "cms" */ '../views/article-list.vue'),
      },
      // 财务管理
      {
        path: '/transactions',
        name: 'transactions',
        meta: {
          title: '交易记录',
        },
        component: () => import(/* webpackChunkName: "finance" */ '../views/transaction-list.vue'),
      },
      {
        path: '/invoices',
        name: 'invoices',
        meta: {
          title: '发票管理',
        },
        component: () => import(/* webpackChunkName: "finance" */ '../views/invoice-list.vue'),
      },
      // 营销管理
      {
        path: '/coupons',
        name: 'coupons',
        meta: {
          title: '优惠券管理',
        },
        component: () => import(/* webpackChunkName: "marketing" */ '../views/coupon-list.vue'),
      },
      // 协议设置
      {
        path: '/agreement-config',
        name: 'agreement-config',
        meta: {
          title: '协议设置',
        },
        component: () => import(/* webpackChunkName: "config" */ '../views/agreement-config.vue'),
      },
      // 反馈管理
      {
        path: '/feedbacks',
        name: 'feedbacks',
        meta: {
          title: '反馈管理',
        },
        component: () => import(/* webpackChunkName: "feedback" */ '../views/feedback-list.vue'),
      },
      {
        path: '/tabs',
        name: 'tabs',
        meta: {
          title: 'tab标签',
          permiss: '3',
        },
        component: () => import(/* webpackChunkName: "tabs" */ '../views/tabs.vue'),
      },
      {
        path: '/user',
        name: 'user',
        meta: {
          title: '个人中心',
        },
        component: () => import(/* webpackChunkName: "user" */ '../views/user.vue'),
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    meta: {
      title: '登录',
    },
    component: () => import(/* webpackChunkName: "login" */ '../views/login.vue'),
  },
  {
    path: '/403',
    name: '403',
    meta: {
      title: '没有权限',
    },
    component: () => import(/* webpackChunkName: "403" */ '../views/403.vue'),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  document.title = `${to.meta.title} | 后台管理系统`;
  const token = localStorage.getItem('token');
  const basicStore = useBasicStore();
  if (!token && to.path !== '/login') {
    next('/login');
  } else {
    if (token) {
      await basicStore.fetchUserinfo();
    }
    next();
  }
});

export default router;
