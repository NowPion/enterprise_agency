<template>
	<view class="container">
		<view class="invoice-card" v-if="invoice">
			<!-- 状态头部 -->
			<view class="status-header" :class="'status-' + invoice.status">
				<text class="status-text">{{ invoice.statusText }}</text>
				<text class="status-desc">{{ statusDesc }}</text>
			</view>
			
			<!-- 发票信息 -->
			<view class="info-section">
				<view class="section-title">发票信息</view>
				<view class="info-row">
					<text class="label">发票类型</text>
					<text class="value">{{ invoice.typeText }}</text>
				</view>
				<view class="info-row">
					<text class="label">发票抬头</text>
					<text class="value">{{ invoice.title }}</text>
				</view>
				<view class="info-row" v-if="invoice.taxNo">
					<text class="label">税号</text>
					<text class="value">{{ invoice.taxNo }}</text>
				</view>
				<view class="info-row">
					<text class="label">开票金额</text>
					<text class="value price">¥{{ invoice.amount }}</text>
				</view>
				<view class="info-row">
					<text class="label">申请时间</text>
					<text class="value">{{ formatTime(invoice.createTime) }}</text>
				</view>
				<view class="info-row" v-if="invoice.issueTime">
					<text class="label">开票时间</text>
					<text class="value">{{ formatTime(invoice.issueTime) }}</text>
				</view>
			</view>
			
			<!-- 接收信息 -->
			<view class="info-section">
				<view class="section-title">接收信息</view>
				<view class="info-row">
					<text class="label">接收邮箱</text>
					<text class="value">{{ invoice.email }}</text>
				</view>
			</view>
			
			<!-- 关联订单 -->
			<view class="info-section">
				<view class="section-title">关联订单</view>
				<view class="order-item" @click="goOrderDetail">
					<text class="order-no">订单号：{{ invoice.orderNo }}</text>
					<text class="arrow">›</text>
				</view>
			</view>
			
			<!-- 发票文件（已开票时显示） -->
			<view class="info-section" v-if="invoice.status === 'issued' && invoice.fileUrl">
				<view class="section-title">电子发票</view>
				<view class="file-row">
					<text class="file-name">电子发票.pdf</text>
					<button class="download-btn" @click="downloadInvoice">下载发票</button>
				</view>
			</view>
			
			<!-- 失败原因 -->
			<view class="info-section" v-if="invoice.status === 'failed' && invoice.failReason">
				<view class="section-title">失败原因</view>
				<view class="fail-reason">{{ invoice.failReason }}</view>
			</view>
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
			id: null,
			invoice: null,
			loading: false
		}
	},
	computed: {
		statusDesc() {
			if (!this.invoice) return ''
			const map = {
				'pending': '发票申请已提交，等待开具',
				'issued': '发票已开具，请查收邮箱',
				'failed': '开票失败，请联系客服'
			}
			return map[this.invoice.status] || ''
		}
	},
	onLoad(options) {
		this.id = options.id
		this.loadDetail()
	},
	methods: {
		async loadDetail() {
			if (!this.id) return
			this.loading = true
			try {
				const res = await invoiceApi.getInvoiceDetail(this.id)
				if (res.code === 0) {
					this.invoice = res.data
				} else {
					uni.showToast({ title: res.msg || '加载失败', icon: 'none' })
				}
			} catch (e) {
				console.error('加载发票详情失败', e)
				uni.showToast({ title: '加载失败', icon: 'none' })
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
		goOrderDetail() {
			if (this.invoice?.orderId) {
				uni.navigateTo({
					url: `/pages/order/detail?id=${this.invoice.orderId}`
				})
			}
		},
		downloadInvoice() {
			if (!this.invoice?.fileUrl) {
				uni.showToast({ title: '发票文件不存在', icon: 'none' })
				return
			}
			uni.downloadFile({
				url: this.invoice.fileUrl,
				success: (res) => {
					if (res.statusCode === 200) {
						uni.openDocument({
							filePath: res.tempFilePath,
							showMenu: true,
							success: () => {},
							fail: () => {
								uni.showToast({ title: '打开文件失败', icon: 'none' })
							}
						})
					}
				},
				fail: () => {
					uni.showToast({ title: '下载失败', icon: 'none' })
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
	padding: 20rpx;
}

.invoice-card {
	background: #fff;
	border-radius: 16rpx;
	overflow: hidden;
}

.status-header {
	padding: 40rpx;
	text-align: center;
}
.status-header.status-pending {
	background: linear-gradient(135deg, #ff9800, #f57c00);
}
.status-header.status-issued {
	background: linear-gradient(135deg, #4caf50, #388e3c);
}
.status-header.status-failed {
	background: linear-gradient(135deg, #f44336, #d32f2f);
}
.status-text {
	font-size: 36rpx;
	font-weight: bold;
	color: #fff;
	display: block;
}
.status-desc {
	font-size: 26rpx;
	color: rgba(255,255,255,0.9);
	margin-top: 12rpx;
	display: block;
}

.info-section {
	padding: 24rpx;
	border-bottom: 1rpx solid #f5f5f5;
}
.info-section:last-child {
	border-bottom: none;
}
.section-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	padding: 12rpx 0;
}
.info-row .label {
	font-size: 28rpx;
	color: #999;
}
.info-row .value {
	font-size: 28rpx;
	color: #333;
	text-align: right;
	flex: 1;
	margin-left: 20rpx;
}
.info-row .value.price {
	color: #1976d2;
	font-weight: bold;
}

.order-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 0;
}
.order-no {
	font-size: 28rpx;
	color: #666;
}
.arrow {
	font-size: 32rpx;
	color: #ccc;
}

.file-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.file-name {
	font-size: 28rpx;
	color: #333;
}
.download-btn {
	background: #1976d2;
	color: #fff;
	font-size: 26rpx;
	padding: 12rpx 24rpx;
	border-radius: 30rpx;
	border: none;
}
.download-btn::after {
	border: none;
}

.fail-reason {
	font-size: 28rpx;
	color: #f44336;
	line-height: 1.6;
}

.loading {
	text-align: center;
	padding: 60rpx;
	color: #999;
	font-size: 28rpx;
}
</style>
