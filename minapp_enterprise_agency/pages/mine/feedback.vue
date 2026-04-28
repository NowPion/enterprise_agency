<template>
	<view class="container">
		<view class="form-section">
			<view class="form-item">
				<text class="label">反馈类型</text>
				<picker :range="typeOptions" @change="onTypeChange">
					<view class="picker-value">
						<text>{{ typeOptions[typeIndex] }}</text>
						<text class="picker-arrow">›</text>
					</view>
				</picker>
			</view>
			
			<view class="form-item">
				<text class="label">反馈内容</text>
				<textarea 
					class="textarea" 
					v-model="content" 
					placeholder="请详细描述您的问题或建议..."
					maxlength="500"
				></textarea>
				<text class="word-count">{{ content.length }}/500</text>
			</view>
			
			<view class="form-item">
				<text class="label">联系方式（选填）</text>
				<input class="input" v-model="contact" placeholder="手机号或微信号，方便我们联系您" />
			</view>
		</view>
		
		<button class="submit-btn" :disabled="!canSubmit" @click="handleSubmit">提交反馈</button>
		
		<view class="tips">
			<text class="tips-title">温馨提示</text>
			<text class="tips-text">1. 请详细描述您遇到的问题或建议</text>
			<text class="tips-text">2. 我们会尽快处理您的反馈</text>
			<text class="tips-text">3. 如有紧急问题，请拨打客服电话</text>
		</view>
	</view>
</template>

<script>
import { feedbackApi } from '@/api/index.js'

export default {
	data() {
		return {
			typeOptions: ['功能建议', '问题反馈', '投诉', '其他'],
			typeIndex: 0,
			content: '',
			contact: ''
		}
	},
	computed: {
		canSubmit() {
			return this.content.trim().length >= 10
		}
	},
	methods: {
		onTypeChange(e) {
			this.typeIndex = e.detail.value
		},
		async handleSubmit() {
			if (!this.canSubmit) {
				uni.showToast({ title: '请输入至少10个字的反馈内容', icon: 'none' })
				return
			}
			
			uni.showLoading({ title: '提交中...' })
			try {
				const res = await feedbackApi.submitFeedback({
					type: this.typeOptions[this.typeIndex],
					content: this.content.trim(),
					contact: this.contact.trim()
				})
				uni.hideLoading()
				if (res.code === 0) {
					uni.showToast({ title: '提交成功，感谢您的反馈', icon: 'success' })
					setTimeout(() => uni.navigateBack(), 1500)
				} else {
					uni.showToast({ title: res.message || '提交失败', icon: 'none' })
				}
			} catch (e) {
				uni.hideLoading()
				uni.showToast({ title: '提交失败，请稍后重试', icon: 'none' })
			}
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
.form-section {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.form-item {
	margin-bottom: 30rpx;
}
.form-item:last-child {
	margin-bottom: 0;
}
.label {
	font-size: 28rpx;
	color: #333;
	font-weight: bold;
	margin-bottom: 16rpx;
	display: block;
}
.picker-value {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 24rpx;
	background: #f8f8f8;
	border-radius: 8rpx;
	font-size: 28rpx;
	color: #333;
}
.picker-arrow {
	color: #999;
	font-size: 28rpx;
}
.textarea {
	width: 100%;
	height: 240rpx;
	padding: 20rpx;
	background: #f8f8f8;
	border-radius: 8rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}
.word-count {
	text-align: right;
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
	display: block;
}
.input {
	padding: 20rpx 24rpx;
	background: #f8f8f8;
	border-radius: 8rpx;
	font-size: 28rpx;
}
.submit-btn {
	margin-top: 40rpx;
	background: #e53935;
	color: #fff;
	border-radius: 48rpx;
	height: 96rpx;
	line-height: 96rpx;
	font-size: 32rpx;
}
.submit-btn[disabled] {
	background: #ccc;
}
.tips {
	margin-top: 40rpx;
	padding: 24rpx;
}
.tips-title {
	font-size: 26rpx;
	color: #666;
	font-weight: bold;
	margin-bottom: 16rpx;
	display: block;
}
.tips-text {
	font-size: 24rpx;
	color: #999;
	line-height: 40rpx;
	display: block;
}
</style>
