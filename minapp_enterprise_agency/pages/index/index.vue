<template>
	<view class="container">
		<!-- 滚动公告 -->
		<view class="notice-bar" v-if="notices.length">
			<image class="notice-icon" src="/static/announcement.png" mode="aspectFit"></image>
			<view class="notice-content">
				<text class="notice-text notice-scroll">{{ noticeText }}</text>
			</view>
		</view>

		<!-- 轮播图 -->
		<swiper class="banner-swiper" indicator-dots autoplay circular :interval="4000" :previous-margin="20" :next-margin="20">
			<swiper-item v-for="item in banners" :key="item.id" class="banner-item">
				<view class="banner-item-inner">
					<image class="banner-img" :src="item.image" mode="aspectFill" @click="onBannerClick(item)"></image>
				</view>
			</swiper-item>
		</swiper>

		<!-- 办理业务区域 -->
		<view class="section">
			<view class="section-title">办理业务</view>
			<view class="product-list">
				<view class="product-item" v-for="item in products" :key="item.id" @click="goProduct(item)">
					<view class="product-info">
						<text class="product-name">{{ item.name }}</text>
						<text class="product-desc">{{ item.description || '专业代办服务' }}</text>
						<view class="product-price">
							<text class="price">¥{{ item.price }}</text>
							<text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
						</view>
					</view>
					<view class="product-btn">立即办理</view>
				</view>
			</view>
		</view>

		<!-- 办理动态区域 -->
		<view class="section" v-if="dynamics.length">
			<view class="section-title">办理实时动态</view>
			<view class="dynamics-list">
				<view class="dynamics-item" v-for="(item, index) in dynamics" :key="index">
					<text class="dynamics-text">
						尾号{{ item.phoneTail }}用户 申请了 {{ item.companyName }} {{ item.productName }}
					</text>
				</view>
			</view>
		</view>

		<!-- 常见问题区域 -->
		<view class="section" v-if="faqs.length">
			<view class="section-title">常见问题</view>
			<view class="faq-list">
				<view class="faq-item" v-for="item in faqs" :key="item.id">
					<view class="faq-question">
						<text class="faq-q-text">{{ item.question || item.title }}</text>
					</view>
					<view class="faq-answer">
						<text class="faq-a-text">{{ item.answer || item.content }}</text>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { homeApi, productApi, contentApi } from '@/api/index.js'
import { getFullImageUrl } from '@/utils/image.js'

