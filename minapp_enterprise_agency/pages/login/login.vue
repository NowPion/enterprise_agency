<template>
	<view class="container">
		<view class="logo-section">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="app-name">舌苔舌诊</text>
			<text class="app-desc">AI智能舌诊，健康自测</text>
		</view>
		
		<view class="login-section">
			<button class="wx-login-btn" @click="wxLogin">
				<text class="btn-icon">💬</text>
				<text>微信授权登录</text>
			</button>
		</view>
		
		<view class="agreement">
			<checkbox-group @change="agreeChange">
				<label class="agreement-label">
					<checkbox :checked="isAgree" color="#2196F3" />
					<text>我已阅读并同意</text>
					<text class="link" @click.stop="goAgreement('user')">《用户协议》</text>
					<text>和</text>
					<text class="link" @click.stop="goAgreement('privacy')">《隐私政策》</text>
				</label>
			</checkbox-group>
		</view>
	</view>
</template>

<script>
import { userApi } from '@/api/index.js'

export default {
	data() {
		return {
			isAgree: false,
			inviter: ''
		}
	},
	onLoad(options) {
		// 获取邀请人参数
		if (options.inviter) {
			this.inviter = options.inviter
		}
	},
	methods: {
		agreeChange(e) {
			this.isAgree = e.detail.value.length > 0
		},
		
		// 微信登录
		wxLogin() {
			if (!this.isAgree) {
				uni.showToast({ title: '请先同意用户协议', icon: 'none' })
				return
			}
			
			uni.showLoading({ title: '登录中...' })
			
			// 1. 调用wx.login获取code
			uni.login({
				provider: 'weixin',
				success: (loginRes) => {
					console.log('wx.login code:', loginRes.code)
					
					// 2. 获取用户信息（头像昵称）
					uni.getUserProfile({
						desc: '用于完善用户资料',
						success: (userRes) => {
							this.doLogin(loginRes.code, userRes.userInfo)
						},
						fail: () => {
							// 用户拒绝授权，使用默认信息
							this.doLogin(loginRes.code, null)
						}
					})
				},
				fail: () => {
					uni.hideLoading()
					uni.showToast({ title: '登录失败', icon: 'none' })
				}
			})
		},
		
		// 调用后端登录接口
		async doLogin(code, wxUserInfo) {
			try {
				// 调用 POST /api/user/wxLogin
				const res = await userApi.wxLogin({
					code: code,
					nickName: wxUserInfo?.nickName || '',
					avatarUrl: wxUserInfo?.avatarUrl || '',
					inviter: this.inviter
				})
				
				if (res.code === 0) {
					// 保存token
					uni.setStorageSync('token', res.data.token)
					
					// 保存用户信息
					const userInfo = {
						id: res.data.userInfo.id,
						openid: res.data.userInfo.openid,
						nickname: res.data.userInfo.nickname,
						avatar: res.data.userInfo.avatar,
						phone: res.data.userInfo.phone,
						remainCount: res.data.userInfo.remainCount,
						isLogin: true,
						isNewUser: res.data.userInfo.isNewUser
					}
					uni.setStorageSync('userInfo', userInfo)
					
					uni.hideLoading()
					
					// 新用户提示
					if (res.data.userInfo.isNewUser) {
						uni.showToast({ title: '注册成功，赠送3次诊断', icon: 'none', duration: 2000 })
					} else {
						uni.showToast({ title: '登录成功', icon: 'success' })
					}
					
					setTimeout(() => {
						uni.switchTab({ url: '/pages/mine/mine' })
					}, 1500)
				} else {
					uni.hideLoading()
					uni.showToast({ title: res.msg || '登录失败', icon: 'none' })
				}
			} catch (e) {
				uni.hideLoading()
				console.error('登录失败', e)
				
				// 开发环境模拟登录（后端接入后删除）
				this.mockLogin(wxUserInfo)
			}
		},
		
		// 模拟登录（开发用，后端接入后删除）
		mockLogin(wxUserInfo) {
			const userInfo = {
				id: 10001,
				openid: 'mock_openid_xxx',
				nickname: wxUserInfo?.nickName || '微信用户',
				avatar: wxUserInfo?.avatarUrl || '',
				phone: '',
				remainCount: 3,
				isLogin: true,
				isNewUser: true
			}
			uni.setStorageSync('token', 'mock_token_xxx')
			uni.setStorageSync('userInfo', userInfo)
			
			uni.showToast({ title: '登录成功', icon: 'success' })
			setTimeout(() => {
				uni.switchTab({ url: '/pages/mine/mine' })
			}, 1500)
		},
		
		goAgreement(type) {
			uni.showToast({ title: type === 'user' ? '用户协议' : '隐私政策', icon: 'none' })
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background: #fff;
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 120rpx 60rpx 60rpx;
}

.logo-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 100rpx;
}

.logo {
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 30rpx;
}

.app-name {
	font-size: 44rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
}

.app-desc {
	font-size: 28rpx;
	color: #999;
}

.login-section {
	width: 100%;
}

.wx-login-btn {
	width: 100%;
	height: 96rpx;
	border-radius: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	margin-bottom: 30rpx;
	border: none;
	background: #07C160;
	color: #fff;
}

.btn-icon {
	margin-right: 16rpx;
	font-size: 36rpx;
}

.agreement {
	position: fixed;
	bottom: 80rpx;
	left: 0;
	right: 0;
	display: flex;
	justify-content: center;
}

.agreement-label {
	display: flex;
	align-items: center;
	font-size: 24rpx;
	color: #999;
}

.agreement-label checkbox {
	transform: scale(0.7);
	margin-right: 8rpx;
}

.link {
	color: #2196F3;
}
</style>
