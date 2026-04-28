<template>
	<!-- 微信小程序插屏广告组件（无UI，通过方法调用显示） -->
	<view class="ad-interstitial"></view>
</template>

<script>
export default {
	name: 'AdInterstitial',
	props: {
		adUnitId: {
			type: String,
			default: ''
		},
		// 是否自动显示
		autoShow: {
			type: Boolean,
			default: false
		}
	},
	data() {
		return {
			interstitialAd: null
		}
	},
	mounted() {
		this.initInterstitialAd()
		if (this.autoShow) {
			this.showAd()
		}
	},
	methods: {
		// 初始化插屏广告
		initInterstitialAd() {
			// #ifdef MP-WEIXIN
			if (wx.createInterstitialAd) {
				this.interstitialAd = wx.createInterstitialAd({
					adUnitId: this.adUnitId || 'adunit-xxxxxxxxxx'
				})
				
				this.interstitialAd.onLoad(() => {
					console.log('插屏广告加载成功')
					this.$emit('load')
				})
				
				this.interstitialAd.onError((err) => {
					console.error('插屏广告加载失败', err)
					this.$emit('error', err)
				})
				
				this.interstitialAd.onClose(() => {
					this.$emit('close')
				})
			}
			// #endif
		},
		
		// 显示插屏广告
		showAd() {
			// #ifdef MP-WEIXIN
			if (this.interstitialAd) {
				this.interstitialAd.show().catch((err) => {
					console.error('插屏广告显示失败', err)
				})
			}
			// #endif
		}
	},
	beforeDestroy() {
		// #ifdef MP-WEIXIN
		if (this.interstitialAd) {
			this.interstitialAd.destroy()
		}
		// #endif
	}
}
</script>

<style lang="scss" scoped>
.ad-interstitial {
	display: none;
}
</style>