export default {
	data() {
		return {
			notices: [],
			banners: [],
			products: [],
			dynamics: [],
			faqs: [],
			dynamicsTimer: null,
			dynamicsPage: 0
		}
	},
	computed: {
		noticeText() {
			return this.notices.map(n => n.content || n.title).join('　　　　')
		}
	},
	onLoad() {
		this.loadData()
		this.startDynamicsTimer()
	},
	onShow() {
		this.startDynamicsTimer()
	},
	onHide() {
		this.stopDynamicsTimer()
	},
	onUnload() {
		this.stopDynamicsTimer()
	},
	onPullDownRefresh() {
		this.loadData().finally(() => uni.stopPullDownRefresh())
	},
	methods: {
		startDynamicsTimer() {
			if (this.dynamicsTimer) return
			this.dynamicsTimer = setInterval(() => {
				this.loadDynamics()
			}, 5000)
		},
		stopDynamicsTimer() {
			if (this.dynamicsTimer) {
				clearInterval(this.dynamicsTimer)
				this.dynamicsTimer = null
			}
		},
		async loadData() {
			await Promise.all([
				this.loadNotices(),
				this.loadBanners(),
				this.loadProducts(),
				this.loadDynamics(),
				this.loadFaqs()
			])
		},
		async loadNotices() {
			try {
				const res = await contentApi.getNoticeList({ page: 1, size: 5 })
				this.notices = res.data?.list || res.data || []
			} catch (e) { console.error('加载公告失败', e) }
		},
		async loadBanners() {
			try {
				const res = await homeApi.getBannerList()
				// 处理图片URL，后端返回的是imageUrl字段
				const list = res.data || []
				this.banners = list.map(item => ({
					...item,
					image: getFullImageUrl(item.imageUrl || item.image)
				}))
			} catch (e) { console.error('加载轮播图失败', e) }
		},
		async loadProducts() {
			try {
				const res = await productApi.getProductList({ page: 1, size: 20 })
				const list = res.data?.list || res.data || []
				// 处理产品图片URL
				this.products = list.map(item => ({
					...item,
					image: getFullImageUrl(item.image || item.icon)
				}))
			} catch (e) { console.error('加载产品失败', e) }
		},
		async loadDynamics() {
			try {
				const res = await contentApi.getDynamics(this.dynamicsPage, 7)
				const data = res.data || []
				if (data.length > 0) {
					this.dynamics = data
					this.dynamicsPage++
				} else {
					// 没有更多数据，重置到第一页
					this.dynamicsPage = 0
					const res2 = await contentApi.getDynamics(0, 7)
					this.dynamics = res2.data || []
				}
			} catch (e) { console.error('加载动态失败', e) }
		},
		async loadFaqs() {
			try {
				const res = await contentApi.getFaqList({ page: 1, size: 10 })
				this.faqs = res.data?.list || res.data || []
			} catch (e) { console.error('加载FAQ失败', e) }
		},
		onBannerClick(item) {
			if (item.linkType === 1 && item.linkUrl) {
				uni.navigateTo({ url: item.linkUrl })
			} else if (item.linkType === 3 && item.linkUrl) {
				uni.navigateTo({ url: `/pages/webview/index?url=${encodeURIComponent(item.linkUrl)}` })
			}
		},
		goProduct(item) {
			uni.navigateTo({ url: `/pages/order/create?productId=${item.id}` })
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #f5f5f5;
	padding-bottom: 20rpx;
}

/* 公告栏 */
.notice-bar {
	display: flex;
	align-items: center;
	background: linear-gradient(90deg, #fff8e6 0%, #fff 100%);
	padding: 16rpx 24rpx;
	margin: 20rpx;
	border-radius: 12rpx;
	box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
	overflow: hidden;
}
.notice-icon {
	width: 36rpx;
	height: 36rpx;
	margin-right: 16rpx;
	flex-shrink: 0;
}
.notice-content {
	flex: 1;
	overflow: hidden;
}
.notice-text {
	font-size: 26rpx;
	color: #d11a1a;
	line-height: 40rpx;
	white-space: nowrap;
	display: inline-block;
}
.notice-scroll {
	animation: scroll-left 20s linear infinite;
}
@keyframes scroll-left {
	0% { transform: translateX(100%); }
	100% { transform: translateX(-100%); }
}

/* 轮播图 */
.banner-swiper {
	width: 100%;
	height: 340rpx;
	margin-top: 0;
}
.banner-item {
	padding: 0 10rpx;
	box-sizing: border-box;
}
.banner-item-inner {
	width: 100%;
	height: 320rpx;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
}
.banner-img {
	width: 100%;
	height: 100%;
}

/* 业务区域 */
.section {
	margin: 20rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 24rpx;
	padding-left: 16rpx;
	border-left: 6rpx solid #1976d2;
}

/* 产品列表 */
.product-list {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
}
.product-item {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: #fafafa;
	border-radius: 12rpx;
}
.product-icon {
	width: 120rpx;
	height: 120rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}
.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 8rpx;
}
.product-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
}
.product-desc {
	font-size: 24rpx;
	color: #999;
}
.product-price {
	display: flex;
	align-items: baseline;
	gap: 12rpx;
}
.price {
	font-size: 32rpx;
	font-weight: bold;
	color: #d11a1a;
}
.original-price {
	font-size: 24rpx;
	color: #999;
	text-decoration: line-through;
}
.product-btn {
	padding: 12rpx 24rpx;
	background: #d11a1a;
	color: #fff;
	font-size: 26rpx;
	border-radius: 30rpx;
}

/* 办理动态 */
.dynamics-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}
.dynamics-item {
	padding: 16rpx 20rpx;
	background: linear-gradient(90deg, #fff8f8 0%, #fff 100%);
	border-radius: 8rpx;
	
}
.dynamics-text {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
}

/* 常见问题 */
.faq-list {
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}
.faq-item {
	background: #fafafa;
	border-radius: 12rpx;
	overflow: hidden;
}
.faq-question {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx;
}
.faq-q-text {
	flex: 1;
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}
.faq-answer {
	padding: 0 24rpx 24rpx;
	border-top: 1rpx solid #eee;
}
.faq-a-text {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}
</style>
