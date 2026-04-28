<template>
	<view class="container">
		<view class="invoice-list" v-if="list.length">
			<view class="invoice-item" v-for="item in list" :key="item.id" @click="goDetail(item)">
				<view class="invoice-header">
					<text class="invoice-title">{{ item.title }}</text>
					<text class="invoice-status" :class="'status-' + item.status">{{ item.statusText }}</text>
				</view>
				<view class="invoice-info">
					<view class="info-row">
						<text class="label">发票类型</text>
						<text class="value">{{ item.typeText }}</text>
					</view>
					<view class="info-row">
						<text class="label">开票金额</text>
						<text class="value price">¥{{ item.amount }}</text>
					</view>
					<view class="info-row">
						<text class="label">申请时间</text>
						<text class="value">{{ formatTime(item.createTime) }}</text>
					</view>
				</view>
				<view class="invoice-footer">
					<text class="order-no">订单号：{{ item.orderNo }}</text>
					<text class="arrow">›</text>
				</view>
			</view>
		</view>
		
		<view class="empty" v-else-if="!loading">
			<image class="empty-icon" src="/static/empty.png" mode="aspectFit"></image>
			<text class="empty-text">暂无发票记录</text>
		</view>
		
		<view class="loading" v-if="loading">
			<text>加载中...</text>
		</view>
	</view>
</template>

<script>
import { invoiceApi } from '@/api/index.js'

export default {
	data() {
		return {
			list: [],
			page: 1,
			size: 10,
			total: 0,
			loading: false,
			finished: false
		}
	},
	onLoad() {
		this.loadList()
	},
	onPullDownRefresh() {
		this.page = 1
		this.finished = false
		this.loadList().finally(() => uni.stopPullDownRefresh())
	},
	onReachBottom() {
		if (!this.finished && !this.loading) {
			this.loadMore()
		}
	},
	methods: {
		async loadList() {
			this.loading = true
			try {
				const res = await invoiceApi.getInvoiceList({ page: this.page, size: this.size })
				this.list = res.data?.list || []
				this.total = res.data?.total || 0
				this.finished = this.list.length >= this.total
			} catch (e) {
				console.error('加载发票列表失败', e)
			} finally {
				this.loading = false
			}
		},
		async loadMore() {
			this.page++
			this.loading = true
			try {
				const res = await invoiceApi.getInvoiceList({ page: this.page, size: this.size })
				const newList = res.data?.list || []
				this.list = [...this.list, ...newList]
				this.finished = this.list.length >= this.total
			} catch (e) {
				console.error('加载更多失败', e)
				this.page--
			} finally {
				this.loading = false
			}
		},
		formatTime(time) {
			if (!time) return ''
			if (typeof time === 'string') {
				return time.replace('T', ' ').substring(0, 16)
			}
			return time
		},
		goDetail(item) {
			uni.navigateTo({
				url: `/pages/invoice/detail?id=${item.id}`
			})
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding: 20rpx;
}

.invoice-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.invoice-item {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}

.invoice-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f5f5f5;
}

.invoice-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	flex: 1;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.invoice-status {
	font-size: 24rpx;
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	flex-shrink: 0;
	margin-left: 16rpx;
}
.invoice-status.status-pending {
	background: #fff3e0;
	color: #f57c00;
}
.invoice-status.status-issued {
	background: #e8f5e9;
	color: #4caf50;
}
.invoice-status.status-failed {
	background: #ffebee;
	color: #f44336;
}

.invoice-info {
	margin-bottom: 16rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 8rpx 0;
}
.info-row .label {
	font-size: 26rpx;
	color: #999;
}
.info-row .value {
	font-size: 26rpx;
	color: #333;
}
.info-row .value.price {
	color: #1976d2;
	font-weight: bold;
}

.invoice-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 16rpx;
	border-top: 1rpx solid #f5f5f5;
}
.order-no {
	font-size: 24rpx;
	color: #999;
}
.arrow {
	font-size: 32rpx;
	color: #ccc;
}

.empty {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 200rpx;
}
.empty-icon {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 20rpx;
}
.empty-text {
	font-size: 28rpx;
	color: #999;
}

.loading {
	text-align: center;
	padding: 30rpx;
	color: #999;
	font-size: 26rpx;
}
</style>
