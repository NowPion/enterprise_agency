<template>
	<view class="container">
		<!-- 未登录状态 -->
		<view class="login-section" v-if="!userInfo.isLogin">
			<view class="login-header">
				<image class="login-logo" src="/static/logo.png" mode="aspectFit"></image>
				<text class="login-title">企快办</text>
				<text class="login-desc">专业企业代办服务平台</text>
			</view>
			
			<view class="login-content">
				<button class="wx-login-btn" open-type="getPhoneNumber" @getphonenumber="onWxLogin">
					<text>微信一键登录</text>
				</button>
				
				<view class="login-tip-row">
					<text class="login-tip">登录即表示同意</text>
					<text class="login-link" @click="goAgreement">《用户协议》</text>
					<text class="login-tip">和</text>
					<text class="login-link" @click="goPrivacy">《隐私政策》</text>
				</view>
			</view>
		</view>
		
		<!-- 已登录状态 -->
		<view v-else>
			<!-- 顶部用户信息 -->
			<view class="user-header" @click="goProfile">
				<view class="avatar-wrapper">
					<image class="avatar" :src="avatarUrl" mode="aspectFill"></image>
				</view>
				<view class="user-info-row">
					<text class="username">{{ userInfo.nickname || '微信用户' }}</text>
					<text class="user-phone" v-if="userInfo.phone">{{ userInfo.phone }}</text>
				</view>
				<text class="edit-btn">编辑 ›</text>
			</view>
			
			<!-- 订单统计 -->
			<view class="order-stats">
				<view class="stat-item" @click="goOrders(0)">
					<text class="stat-num">{{ stats.pending || 0 }}</text>
					<text class="stat-label">待支付</text>
				</view>
				<view class="stat-item" @click="goOrders(2)">
					<text class="stat-num">{{ stats.processing || 0 }}</text>
					<text class="stat-label">办理中</text>
				</view>
				<view class="stat-item" @click="goOrders(3)">
					<text class="stat-num">{{ stats.completed || 0 }}</text>
					<text class="stat-label">已完成</text>
				</view>
				<view class="stat-item" @click="goOrders(null)">
					<text class="stat-num">{{ stats.total || 0 }}</text>
					<text class="stat-label">全部订单</text>
				</view>
			</view>
			
			<!-- 功能菜单 -->
			<view class="menu-section">
				<view class="menu-item" @click="goCoupons">
					<text class="menu-name">我的优惠券</text>
					<text class="menu-badge" v-if="couponCount">{{ couponCount }}</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goInvoices">
					<text class="menu-name">我的发票</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goProfile">
					<text class="menu-name">个人资料</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goAgreement">
					<text class="menu-name">用户协议</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goPrivacy">
					<text class="menu-name">隐私政策</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="goFeedback">
					<text class="menu-name">反馈问题/建议</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="callService">
					<text class="menu-name">联系客服</text>
					<text class="menu-value" v-if="servicePhone">{{ servicePhone }}</text>
					<text class="menu-arrow">›</text>
				</view>
				<view class="menu-item" @click="handleLogout">
					<text class="menu-name">退出登录</text>
					<text class="menu-arrow">›</text>
				</view>
			</view>
		</view>
		
		<view class="footer">
			<text>企快办 · 专业企业代办服务</text>
		</view>
	</view>
</template>

<script>
import { userApi, couponApi, commonApi } from '@/api/index.js'
import { get, setLoggingIn } from '@/utils/request.js'
import { getFullImageUrl } from '@/utils/image.js'

