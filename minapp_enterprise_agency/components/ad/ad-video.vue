<template>
	<!-- 微信小程序激励视频广告组件 -->
	<view class="ad-video-wrapper">
		<view class="video-btn" @click="showVideoAd" :class="{ disabled: !canWatch }">
			<text class="btn-icon">▶</text>
			<view class="btn-info">
				<text class="btn-title">{{ title }}</text>
				<text class="btn-desc">{{ desc }}</text>
			</view>
			<view class="btn-reward">
				<text>+{{ rewardCount }}次</text>
			</view>
		</view>
		<!-- 今日观看情况 -->
		<view class="watch-info" v-if="showWatchInfo">
			<text>今日已观看 {{ todayViews }}/{{ maxViews }} 次</text>
		</view>
	</view>
</template>

<script>
import { adApi } from '@/api/index.js'

export default {
	name: 'AdVideo',
	props: {
		title: {
			type: String,
			default: '观看视频'
		},
		desc: {
			type: String,
			default: '免费获得诊断次数'
		},
		rewardCount: {
			type: Number,
			default: 1
		},
		adUnitId: {
			type: String,
			default: ''
		},
		showWatchInfo: {
			type: Boolean,
			default: true
		}
	},
	data() {
		return {
			videoAd: null,
			canWatch: true,
			watchId: '',
			todayViews: 0,
			maxViews: 10
		}
	},
	mounted() {
		this.initVideoAd()
		this.loadTodayViews()
	},
	methods: {
		// 初始化激励视频广告
		initVideoAd() {
			// #ifdef MP-WEIXIN
			if (wx.createRewardedVideoAd) {
				this.videoAd = wx.createRewardedVideoAd({
					adUnitId: this.adUnitId || 'adunit-xxxxxxxxxx'
				})
				
				this.videoAd.onLoad(() => {
					console.log('激励视频广告加载成功')
				})
				
				this.videoAd.onError((err) => {
					console.error('激励视频广告加载失败', err)
				})
				
				this.videoAd.onClose((res) => {
					if (res && res.isEnded) {
						// 用户完整观看了广告
						this.onAdComplete()
					} else {
						// 用户中途退出
						uni.showToast({ title: '观看完整视频才能获得奖励', icon: 'none' })
					}
				})
			}
			// #endif
		},
		
		// 加载今日观看次数
		async loadTodayViews() {
			try {
				const res = await adApi.getTodayAdViews()
				if (res.code === 0) {
					this.todayViews = res.data.viewCount
					this.maxViews = res.data.maxCount
					this.canWatch = res.data.canWatch
				}
			} catch (e) {
				console.error('加载今日观看次数失败', e)
			}
		},
		
		// 检查是否可以观看广告
		async checkCanWatch() {
			try {
				const res = await adApi.checkCanWatchAd()
				if (res.code === 0) {
					this.canWatch = res.data.canWatch
					if (!res.data.canWatch) {
						uni.showToast({ title: res.data.reason || '今日观看次数已达上限', icon: 'none' })
					}
				}
				return this.canWatch
			} catch (e) {
				console.error('检查广告观看状态失败', e)
				return false
			}
		},
		
		// 显示激励视频广告
		async showVideoAd() {
			// 检查登录
			const userInfo = uni.getStorageSync('userInfo')
			if (!userInfo || !userInfo.isLogin) {
				uni.switchTab({ url: '/pages/mine/mine' })
				return
			}
			
			// 检查是否可以观看
			const canWatch = await this.checkCanWatch()
			if (!canWatch) return
			
			// 上报广告开始
			try {
				const res = await adApi.reportAdStart({
					adType: 'video',
					adId: this.adUnitId
				})
				if (res.code === 0) {
					this.watchId = res.data.watchId
				} else if (res.code === 40001) {
					uni.showToast({ title: '今日观看次数已达上限', icon: 'none' })
					this.canWatch = false
					return
				}
			} catch (e) {
				console.error('上报广告开始失败', e)
			}
			
			// #ifdef MP-WEIXIN
			if (this.videoAd) {
				this.videoAd.show().catch(() => {
					// 广告加载失败，重新加载
					this.videoAd.load().then(() => this.videoAd.show())
				})
			}
			// #endif
			
			// #ifndef MP-WEIXIN
			// 非微信环境模拟
			uni.showModal({
				title: '提示',
				content: '激励视频广告仅在微信小程序中可用',
				showCancel: false
			})
			// #endif
		},
		
		// 广告观看完成
		async onAdComplete() {
			try {
				const res = await adApi.reportAdComplete({
					watchId: this.watchId,
					adType: 'video',
					adId: this.adUnitId,
					duration: 30
				})
				if (res.code === 0) {
					uni.showToast({ title: `获得${res.data.rewardCount}次诊断次数`, icon: 'success' })
					this.todayViews = res.data.todayViews
					this.canWatch = res.data.remainViews > 0
					this.$emit('reward', res.data)
					
					// 更新本地用户信息
					const userInfo = uni.getStorageSync('userInfo')
					if (userInfo) {
						userInfo.remainCount = res.data.remainCount
						uni.setStorageSync('userInfo', userInfo)
					}
				} else if (res.code === 40004) {
					uni.showToast({ title: '观看时长不足', icon: 'none' })
				} else if (res.code === 40005) {
					uni.showToast({ title: '奖励已发放', icon: 'none' })
				}
			} catch (e) {
				console.error('上报广告完成失败', e)
				uni.showToast({ title: '领取奖励失败', icon: 'none' })
			}
		}
	},
	beforeDestroy() {
		// #ifdef MP-WEIXIN
		if (this.videoAd) {
			this.videoAd.destroy()
		}
		// #endif
	}
}
</script>

<style lang="scss" scoped>
.video-btn {
	background: #fff;
	margin: 20rpx 30rpx;
	border-radius: 20rpx;
	padding: 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.05);
}

.video-btn.disabled {
	opacity: 0.6;
}

.btn-icon {
	width: 80rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #FF9800, #FFB74D);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 32rpx;
	margin-right: 20rpx;
}

.btn-info {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.btn-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}

.btn-desc {
	font-size: 24rpx;
	color: #999;
	margin-top: 6rpx;
}

.btn-reward {
	background: #FFF3E0;
	padding: 12rpx 24rpx;
	border-radius: 30rpx;
}

.btn-reward text {
	color: #FF9800;
	font-size: 26rpx;
	font-weight: bold;
}

.watch-info {
	text-align: center;
	padding: 0 30rpx 20rpx;
}

.watch-info text {
	font-size: 22rpx;
	color: #999;
}
</style>
