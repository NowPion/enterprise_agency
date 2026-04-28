<script>
	import { commonApi } from '@/api/index.js'
	
	export default {
		onLaunch: function() {
			console.log('App Launch')
			// 检查版本更新
			//this.checkUpdate()
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		methods: {
			async checkUpdate() {
				// #ifdef MP-WEIXIN
				try {
					const accountInfo = uni.getAccountInfoSync()
					const version = accountInfo.miniProgram.version || '1.0.0'
					
					const res = await commonApi.checkUpdate({
						version: version,
						platform: 'mp-weixin'
					})
					
					if (res.code === 0 && res.data.hasUpdate) {
						const { forceUpdate, content, version: newVersion } = res.data
						
						uni.showModal({
							title: `发现新版本 v${newVersion}`,
							content: content || '有新版本可用，是否更新？',
							showCancel: !forceUpdate,
							confirmText: '更新',
							success: (modalRes) => {
								if (modalRes.confirm) {
									// 微信小程序使用更新管理器
									const updateManager = uni.getUpdateManager()
									updateManager.onCheckForUpdate((checkRes) => {
										if (checkRes.hasUpdate) {
											updateManager.onUpdateReady(() => {
												updateManager.applyUpdate()
											})
											updateManager.onUpdateFailed(() => {
												uni.showToast({ title: '更新失败，请删除小程序重新打开', icon: 'none' })
											})
										}
									})
								}
							}
						})
					}
				} catch (e) {
					console.error('检查更新失败', e)
				}
				// #endif
			}
		}
	}
</script>

<style>
	/*每个页面公共css */
</style>