export default {
	data() {
		return {
			userInfo: {
				id: 0,
				openid: '',
				avatar: '',
				nickname: '微信用户',
				phone: '',
				isLogin: false
			},
			stats: {
				pending: 0,
				processing: 0,
				completed: 0,
				total: 0
			},
			couponCount: 0,
			justLoggedIn: false,
			servicePhone: ''
		}
	},
	computed: {
		avatarUrl() {
			return getFullImageUrl(this.userInfo.avatar, '/static/mr.png')
		}
	},
	onShow() {
		// 加载客服电话
		this.loadServicePhone()
		// 如果刚登录成功，跳过检查（避免重复请求）
		if (this.justLoggedIn) {
			this.justLoggedIn = false
			return
		}
		this.checkLoginStatus()
	},
	methods: {
		async onWxLogin(e) {
			if (e.detail.errMsg !== 'getPhoneNumber:ok') {
				uni.showToast({ title: '需要授权手机号才能登录', icon: 'none' })
				return
			}
			
			uni.showLoading({ title: '登录中...' })
			// 标记正在登录，防止401响应清除token
			setLoggingIn(true)
			
			try {
				const loginRes = await this.getWxLoginCode()
				const res = await userApi.wxLogin({
					code: loginRes.code,
					phoneCode: e.detail.code
				})
				
				if (res.code === 0) {
					this.handleLoginSuccess(res.data)
				} else {
					setLoggingIn(false)
					uni.hideLoading()
					uni.showToast({ title: res.msg || '登录失败', icon: 'none' })
				}
			} catch (e) {
				setLoggingIn(false)
				uni.hideLoading()
				console.error('登录失败', e)
				this.mockLogin()
			}
		},
		
		getWxLoginCode() {
			return new Promise((resolve, reject) => {
				uni.login({
					provider: 'weixin',
					success: resolve,
					fail: reject
				})
			})
		},
		
		async handleLoginSuccess(data) {
			// 标记刚登录成功，避免onShow重复检查
			this.justLoggedIn = true
			// 先保存token
			console.log('登录成功，保存token:', data.token ? data.token.substring(0, 30) + '...' : '无token')
			uni.setStorageSync('token', data.token)
			// 验证token是否保存成功
			const savedToken = uni.getStorageSync('token')
			console.log('验证保存的token:', savedToken ? savedToken.substring(0, 30) + '...' : '无token')
			
			const userInfo = {
				id: data.userInfo.id,
				openid: data.userInfo.openid,
				nickname: data.userInfo.nickname || '微信用户',
				avatar: data.userInfo.avatar || '',
				phone: data.userInfo.phone || '',
				isLogin: true
			}
			uni.setStorageSync('userInfo', userInfo)
			this.userInfo = userInfo
			uni.hideLoading()
			uni.showToast({ title: '登录成功', icon: 'success' })
			// 等待token确实保存后再加载数据，然后解除登录保护
			setTimeout(() => {
				const tokenBeforeRequest = uni.getStorageSync('token')
				console.log('请求前检查token:', tokenBeforeRequest ? tokenBeforeRequest.substring(0, 30) + '...' : '无token')
				setLoggingIn(false)
				this.loadStats()
				this.loadCouponCount()
			}, 1000)
		},
		
		mockLogin() {
			const userInfo = {
				id: 10001,
				openid: 'mock_openid_xxx',
				nickname: '微信用户',
				avatar: '',
				phone: '138****8888',
				isLogin: true
			}
			uni.setStorageSync('token', 'mock_token_xxx')
			uni.setStorageSync('userInfo', userInfo)
			this.userInfo = userInfo
			uni.showToast({ title: '登录成功', icon: 'success' })
		},
		
		async checkLoginStatus() {
			const token = uni.getStorageSync('token')
			const localUser = uni.getStorageSync('userInfo')
			
			if (token && localUser && localUser.isLogin) {
				// 有本地登录信息，直接使用并加载数据
				this.userInfo = localUser
				// 直接加载数据，如果token无效会在请求时处理
				this.loadStats()
				this.loadCouponCount()
				// 后台静默刷新用户信息
				this.loadUserInfo()
			} else {
				this.userInfo = { avatar: '', nickname: '微信用户', isLogin: false }
			}
		},
		
		async loadUserInfo() {
			try {
				const res = await userApi.getUserInfo()
				if (res.code === 0) {
					const userInfo = {
						id: res.data.id,
						openid: res.data.openid,
						nickname: res.data.nickname,
						avatar: res.data.avatar,
						phone: res.data.phone,
						isLogin: true
					}
					this.userInfo = userInfo
					uni.setStorageSync('userInfo', userInfo)
				}
			} catch (e) {
				console.error('获取用户信息失败', e)
			}
		},
		
		async loadStats() {
			// 检查是否已登录
			const token = uni.getStorageSync('token')
			if (!token) return
			try {
				const res = await get('/user/order-stats')
				if (res.code === 0) {
					this.stats = res.data || {}
				}
			} catch (e) {
				console.error('加载订单统计失败', e)
			}
		},
		
		async loadCouponCount() {
			// 检查是否已登录
			const token = uni.getStorageSync('token')
			if (!token) return
			try {
				const res = await couponApi.getMyCoupons({ page: 1, size: 1, status: 0 })
				this.couponCount = res.data?.total || 0
			} catch (e) {
				console.error('加载优惠券数量失败', e)
			}
		},
		
		handleLogout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await userApi.logout()
						} catch (e) {}
						this.clearLogin()
						uni.showToast({ title: '已退出登录', icon: 'none' })
					}
				}
			})
		},
		
		clearLogin() {
			uni.removeStorageSync('token')
			uni.removeStorageSync('userInfo')
			this.userInfo = { avatar: '', nickname: '微信用户', isLogin: false }
			this.stats = {}
			this.couponCount = 0
		},
		
		goOrders(status) {
			uni.switchTab({ url: '/pages/order/list' })
		},
		goCoupons() {
			uni.navigateTo({ url: '/pages/coupon/list' })
		},
		goInvoices() {
			uni.navigateTo({ url: '/pages/invoice/list' })
		},
		goProfile() {
			uni.navigateTo({ url: '/pages/mine/profile' })
		},
		goAgreement() {
			uni.navigateTo({ url: '/pages/mine/agreement' })
		},
		goPrivacy() {
			uni.navigateTo({ url: '/pages/mine/privacy' })
		},
		goFeedback() {
			uni.navigateTo({ url: '/pages/mine/feedback' })
		},
		async loadServicePhone() {
			try {
				const res = await commonApi.getConfig('service_phone')
				console.log("客服电话：",res)
				if (res.code === 0) {
					// 兼容 data 或 msg 字段
					this.servicePhone = res.data || res.msg || ''
				}
			} catch (e) {
				console.error('获取客服电话失败', e)
			}
		},
		callService() {
			if (!this.servicePhone) {
				uni.showToast({ title: '客服电话未配置', icon: 'none' })
				return
			}
			uni.showModal({
				title: '联系客服',
				content: `是否拨打客服电话 ${this.servicePhone}？`,
				confirmText: '拨打',
				success: (res) => {
					if (res.confirm) {
						uni.makePhoneCall({
							phoneNumber: this.servicePhone,
							fail: () => {
								uni.showToast({ title: '拨打失败', icon: 'none' })
							}
						})
					}
				}
			})
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

/* 登录区域 */
.login-section {
	min-height: 100vh;
	background: linear-gradient(180deg, #1976d2 0%, #1565c0 100%);
	display: flex;
	flex-direction: column;
}
.login-header {
	padding: 100rpx 60rpx 60rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}
.login-logo {
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 24rpx;
}
.login-title {
	font-size: 48rpx;
	font-weight: bold;
	color: #fff;
	margin-bottom: 16rpx;
}
.login-desc {
	font-size: 28rpx;
	color: rgba(255,255,255,0.8);
}
.login-content {
	flex: 1;
	background: #fff;
	border-radius: 40rpx 40rpx 0 0;
	padding: 80rpx 60rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}
.wx-login-btn {
	width: 100%;
	height: 96rpx;
	background: #07C160;
	border-radius: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 32rpx;
	border: none;
}
.wx-login-btn::after {
	border: none;
}
.login-tip-row {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: wrap;
	margin-top: 40rpx;
}
.login-tip {
	font-size: 24rpx;
	color: #999;
}
.login-link {
	font-size: 24rpx;
	color: #1976d2;
}

/* 用户头部 */
.user-header {
	background: linear-gradient(135deg, #1976d2, #1565c0);
	padding: 60rpx 30rpx;
	display: flex;
	align-items: center;
}
.avatar-wrapper {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	overflow: hidden;
	background: #fff;
	margin-right: 24rpx;
}
.avatar {
	width: 100%;
	height: 100%;
}
.user-info-row {
	flex: 1;
}
.username {
	color: #fff;
	font-size: 34rpx;
	font-weight: bold;
	display: block;
}
.user-phone {
	color: rgba(255,255,255,0.8);
	font-size: 26rpx;
	margin-top: 8rpx;
}
.edit-btn {
	color: rgba(255,255,255,0.9);
	font-size: 26rpx;
	padding: 8rpx 20rpx;
	border: 1rpx solid rgba(255,255,255,0.5);
	border-radius: 30rpx;
}

/* 订单统计 */
.order-stats {
	display: flex;
	background: #fff;
	margin: 20rpx;
	border-radius: 16rpx;
	padding: 30rpx 0;
}
.stat-item {
	flex: 1;
	display: flex;
	flex-direction: column;
	align-items: center;
}
.stat-num {
	font-size: 40rpx;
	font-weight: bold;
	color: #1976d2;
}
.stat-label {
	font-size: 24rpx;
	color: #666;
	margin-top: 8rpx;
}

/* 功能菜单 */
.menu-section {
	background: #fff;
	margin: 20rpx;
	border-radius: 16rpx;
}
.menu-item {
	display: flex;
	align-items: center;
	padding: 30rpx 24rpx;
	border-bottom: 1rpx solid #f5f5f5;
}
.menu-item:last-child {
	border-bottom: none;
}
.menu-icon {
	width: 44rpx;
	height: 44rpx;
	margin-right: 20rpx;
}
.menu-name {
	flex: 1;
	font-size: 30rpx;
	color: #333;
}
.menu-badge {
	background: #1976d2;
	color: #fff;
	font-size: 22rpx;
	padding: 4rpx 12rpx;
	border-radius: 20rpx;
	margin-right: 12rpx;
}
.menu-value {
	font-size: 26rpx;
	color: #1976d2;
	margin-right: 12rpx;
}
.menu-arrow {
	color: #ccc;
	font-size: 32rpx;
}

/* 底部 */
.footer {
	text-align: center;
	padding: 40rpx 0;
}
.footer text {
	color: #ccc;
	font-size: 24rpx;
}
</style>
