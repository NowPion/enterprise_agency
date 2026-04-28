<template>
	<!-- 微信小程序Banner广告组件 -->
	<view class="ad-banner-wrapper" v-if="show && adUnitId">
		<!-- #ifdef MP-WEIXIN -->
		<ad 
			unit-id="adunit-xxxxxxxxxx"
			ad-type="banner"
			ad-theme="white"
			@load="onAdLoad"
			@error="onAdError"
			@close="onAdClose"
		></ad>
		<!-- #endif -->
		<!-- #ifndef MP-WEIXIN -->
		<view class="ad-placeholder" v-if="showPlaceholder">
			<text class="ad-text">广告位</text>
		</view>
		<!-- #endif -->
	</view>
</template>

<script>
export default {
	name: 'AdBanner',
	props: {
		// 广告单元ID（从后台配置获取）
		adUnitId: {
			type: String,
			default: ''
		},
		// 是否显示
		show: {
			type: Boolean,
			default: true
		},
		// 非微信环境是否显示占位
		showPlaceholder: {
			type: Boolean,
			default: false
		}
	},
	methods: {
		onAdLoad() {
			this.$emit('load')
		},
		onAdError(e) {
			console.error('Banner广告加载失败', e)
			this.$emit('error', e)
		},
		onAdClose() {
			this.$emit('close')
		}
	}
}
</script>

<style lang="scss" scoped>
.ad-banner-wrapper {
	width: 100%;
	min-height: 100rpx;
}

.ad-placeholder {
	background: #f5f5f5;
	height: 150rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 12rpx;
	margin: 20rpx 30rpx;
}

.ad-text {
	color: #999;
	font-size: 24rpx;
}
</style>
