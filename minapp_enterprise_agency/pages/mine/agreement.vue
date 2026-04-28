<template>
	<view class="container">
		<view class="content" v-if="content">
			<rich-text :nodes="content"></rich-text>
		</view>
		<view class="loading" v-else-if="loading">
			<text>加载中...</text>
		</view>
		<view class="empty" v-else>
			<text>暂无内容</text>
		</view>
	</view>
</template>

<script>
import { contentApi } from '@/api/index.js'

export default {
	data() {
		return {
			content: '',
			loading: true
		}
	},
	onLoad() {
		this.loadContent()
	},
	methods: {
		async loadContent() {
			try {
				const res = await contentApi.getAgreement()
				if (res.code === 0) {
					this.content = res.data?.content || ''
				}
			} catch (e) {
				console.error('加载用户协议失败', e)
			} finally {
				this.loading = false
			}
		}
	}
}
</script>

<style scoped>
.container {
	min-height: 100vh;
	background: #fff;
	padding: 30rpx;
}
.content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.8;
}
.loading, .empty {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 300rpx;
	color: #999;
	font-size: 28rpx;
}
</style>
