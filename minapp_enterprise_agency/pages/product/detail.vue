<template>
	<view class="container">
		<!-- 产品图片 -->
		<image class="product-img" :src="product.image || '/static/logo.png'" mode="aspectFill"></image>
		
		<!-- 产品信息 -->
		<view class="product-info">
			<view class="price-row">
				<text class="price">¥{{ product.price }}</text>
				<text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
			</view>
			<text class="product-name">{{ product.name }}</text>
			<text class="product-desc">{{ product.description }}</text>
			<view class="tags" v-if="product.processDays">
				<text class="tag">预计{{ product.processDays }}个工作日</text>
			</view>
		</view>
		
		<!-- 服务说明 -->
		<view class="section" v-if="product.notice">
			<view class="section-title">服务说明</view>
			<view class="section-content">
				<text>{{ product.notice }}</text>
			</view>
		</view>
		
		<!-- 所需材料 -->
		<view class="section" v-if="product.materials">
			<view class="section-title">所需材料</view>
			<view class="section-content">
				<text>{{ product.materials }}</text>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="bottom-bar">
			<view class="btn-order" @click="goOrder">立即办理</view>
		</view>
	</view>
</template>

<script>
import { productApi } from '@/api/index.js'

export default {
	data() {
		return {
			productId: null,
			product: {}
		}
	},
	onLoad(options) {
		this.productId = options.id
		this.loadProduct()
	},
	methods: {
		async loadProduct() {
			try {
				const res = await productApi.getProductDetail(this.productId)
				this.product = res.data || {}
				// 设置页面标题
				if (this.product.name) {
					uni.setNavigationBarTitle({ title: this.product.name })
				}
			} catch (e) {
				console.error('加载产品详情失败', e)
			}
		},
		goOrder() {
			uni.navigateTo({ url: `/pages/order/create?productId=${this.productId}` })
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 120rpx;
}

.product-img {
	width: 100%;
	height: 500rpx;
}

.product-info {
	background: #fff;
	padding: 24rpx;
	margin-bottom: 20rpx;
}
.price-row {
	display: flex;
	align-items: baseline;
	gap: 16rpx;
	margin-bottom: 16rpx;
}
.price {
	font-size: 48rpx;
	font-weight: bold;
	color: #1976d2;
}
.original-price {
	font-size: 28rpx;
	color: #999;
	text-decoration: line-through;
}
.product-name {
	font-size: 34rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 12rpx;
}
.product-desc {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}
.tags {
	display: flex;
	gap: 16rpx;
	margin-top: 16rpx;
}
.tag {
	font-size: 24rpx;
	color: #1976d2;
	background: #e3f2fd;
	padding: 8rpx 16rpx;
	border-radius: 8rpx;
}

.section {
	background: #fff;
	padding: 24rpx;
	margin-bottom: 20rpx;
}
.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
	padding-left: 16rpx;
	border-left: 6rpx solid #1976d2;
}
.section-content {
	font-size: 28rpx;
	color: #666;
	line-height: 1.8;
	white-space: pre-wrap;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx 30rpx;
	background: #fff;
	box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.btn-order {
	width: 100%;
	height: 88rpx;
	background: #1976d2;
	color: #fff;
	font-size: 32rpx;
	border-radius: 44rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}
</style>
