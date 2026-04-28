<template>
	<view class="container">
		<!-- 状态筛选 -->
		<scroll-view class="status-tabs" scroll-x>
			<view class="tab-item" :class="{ active: currentStatus === null }" @click="changeStatus(null)">全部</view>
			<view class="tab-item" :class="{ active: currentStatus === 0 }" @click="changeStatus(0)">待支付</view>
			<view class="tab-item" :class="{ active: currentStatus === 1 }" @click="changeStatus(1)">已支付</view>
			<view class="tab-item" :class="{ active: currentStatus === 2 }" @click="changeStatus(2)">办理中</view>
			<view class="tab-item" :class="{ active: currentStatus === 3 }" @click="changeStatus(3)">已完成</view>
			<view class="tab-item" :class="{ active: currentStatus === 4 }" @click="changeStatus(4)">退款中</view>
		</scroll-view>

		<!-- 订单列表 -->
		<view class="order-list" v-if="orders.length">
			<view class="order-item" v-for="item in orders" :key="item.id" @click="goDetail(item)">
				<view class="order-header">
					<text class="order-no">订单号：{{ item.orderNo }}</text>
					<text class="order-status" :class="'status-' + item.status">{{ item.statusText }}</text>
				</view>
				<view class="order-body">
					<text class="product-name">{{ item.productName }}</text>
					<view class="order-info">
						<text class="create-time">{{ formatTime(item.createTime) }}</text>
						<text class="pay-amount">¥{{ item.payAmount }}</text>
					</view>
				</view>
				<!-- 办理进度 -->
				<view class="progress-bar" v-if="item.progressList && item.progressList.length">
					<view class="progress-item" v-for="(p, idx) in item.progressList.slice(0, 3)" :key="p.id">
						<view class="progress-dot" :class="{ active: idx === 0 }"></view>
						<text class="progress-title">{{ p.title }}</text>
					</view>
					<text class="progress-more" v-if="item.progressList.length > 3">...</text>
				</view>
				<view class="order-actions">
					<view class="btn-pay" v-if="item.status === 0" @click.stop="goPay(item)">去支付</view>
					<view class="btn-detail" @click.stop="goDetail(item)">查看详情</view>
				</view>
			</view>
		</view>

		<!-- 空状态 -->
		<view class="empty" v-else>
			<image class="empty-img" src="/static/mr.png" mode="aspectFit"></image>
			<text class="empty-text">暂无订单</text>
		</view>
	</view>
</template>

<script>
import { orderApi } from '@/api/index.js'

export default {
	data() {
		return {
			currentStatus: null,
			orders: [],
			page: 1,
			size: 10,
			hasMore: true,
			loading: false
		}
	},
	onShow() {
		// 检查是否已登录
		const token = uni.getStorageSync('token')
		if (token) {
			this.refresh()
		}
	},
	onPullDownRefresh() {
		this.refresh().finally(() => uni.stopPullDownRefresh())
	},
	onReachBottom() {
		if (this.hasMore && !this.loading) {
			this.loadMore()
		}
	},
	methods: {
		async refresh() {
			this.page = 1
			this.hasMore = true
			await this.loadOrders()
		},
		async loadOrders() {
			// 检查是否已登录
			const token = uni.getStorageSync('token')
			if (!token) {
				this.orders = []
				return
			}
			if (this.loading) return
			this.loading = true
			try {
				const params = { page: this.page, size: this.size }
				if (this.currentStatus !== null) params.status = this.currentStatus
				const res = await orderApi.getOrderList(params)
				const list = res.data?.list || []
				if (this.page === 1) {
					this.orders = list
				} else {
					this.orders = [...this.orders, ...list]
				}
				this.hasMore = list.length >= this.size
			} catch (e) {
				console.error('加载订单失败', e)
			} finally {
				this.loading = false
			}
		},
		async loadMore() {
			this.page++
			await this.loadOrders()
		},
		changeStatus(status) {
			this.currentStatus = status
			this.refresh()
		},
		formatTime(time) {
			if (!time) return ''
			return time.replace('T', ' ').substring(0, 16)
		},
		goDetail(item) {
			uni.navigateTo({ url: `/pages/order/detail?id=${item.id}` })
		},
		async goPay(item) {
			uni.showLoading({ title: '发起支付...' })
			try {
				const res = await orderApi.prepay(item.id)
				uni.hideLoading()
				// 调用微信支付
				const payData = res.data
				uni.requestPayment({
					provider: 'wxpay',
					timeStamp: payData.timeStamp,
					nonceStr: payData.nonceStr,
					package: payData.packageValue,
					signType: payData.signType,
					paySign: payData.paySign,
					success: () => {
						uni.showToast({ title: '支付成功', icon: 'success' })
						this.refresh()
					},
					fail: (err) => {
						if (err.errMsg !== 'requestPayment:fail cancel') {
							uni.showToast({ title: '支付失败', icon: 'none' })
						}
					}
				})
			} catch (e) {
				uni.hideLoading()
				console.error('发起支付失败', e)
			}
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
}

/* 状态筛选 */
.status-tabs {
	display: flex;
	white-space: nowrap;
	background: #fff;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #eee;
}
.tab-item {
	display: inline-block;
	padding: 12rpx 32rpx;
	font-size: 28rpx;
	color: #666;
}
.tab-item.active {
	color: #1976d2;
	font-weight: bold;
}

/* 订单列表 */
.order-list {
	padding: 20rpx;
}
.order-item {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	margin-bottom: 20rpx;
}
.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-bottom: 16rpx;
	border-bottom: 1rpx solid #f0f0f0;
}
.order-no {
	font-size: 24rpx;
	color: #999;
}
.order-status {
	font-size: 26rpx;
	font-weight: bold;
}
.status-0 { color: #ff9800; }
.status-1 { color: #2196f3; }
.status-2 { color: #1976d2; }
.status-3 { color: #4caf50; }
.status-4 { color: #9c27b0; }
.status-5 { color: #999; }

.order-body {
	padding: 20rpx 0;
}
.product-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 12rpx;
}
.order-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.create-time {
	font-size: 24rpx;
	color: #999;
}
.pay-amount {
	font-size: 32rpx;
	font-weight: bold;
	color: #d11a1a;
}

/* 办理进度 */
.progress-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 0;
	border-top: 1rpx solid #f0f0f0;
	gap: 16rpx;
	overflow: hidden;
}
.progress-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
}
.progress-dot {
	width: 16rpx;
	height: 16rpx;
	border-radius: 50%;
	background: #ddd;
}
.progress-dot.active {
	background: #1976d2;
}
.progress-title {
	font-size: 24rpx;
	color: #666;
}
.progress-more {
	font-size: 24rpx;
	color: #999;
}

/* 操作按钮 */
.order-actions {
	display: flex;
	justify-content: flex-end;
	gap: 20rpx;
	padding-top: 16rpx;
	border-top: 1rpx solid #f0f0f0;
}
.btn-pay {
	padding: 12rpx 32rpx;
	background: #d11a1a;
	color: #fff;
	font-size: 26rpx;
	border-radius: 30rpx;
}
.btn-detail {
	padding: 12rpx 32rpx;
	background: #fff;
	color: #d11a1a;
	font-size: 26rpx;
	border-radius: 30rpx;
	border: 1rpx solid #d11a1a;
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
