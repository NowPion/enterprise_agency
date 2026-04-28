<template>
	<view class="container">
		<!-- 订单状态 -->
		<view class="status-card" :class="'status-bg-' + order.status">
			<text class="status-text">{{ order.statusText }}</text>
			<text class="status-desc">{{ getStatusDesc() }}</text>
		</view>

		<!-- 订单信息 -->
		<view class="section">
			<view class="section-title">订单信息</view>
			<view class="info-row">
				<text class="label">订单编号</text>
				<text class="value">{{ order.orderNo }}</text>
			</view>
			<view class="info-row">
				<text class="label">办理业务</text>
				<text class="value">{{ order.productName }}</text>
			</view>
			<view class="info-row">
				<text class="label">订单金额</text>
				<text class="value price">¥{{ order.amount }}</text>
			</view>
			<view class="info-row" v-if="order.discountAmount > 0">
				<text class="label">优惠金额</text>
				<text class="value discount">-¥{{ order.discountAmount }}</text>
			</view>
			<view class="info-row">
				<text class="label">实付金额</text>
				<text class="value price">¥{{ order.payAmount }}</text>
			</view>
			<view class="info-row">
				<text class="label">下单时间</text>
				<text class="value">{{ formatTime(order.createTime) }}</text>
			</view>
			<view class="info-row" v-if="order.payTime">
				<text class="label">支付时间</text>
				<text class="value">{{ formatTime(order.payTime) }}</text>
			</view>
		</view>

		<!-- 办理进度 -->
		<view class="section" v-if="order.progressList && order.progressList.length">
			<view class="section-title">办理进度</view>
			<view class="timeline">
				<view class="timeline-item" v-for="(item, idx) in order.progressList" :key="item.id">
					<view class="timeline-dot" :class="{ active: idx === 0 }"></view>
					<view class="timeline-line" v-if="idx < order.progressList.length - 1"></view>
					<view class="timeline-content">
						<view class="timeline-header">
							<text class="timeline-title">{{ item.title }}</text>
							<text class="timeline-time">{{ formatTime(item.createTime) }}</text>
						</view>
						<text class="timeline-desc" v-if="item.content">{{ item.content }}</text>
						<!-- 进度关联的材料（办理人员上传） -->
						<view class="progress-materials" v-if="item.materials && item.materials.length">
							<view class="progress-material-item" v-for="mat in item.materials" :key="mat.id" @click="previewFile(mat)">
								<image class="progress-material-icon" :src="getFileIcon(mat.fileUrl)" mode="aspectFill"></image>
								<text class="progress-material-name">{{ mat.name }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单材料 -->
		<view class="section">
			<view class="section-header">
				<text class="section-title">订单材料</text>
				<view class="upload-btn" @click="uploadMaterial" v-if="canUpload">
					<text>上传材料</text>
				</view>
			</view>
			<!-- 所需材料提示 -->
			<view class="required-materials" v-if="order.requiredMaterials && canUpload">
				<view class="required-title">
					<text>所需材料</text>
				</view>
				<view class="required-content">{{ order.requiredMaterials }}</view>
			</view>
			<view class="material-list" v-if="order.materialList && order.materialList.length">
				<view class="material-item" v-for="item in order.materialList" :key="item.id" @click="previewFile(item)">
					<image class="material-icon" :src="getFileIcon(item.fileUrl)" mode="aspectFit"></image>
					<view class="material-info">
						<text class="material-name">{{ item.name }}</text>
						<text class="material-time">{{ formatTime(item.uploadTime) }}</text>
					</view>
					<view class="material-del" @click.stop="deleteMaterial(item)" v-if="canUpload">
						<text>删除</text>
					</view>
				</view>
			</view>
			<view class="empty-tip" v-else-if="!order.requiredMaterials">
				<text>暂无材料</text>
			</view>
		</view>

		<!-- 底部操作 -->
		<view class="bottom-bar" v-if="order.status === 0 || order.status === 1 || order.status === 3">
			<view class="btn-cancel" @click="cancelOrder" v-if="order.status === 0">取消订单</view>
			<view class="btn-invoice" @click="applyInvoice" v-if="canApplyInvoice">申请发票</view>
			<view class="btn-pay" @click="goPay" v-if="order.status === 0">去支付</view>
			<view class="btn-refund" @click="applyRefund" v-if="order.status === 1">申请退款</view>
		</view>
	</view>
</template>

<script>
import { orderApi, invoiceApi } from '@/api/index.js'
import { uploadFile } from '@/utils/request.js'
import { getFullImageUrl } from '@/utils/image.js'

export default {
	data() {
		return {
			orderId: null,
			order: {},
			canApplyInvoice: false
		}
	},
	computed: {
		canUpload() {
			return [0, 1, 2].includes(this.order.status)
		}
	},
	onLoad(options) {
		this.orderId = options.id
		// 检查是否已登录
		const token = uni.getStorageSync('token')
		if (token) {
			this.loadDetail()
		} else {
			uni.showToast({ title: '请先登录', icon: 'none' })
			setTimeout(() => {
				uni.switchTab({ url: '/pages/mine/mine' })
			}, 1500)
		}
	},
	onPullDownRefresh() {
		this.loadDetail().finally(() => uni.stopPullDownRefresh())
	},
	methods: {
		async loadDetail() {
			try {
				const res = await orderApi.getOrderDetail(this.orderId)
				this.order = res.data || {}
				// 检查是否可以申请发票
				this.checkInvoice()
			} catch (e) {
				console.error('加载订单详情失败', e)
			}
		},
		async checkInvoice() {
			// 只有已支付或已完成的订单才检查
			if ([1, 3].includes(this.order.status)) {
				try {
					const res = await invoiceApi.checkCanApply(this.orderId)
					this.canApplyInvoice = res.data?.canApply || false
				} catch (e) {
					this.canApplyInvoice = false
				}
			}
		},
		applyInvoice() {
			uni.navigateTo({
				url: `/pages/invoice/apply?orderId=${this.orderId}&orderNo=${this.order.orderNo}&amount=${this.order.payAmount || this.order.amount}`
			})
		},
		getStatusDesc() {
			const map = {
				0: '请尽快完成支付',
				1: '已支付，等待办理',
				2: '正在为您办理中',
				3: '办理已完成',
				4: '退款申请处理中',
				5: '订单已取消'
			}
			return map[this.order.status] || ''
		},
		formatTime(time) {
			if (!time) return ''
			return time.replace('T', ' ').substring(0, 16)
		},
		getFileIcon(url) {
			if (!url) return '/static/picture.png'
			const ext = url.split('.').pop().toLowerCase()
			if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)) {
				return getFullImageUrl(url)
			}
			return '/static/picture.png'
		},
		previewFile(item) {
			const fullUrl = getFullImageUrl(item.fileUrl)
			const ext = item.fileUrl.split('.').pop().toLowerCase()
			if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)) {
				uni.previewImage({ urls: [fullUrl] })
			} else {
				uni.downloadFile({
					url: fullUrl,
					success: (res) => {
						uni.openDocument({ filePath: res.tempFilePath })
					}
				})
			}
		},
		uploadMaterial() {
			uni.chooseMessageFile({
				count: 1,
				type: 'all',
				success: async (res) => {
					const file = res.tempFiles[0]
					uni.showLoading({ title: '上传中...' })
					try {
						const uploadRes = await uploadFile({
							url: '/common/upload',
							filePath: file.path,
							name: 'file'
						})
						// 保存材料
						await orderApi.uploadMaterial(this.orderId, {
							name: file.name,
							fileUrl: uploadRes.data.url
						})
						uni.hideLoading()
						uni.showToast({ title: '上传成功', icon: 'success' })
						this.loadDetail()
					} catch (e) {
						uni.hideLoading()
						console.error('上传失败', e)
					}
				}
			})
		},
		async deleteMaterial(item) {
			uni.showModal({
				title: '提示',
				content: '确定删除该材料？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await orderApi.deleteMaterial(this.orderId, item.id)
							uni.showToast({ title: '删除成功', icon: 'success' })
							this.loadDetail()
						} catch (e) {
							console.error('删除失败', e)
						}
					}
				}
			})
		},
		async goPay() {
			uni.showLoading({ title: '发起支付...' })
			try {
				const res = await orderApi.prepay(this.orderId)
				uni.hideLoading()
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
						this.loadDetail()
					},
					fail: (err) => {
						if (err.errMsg !== 'requestPayment:fail cancel') {
							uni.showToast({ title: '支付失败', icon: 'none' })
						}
					}
				})
			} catch (e) {
				uni.hideLoading()
			}
		},
		cancelOrder() {
			uni.showModal({
				title: '提示',
				content: '确定取消该订单？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await orderApi.cancelOrder(this.orderId)
							uni.showToast({ title: '取消成功', icon: 'success' })
							this.loadDetail()
						} catch (e) {
							console.error('取消失败', e)
						}
					}
				}
			})
		},
		applyRefund() {
			uni.showModal({
				title: '申请退款',
				editable: true,
				placeholderText: '请输入退款原因',
				success: async (res) => {
					if (res.confirm && res.content) {
						try {
							await orderApi.applyRefund(this.orderId, res.content)
							uni.showToast({ title: '申请已提交', icon: 'success' })
							this.loadDetail()
						} catch (e) {
							console.error('申请退款失败', e)
						}
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
	padding-bottom: 140rpx;
}

/* 状态卡片 */
.status-card {
	padding: 40rpx 30rpx;
	color: #fff;
}
.status-bg-0 { background: linear-gradient(135deg, #ff9800, #f57c00); }
.status-bg-1 { background: linear-gradient(135deg, #1976d2, #1565c0); }
.status-bg-2 { background: linear-gradient(135deg, #1976d2, #1565c0); }
.status-bg-3 { background: linear-gradient(135deg, #4caf50, #388e3c); }
.status-bg-4 { background: linear-gradient(135deg, #9c27b0, #7b1fa2); }
.status-bg-5 { background: linear-gradient(135deg, #9e9e9e, #757575); }
.status-text {
	font-size: 36rpx;
	font-weight: bold;
	display: block;
}
.status-desc {
	font-size: 26rpx;
	margin-top: 12rpx;
	opacity: 0.9;
}

/* 区块 */
.section {
	margin: 20rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}
.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 20rpx;
}
.section-header .section-title {
	margin-bottom: 0;
}
.upload-btn {
	padding: 8rpx 20rpx;
	background: #d11a1a;
	color: #fff;
	font-size: 24rpx;
	border-radius: 20rpx;
}

/* 信息行 */
.info-row {
	display: flex;
	justify-content: space-between;
	padding: 16rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}
.info-row:last-child {
	border-bottom: none;
}
.label {
	font-size: 28rpx;
	color: #666;
}
.value {
	font-size: 28rpx;
	color: #333;
}
.value.price {
	color: #d11a1a;
	font-weight: bold;
}
.value.discount {
	color: #4caf50;
}

/* 时间线 */
.timeline {
	position: relative;
}
.timeline-item {
	display: flex;
	position: relative;
	padding-bottom: 30rpx;
}
.timeline-item:last-child {
	padding-bottom: 0;
}
.timeline-dot {
	width: 20rpx;
	height: 20rpx;
	border-radius: 50%;
	background: #ddd;
	margin-right: 20rpx;
	margin-top: 6rpx;
	flex-shrink: 0;
}
.timeline-dot.active {
	background: #d11a1a;
}
.timeline-line {
	position: absolute;
	left: 9rpx;
	top: 30rpx;
	width: 2rpx;
	height: calc(100% - 20rpx);
	background: #eee;
}
.timeline-content {
	flex: 1;
}
.timeline-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.timeline-title {
	font-size: 28rpx;
	color: #333;
	font-weight: bold;
}
.timeline-time {
	font-size: 24rpx;
	color: #999;
}
.timeline-desc {
	font-size: 26rpx;
	color: #666;
	margin-top: 8rpx;
}

/* 进度关联材料 */
.progress-materials {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
	margin-top: 16rpx;
}
.progress-material-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
	padding: 8rpx 16rpx;
	background: #f5f5f5;
	border-radius: 8rpx;
}
.progress-material-icon {
	width: 48rpx;
	height: 48rpx;
	border-radius: 6rpx;
}
.progress-material-name {
	font-size: 24rpx;
	color: #666;
	max-width: 200rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 材料列表 */
.required-materials {
	background: #fff8e1;
	border-radius: 12rpx;
	padding: 20rpx;
	margin-bottom: 20rpx;
	border: 1rpx solid #ffe082;
}
.required-title {
	display: flex;
	align-items: center;
	gap: 8rpx;
	font-size: 28rpx;
	font-weight: bold;
	color: #f57c00;
	margin-bottom: 12rpx;
}
.required-icon {
	font-size: 32rpx;
}
.required-content {
	font-size: 26rpx;
	color: #666;
	line-height: 1.8;
	white-space: pre-wrap;
}
.material-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}
.material-item {
	display: flex;
	align-items: center;
	padding: 16rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
}
.material-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 8rpx;
	margin-right: 16rpx;
}
.material-info {
	flex: 1;
}
.material-name {
	font-size: 28rpx;
	color: #333;
	display: block;
}
.material-time {
	font-size: 24rpx;
	color: #999;
}
.material-del {
	padding: 8rpx 16rpx;
	color: #1976d2;
	font-size: 24rpx;
}
.empty-tip {
	text-align: center;
	padding: 40rpx;
	color: #999;
	font-size: 28rpx;
}

/* 底部操作栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: flex-end;
	gap: 20rpx;
	padding: 20rpx 30rpx;
	background: #fff;
	box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.btn-cancel, .btn-refund {
	padding: 20rpx 40rpx;
	background: #fff;
	color: #666;
	font-size: 28rpx;
	border-radius: 40rpx;
	border: 1rpx solid #ddd;
}
.btn-invoice {
	padding: 20rpx 40rpx;
	background: #fff;
	color: #d11a1a;
	font-size: 28rpx;
	border-radius: 40rpx;
	border: 1rpx solid #d11a1a;
}
.btn-pay {
	padding: 20rpx 60rpx;
	background: #d11a1a;
	color: #fff;
	font-size: 28rpx;
	border-radius: 40rpx;
}
</style>
