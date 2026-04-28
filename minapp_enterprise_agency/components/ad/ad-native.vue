<template>
	<!-- 微信小程序原生模板广告组件 -->
	<view class="ad-native-wrapper" v-if="show">
		<!-- #ifdef MP-WEIXIN -->
		<ad-custom 
			unit-id="adunit-xxxxxxxxxx"
			@load="onAdLoad"
			@error="onAdError"
		></ad-custom>
		<!-- #endif -->
		<!-- #ifndef MP-WEIXIN -->
		<view class="ad-native-placeholder" v-if="showPlaceholder">
			<view class="placeholder-content">
				<view class="placeholder-left">
					<view class="placeholder-icon"></view>
				</view>
				<view class="placeholder-right">
					<text class="placeholder-title">广告</text>
					<text class="placeholder-desc">原生广告位</text>
				</view>
			</view>
		</view>
		<!-- #endif -->
	</view>
</template>

<script>
export default {
	name: 'AdNative',
	props: {
		adUnitId: {
			type: String,
			default: ''
		},
		show: {
			type: Boolean,
			default: true
		},
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
			console.error('原生广告加载失败', e)
			this.$emit('error', e)
		}
	}
}
</script>

<style lang="scss" scoped>
.ad-native-wrapper {
	margin: 20rpx 30rpx;
}

.ad-native-placeholder {
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
	box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.05);
}

.placeholder-content {
	display: flex;
	align-items: center;
}

.placeholder-left {
	margin-right: 20rpx;
}

.placeholder-icon {
	width: 120rpx;
	height: 120rpx;
	background: #f5f5f5;
	border-radius: 12rpx;
}

.placeholder-right {
	flex: 1;
	display: flex;
	flex-direction: column;
}

.placeholder-title {
	font-size: 28rpx;
	color: #333;
	font-weight: bold;
}

.placeholder-desc {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}
</style>
