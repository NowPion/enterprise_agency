<template>
	<view class="container">
		<!-- 订单信息 -->
		<view class="order-info">
			<view class="order-row">
				<text class="label">订单编号</text>
				<text class="value">{{ orderNo }}</text>
			</view>
			<view class="order-row">
				<text class="label">开票金额</text>
				<text class="value price">¥{{ amount }}</text>
			</view>
		</view>

		<!-- 发票类型 -->
		<view class="section">
			<view class="section-title">发票类型</view>
			<view class="type-options">
				<view class="type-item" :class="{ active: form.type === 1 }" @click="form.type = 1">
					<text>普通发票</text>
				</view>
				<view class="type-item" :class="{ active: form.type === 2 }" @click="form.type = 2">
					<text>增值税专用发票</text>
				</view>
			</view>
		</view>

		<!-- 抬头类型 -->
		<view class="section">
			<view class="section-title">抬头类型</view>
			<view class="type-options">
				<view class="type-item" :class="{ active: form.titleType === 1 }" @click="form.titleType = 1">
					<text>个人</text>
				</view>
				<view class="type-item" :class="{ active: form.titleType === 2 }" @click="form.titleType = 2">
					<text>企业</text>
				</view>
			</view>
		</view>

		<!-- 发票信息 -->
		<view class="section">
			<view class="section-title">发票信息</view>
			<view class="form-item">
				<text class="form-label">发票抬头</text>
				<input class="form-input" v-model="form.title" :placeholder="form.titleType === 1 ? '请输入个人姓名' : '请输入企业名称'" />
			</view>
			<view class="form-item" v-if="form.titleType === 2">
				<text class="form-label">税号</text>
				<input class="form-input" v-model="form.taxNo" placeholder="请输入纳税人识别号" />
			</view>
			<view class="form-item">
				<text class="form-label">接收邮箱</text>
				<input class="form-input" v-model="form.email" placeholder="请输入接收发票的邮箱" type="text" />
			</view>
		</view>

		<!-- 提交按钮 -->
		<view class="submit-btn" @click="submitApply">
			<text>提交申请</text>
		</view>
	</view>
</template>

<script>
import { invoiceApi } from '@/api/index.js'

export default {
	data() {
		return {
			orderId: null,
			orderNo: '',
			amount: 0,
			form: {
				type: 1,
				titleType: 2,
				title: '',
				taxNo: '',
				email: ''
			}
		}
	},
	onLoad(options) {
		this.orderId = options.orderId
		this.orderNo = options.orderNo || ''
		this.amount = options.amount || 0
	},
	methods: {
		async submitApply() {
			if (!this.form.title) {
				uni.showToast({ title: '请输入发票抬头', icon: 'none' })
				return
			}
			if (this.form.titleType === 2 && !this.form.taxNo) {
				uni.showToast({ title: '请输入税号', icon: 'none' })
				return
			}
			if (!this.form.email) {
				uni.showToast({ title: '请输入接收邮箱', icon: 'none' })
				return
			}
			// 简单邮箱格式验证
			if (!/^[\w.-]+@[\w.-]+\.\w+$/.test(this.form.email)) {
				uni.showToast({ title: '邮箱格式不正确', icon: 'none' })
				return
			}

			uni.showLoading({ title: '提交中...' })
			try {
				await invoiceApi.applyInvoice({
					orderId: this.orderId,
					type: this.form.type,
					titleType: this.form.titleType,
					title: this.form.title,
					taxNo: this.form.taxNo,
					email: this.form.email
				})
				uni.hideLoading()
				uni.showToast({ title: '申请成功', icon: 'success' })
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			} catch (e) {
				uni.hideLoading()
				console.error('申请发票失败', e)
			}
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

.order-info {
	background: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}
.order-row {
	display: flex;
	justify-content: space-between;
	padding: 12rpx 0;
}
.order-row .label {
	color: #666;
	font-size: 28rpx;
}
.order-row .value {
	color: #333;
	font-size: 28rpx;
}
.order-row .value.price {
	color: #1976d2;
	font-weight: bold;
}

.section {
	background: #fff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}
.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 24rpx;
}

.type-options {
	display: flex;
	gap: 20rpx;
}
.type-item {
	flex: 1;
	padding: 24rpx;
	text-align: center;
	background: #f5f5f5;
	border-radius: 12rpx;
	font-size: 28rpx;
	color: #666;
	border: 2rpx solid transparent;
}
.type-item.active {
	background: #e3f2fd;
	color: #1976d2;
	border-color: #1976d2;
}

.form-item {
	display: flex;
	align-items: center;
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}
.form-item:last-child {
	border-bottom: none;
}
.form-label {
	width: 160rpx;
	font-size: 28rpx;
	color: #333;
	flex-shrink: 0;
}
.form-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.submit-btn {
	position: fixed;
	bottom: 40rpx;
	left: 30rpx;
	right: 30rpx;
	background: #1976d2;
	color: #fff;
	text-align: center;
	padding: 28rpx;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
}
</style>
