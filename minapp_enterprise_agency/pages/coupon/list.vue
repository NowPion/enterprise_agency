<template>
	<view class="container">
		<!-- 顶部Tab -->
		<view class="top-tabs">
			<view class="tab-item" :class="{ active: currentTab === 'center' }" @click="changeTab('center')">领券中心</view>
			<view class="tab-item" :class="{ active: currentTab === 'my' }" @click="changeTab('my')">我的优惠券</view>
		</view>

		<!-- 领券中心 -->
		<view v-if="currentTab === 'center'">
			<view class="coupon-list" v-if="availableCoupons.length">
				<view class="coupon-item" v-for="item in availableCoupons" :key="item.id">
					<view class="coupon-left">
						<text class="coupon-symbol">¥</text>
						<text class="coupon-amount">{{ getDiscountAmount(item) }}</text>
						<text class="coupon-condition">{{ getConditionText(item) }}</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{ item.name }}</text>
						<text class="coupon-desc">{{ item.description || item.typeText }}</text>
						<text class="coupon-remain" v-if="item.remainCount !== -1">剩余{{ item.remainCount }}张</text>
						<text class="coupon-remain" v-else>不限量</text>
					</view>
					<view class="receive-btn" :class="{ disabled: !item.canReceive }" @click="receiveCoupon(item)">
						{{ item.canReceive ? '立即领取' : '已领取' }}
					</view>
				</view>
			</view>
			<view class="empty" v-else>
				<image class="empty-img" src="/static/mr.png" mode="aspectFit"></image>
				<text class="empty-text">暂无可领取的优惠券</text>
			</view>
		</view>

		<!-- 我的优惠券 -->
		<view v-else>
			<!-- 状态筛选 -->
			<view class="status-tabs">
				<view class="status-item" :class="{ active: currentStatus === 'unused' }" @click="changeStatus('unused')">可使用</view>
				<view class="status-item" :class="{ active: currentStatus === 'used' }" @click="changeStatus('used')">已使用</view>
				<view class="status-item" :class="{ active: currentStatus === 'expired' }" @click="changeStatus('expired')">已过期</view>
			</view>

			<view class="coupon-list" v-if="myCoupons.length">
				<view class="coupon-item" :class="{ disabled: currentStatus !== 'unused' }" v-for="item in myCoupons" :key="item.id">
					<view class="coupon-left">
						<text class="coupon-symbol">¥</text>
						<text class="coupon-amount">{{ getDiscountAmount(item) }}</text>
						<text class="coupon-condition">{{ getConditionText(item) }}</text>
					</view>
					<view class="coupon-right">
						<text class="coupon-name">{{ item.name }}</text>
						<text class="coupon-desc">{{ item.typeText }}</text>
						<text class="coupon-time">{{ formatTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
					</view>
					<view class="coupon-status" v-if="currentStatus === 'used'">已使用</view>
					<view class="coupon-status" v-else-if="currentStatus === 'expired'">已过期</view>
					<view class="use-btn" v-else @click="goUse">去使用</view>
				</view>
			</view>
			<view class="empty" v-else>
				<image class="empty-img" src="/static/mr.png" mode="aspectFit"></image>
				<text class="empty-text">暂无优惠券</text>
			</view>
		</view>
	</view>
</template>

<script>
import { couponApi } from '@/api/index.js'

export default {
	data() {
		return {
			currentTab: 'center',
			currentStatus: 'unused',
			availableCoupons: [],
			myCoupons: [],
			page: 1,
			size: 20,
			hasMore: true,
			loading: false
		}
	},
	onLoad() {
		this.loadAvailableCoupons()
	},
	onPullDownRefresh() {
		this.refresh().finally(() => uni.stopPullDownRefresh())
	},
	onReachBottom() {
		if (this.currentTab === 'my' && this.hasMore && !this.loading) {
			this.loadMore()
		}
	},
	methods: {
		async refresh() {
			if (this.currentTab === 'center') {
				await this.loadAvailableCoupons()
			} else {
				this.page = 1
				this.hasMore = true
				await this.loadMyCoupons()
			}
		},
		changeTab(tab) {
			this.currentTab = tab
			if (tab === 'center') {
				this.loadAvailableCoupons()
			} else {
				this.page = 1
				this.hasMore = true
				this.loadMyCoupons()
			}
		},
		changeStatus(status) {
			this.currentStatus = status
			this.page = 1
			this.hasMore = true
			this.loadMyCoupons()
		},
		async loadAvailableCoupons() {
			const token = uni.getStorageSync('token')
			if (!token) {
				this.availableCoupons = []
				return
			}
			try {
				const res = await couponApi.getAvailableCoupons()
				this.availableCoupons = res.data || []
			} catch (e) {
				console.error('加载优惠券失败', e)
			}
		},
		async loadMyCoupons() {
			const token = uni.getStorageSync('token')
			if (!token) {
				this.myCoupons = []
				return
			}
			if (this.loading) return
			this.loading = true
			try {
				const res = await couponApi.getMyCoupons({
					page: this.page,
					size: this.size,
					status: this.currentStatus
				})
				const list = res.data?.list || []
				if (this.page === 1) {
					this.myCoupons = list
				} else {
					this.myCoupons = [...this.myCoupons, ...list]
				}
				this.hasMore = list.length >= this.size
			} catch (e) {
				console.error('加载优惠券失败', e)
			} finally {
				this.loading = false
			}
		},
		async loadMore() {
			this.page++
			await this.loadMyCoupons()
		},
		async receiveCoupon(item) {
			if (!item.canReceive) return
			const token = uni.getStorageSync('token')
			if (!token) {
				uni.showToast({ title: '请先登录', icon: 'none' })
				return
			}
			try {
				await couponApi.receiveCoupon(item.id)
				uni.showToast({ title: '领取成功', icon: 'success' })
				// 刷新列表
				this.loadAvailableCoupons()
			} catch (e) {
				console.error('领取失败', e)
			}
		},
		goUse() {
			uni.switchTab({ url: '/pages/index/index' })
		},
		getDiscountAmount(item) {
			if (item.type === 'fixed') {
				return item.value
			} else {
				return (100 - item.value * 100) / 10 + '折'
			}
		},
		getConditionText(item) {
			if (!item.minAmount || item.minAmount == 0) {
				return '无门槛'
			}
			return '满' + item.minAmount + '可用'
		},
		formatTime(time) {
			if (!time) return ''
			return time.substring(0, 10)
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

/* 顶部Tab */
.top-tabs {
	display: flex;
	background: #fff;
	padding: 0;
}
.tab-item {
	flex: 1;
	text-align: center;
	font-size: 30rpx;
	color: #666;
	padding: 28rpx 0;
	position: relative;
}
.tab-item.active {
	color: #1976d2;
	font-weight: bold;
}
.tab-item.active::after {
	content: '';
	position: absolute;
	bottom: 0;
	left: 50%;
	transform: translateX(-50%);
	width: 60rpx;
	height: 6rpx;
	background: #1976d2;
	border-radius: 3rpx;
}

/* 状态筛选 */
.status-tabs {
	display: flex;
	background: #fff;
	padding: 16rpx 0;
	margin-top: 2rpx;
}
.status-item {
	flex: 1;
	text-align: center;
	font-size: 26rpx;
	color: #666;
	padding: 12rpx 0;
}
.status-item.active {
	color: #1976d2;
	font-weight: bold;
}

/* 优惠券列表 */
.coupon-list {
	padding: 20rpx;
}
.coupon-item {
	display: flex;
	align-items: center;
	background: #fff;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
	overflow: hidden;
	position: relative;
}
.coupon-item.disabled {
	opacity: 0.6;
}
.coupon-left {
	width: 200rpx;
	background: linear-gradient(135deg, #1976d2, #1565c0);
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 30rpx 0;
}
.coupon-symbol {
	font-size: 24rpx;
	color: #fff;
}
.coupon-amount {
	font-size: 52rpx;
	font-weight: bold;
	color: #fff;
}
.coupon-condition {
	font-size: 20rpx;
	color: rgba(255,255,255,0.8);
	margin-top: 6rpx;
}
.coupon-right {
	flex: 1;
	padding: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
}
.coupon-name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
}
.coupon-desc {
	font-size: 24rpx;
	color: #666;
	margin-top: 6rpx;
}
.coupon-remain {
	font-size: 22rpx;
	color: #999;
	margin-top: 6rpx;
}
.coupon-time {
	font-size: 22rpx;
	color: #999;
	margin-top: 6rpx;
}
.receive-btn {
	padding: 16rpx 24rpx;
	background: #1976d2;
	color: #fff;
	font-size: 24rpx;
	border-radius: 30rpx;
	margin-right: 20rpx;
}
.receive-btn.disabled {
	background: #ccc;
}
.use-btn {
	padding: 16rpx 24rpx;
	background: #1976d2;
	color: #fff;
	font-size: 24rpx;
	border-radius: 30rpx;
	margin-right: 20rpx;
}
.coupon-status {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	font-size: 22rpx;
	color: #999;
	padding: 4rpx 12rpx;
	border: 1rpx solid #ddd;
	border-radius: 4rpx;
}

/* 空状态 */
.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 200rpx;
}
.empty-img {
	width: 300rpx;
	height: 300rpx;
}
.empty-text {
	font-size: 28rpx;
	color: #999;
	margin-top: 20rpx;
}
</style>