<template>
	<view class="container">
		<!-- 产品信息 -->
		<view class="product-card">
			<image class="product-img" :src="product.image || '/static/logo.png'" mode="aspectFill"></image>
			<view class="product-info">
				<text class="product-name">{{ product.name }}</text>
				<view class="product-tags">
					<text class="tag" v-if="product.processDays">{{ product.processDays }}个工作日</text>
					<text class="tag" v-if="selectedYearPrice">{{ selectedYearPrice.year }}</text>
				</view>
				<view class="product-price">
					<text class="price">¥{{ currentPrice }}</text>
					<text class="original-price" v-if="selectedYearPrice && selectedYearPrice.originalPrice">¥{{ selectedYearPrice.originalPrice }}</text>
					<text class="original-price" v-else-if="!selectedYearPrice && product.originalPrice">¥{{ product.originalPrice }}</text>
				</view>
			</view>
		</view>

		<!-- 产品描述 -->
		<view class="info-section" v-if="product.description">
			<view class="section-title">产品描述</view>
			<view class="section-content">{{ product.description }}</view>
		</view>

		<!-- 办理须知 -->
		<view class="info-section" v-if="product.notice">
			<view class="section-title">办理须知</view>
			<view class="section-content">{{ product.notice }}</view>
		</view>

		<!-- 所需材料 -->
		<view class="info-section" v-if="product.materials">
			<view class="section-title">所需材料</view>
			<view class="section-content">{{ product.materials }}</view>
		</view>

		<!-- 年度选择 -->
		<view class="year-section" v-if="product.yearPrices && product.yearPrices.length">
			<view class="section-title">选择办理年度</view>
			<view class="year-list">
				<view 
					class="year-item" 
					:class="{ active: selectedYearPrice && selectedYearPrice.id === yp.id }"
					v-for="yp in product.yearPrices" 
					:key="yp.id"
					@click="selectYearPrice(yp)"
				>
					<text class="year-name">{{ yp.year }}</text>
					<view class="year-price-info">
						<text class="year-price">¥{{ yp.price }}</text>
						<text class="year-original" v-if="yp.originalPrice">¥{{ yp.originalPrice }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 动态表单 -->
		<view class="form-section">
			<view class="section-title">填写信息</view>
			<view class="form-item" v-for="field in product.fields" :key="field.id">
				<view class="form-label">
					<text class="required" v-if="field.isRequired">*</text>
					<text>{{ field.fieldName }}</text>
				</view>
				<!-- 企业名称搜索（自动识别：fieldKey包含company_name或fieldName包含企业名称） -->
				<view v-if="isCompanyField(field)" class="form-company">
					<view class="company-input-wrapper">
						<input class="form-input company-input" 
							:placeholder="field.placeholder || '请输入企业名称'"
							v-model="formData[field.fieldKey]" />
						<view class="search-btn" @click="openEnterpriseSearch(field)">
							<text class="search-text">搜索</text>
						</view>
					</view>
				</view>
				<!-- 文本输入 -->
				<input v-else-if="field.fieldType === 'text'" class="form-input" 
					:placeholder="field.placeholder || '请输入' + field.fieldName"
					v-model="formData[field.fieldKey]" />
				<!-- 数字输入 -->
				<input v-else-if="field.fieldType === 'number'" class="form-input" type="number"
					:placeholder="field.placeholder || '请输入' + field.fieldName"
					v-model="formData[field.fieldKey]" />
				<!-- 手机号 -->
				<input v-else-if="field.fieldType === 'phone'" class="form-input" type="number"
					:placeholder="field.placeholder || '请输入手机号'"
					v-model="formData[field.fieldKey]" maxlength="11" />
				<!-- 身份证 -->
				<input v-else-if="field.fieldType === 'idcard'" class="form-input" type="idcard"
					:placeholder="field.placeholder || '请输入身份证号'"
					v-model="formData[field.fieldKey]" maxlength="18" />
				<!-- 多行文本 -->
				<textarea v-else-if="field.fieldType === 'textarea'" class="form-textarea"
					:placeholder="field.placeholder || '请输入' + field.fieldName"
					v-model="formData[field.fieldKey]" />
				<!-- 选择器 -->
				<picker v-else-if="field.fieldType === 'select'" mode="selector" 
					:range="getOptions(field)" @change="onPickerChange($event, field)">
					<view class="form-picker">
						<text :class="{ placeholder: !formData[field.fieldKey] }">
							{{ formData[field.fieldKey] || field.placeholder || '请选择' + field.fieldName }}
						</text>
						<text class="arrow">›</text>
					</view>
				</picker>
				<!-- 日期选择 -->
				<picker v-else-if="field.fieldType === 'date'" mode="date" @change="onDateChange($event, field)">
					<view class="form-picker">
						<text :class="{ placeholder: !formData[field.fieldKey] }">
							{{ formData[field.fieldKey] || field.placeholder || '请选择日期' }}
						</text>
						<text class="arrow">›</text>
					</view>
				</picker>
				<!-- 图片上传 -->
				<view v-else-if="field.fieldType === 'image'" class="form-images">
					<view class="image-item" v-for="(img, idx) in (formData[field.fieldKey] || [])" :key="idx">
						<image :src="img" mode="aspectFill" @click="previewImage(formData[field.fieldKey], idx)"></image>
						<view class="del-btn" @click="removeImage(field.fieldKey, idx)">×</view>
					</view>
					<view class="add-image" @click="chooseImage(field.fieldKey)" v-if="(formData[field.fieldKey] || []).length < 9">
						<text class="add-icon">+</text>
						<text class="add-text">上传图片</text>
					</view>
				</view>
				<!-- 默认文本 -->
				<input v-else class="form-input" 
					:placeholder="field.placeholder || '请输入' + field.fieldName"
					v-model="formData[field.fieldKey]" />
			</view>
		</view>

		<!-- 优惠券 -->
		<view class="coupon-section" @click="selectCoupon">
			<text class="coupon-label">优惠券</text>
			<view class="coupon-value">
				<text v-if="selectedCoupon" class="coupon-discount">-¥{{ discountAmount }}</text>
				<text v-else class="coupon-placeholder">{{ availableCoupons.length ? '选择优惠券' : '暂无可用' }}</text>
				<text class="arrow">›</text>
			</view>
		</view>

		<!-- 备注 -->
		<view class="remark-section">
			<text class="remark-label">备注</text>
			<textarea class="remark-input" placeholder="选填，如有特殊要求请备注" v-model="remark" />
		</view>

		<!-- 企业搜索弹窗 -->
		<view class="enterprise-popup" v-if="showEnterprisePopup" @touchmove.stop.prevent="">
			<view class="popup-mask" @click="closeEnterpriseSearch"></view>
			<view class="popup-content" @click.stop @touchmove.stop>
				<view class="popup-header">
					<text class="popup-title">搜索企业</text>
					<text class="popup-close" @click="closeEnterpriseSearch">×</text>
				</view>
				<view class="search-box">
					<input class="search-input" v-model="enterpriseSearchKeyword" 
						placeholder="请输入企业名称关键字" @input="onEnterpriseSearch" @click.stop />
				</view>
				<scroll-view class="search-results" scroll-y @touchmove.stop>
					<view class="result-item" v-for="item in enterpriseSearchResults" :key="item.id" 
						@click="selectEnterprise(item)">
						<text class="company-name">{{ item.companyName }}</text>
						<view class="company-info">
							<text v-if="item.legalPerson">法人：{{ item.legalPerson }}</text>
							<text v-if="item.creditNo">信用代码：{{ item.creditNo }}</text>
						</view>
					</view>
					<view class="no-result" v-if="enterpriseSearchKeyword && !enterpriseSearchResults.length">
						<text>未找到相关企业</text>
					</view>
					<view class="search-tip" v-if="!enterpriseSearchKeyword">
						<text>请输入至少2个字符进行搜索</text>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 底部结算 -->
		<view class="bottom-bar">
			<view class="price-info">
				<text class="total-label">合计：</text>
				<text class="total-price">¥{{ finalPrice }}</text>
			</view>
			<view class="submit-btn" @click="submitOrder">提交订单</view>
		</view>
	</view>
</template>

<script>
import { productApi, orderApi, couponApi, enterpriseApi } from '@/api/index.js'
import { uploadFile } from '@/utils/request.js'

export default {
	data() {
		return {
			productId: null,
			product: {},
			formData: {},
			remark: '',
			availableCoupons: [],
			selectedCoupon: null,
			selectedYearPrice: null,
			// 企业搜索相关
			enterpriseSearchKeyword: '',
			enterpriseSearchResults: [],
			showEnterprisePopup: false,
			currentEnterpriseField: null,
			searchTimer: null
		}
	},
	computed: {
		currentPrice() {
			// 如果选择了年度价格，使用年度价格，否则使用产品默认价格
			if (this.selectedYearPrice) {
				return parseFloat(this.selectedYearPrice.price) || 0
			}
			return parseFloat(this.product.price) || 0
		},
		finalPrice() {
			let price = this.currentPrice
			if (this.selectedCoupon) {
				// 计算优惠金额
				let discount = 0
				if (this.selectedCoupon.type === 'fixed') {
					discount = parseFloat(this.selectedCoupon.value) || 0
				} else {
					// 折扣券
					discount = price * (1 - parseFloat(this.selectedCoupon.value))
					// 检查最大优惠金额
					if (this.selectedCoupon.maxDiscount && discount > parseFloat(this.selectedCoupon.maxDiscount)) {
						discount = parseFloat(this.selectedCoupon.maxDiscount)
					}
				}
				price -= discount
			}
			return Math.max(0, price).toFixed(2)
		},
		discountAmount() {
			if (!this.selectedCoupon) return 0
			let price = this.currentPrice
			if (this.selectedCoupon.type === 'fixed') {
				return parseFloat(this.selectedCoupon.value) || 0
			} else {
				let discount = price * (1 - parseFloat(this.selectedCoupon.value))
				if (this.selectedCoupon.maxDiscount && discount > parseFloat(this.selectedCoupon.maxDiscount)) {
					discount = parseFloat(this.selectedCoupon.maxDiscount)
				}
				return discount.toFixed(2)
			}
		}
	},
	onLoad(options) {
		this.productId = options.productId
		this.loadProduct()
	},
	methods: {
		async loadProduct() {
			try {
				const res = await productApi.getProductDetail(this.productId)
				this.product = res.data || {}
				// 初始化表单数据
				if (this.product.fields) {
					this.product.fields.forEach(f => {
						if (f.fieldType === 'image') {
							this.$set(this.formData, f.fieldKey, [])
						} else {
							this.$set(this.formData, f.fieldKey, '')
						}
					})
				}
				// 如果有年度价格，默认选中第一个
				if (this.product.yearPrices && this.product.yearPrices.length) {
					this.selectedYearPrice = this.product.yearPrices[0]
				}
				// 产品加载后再加载优惠券
				this.loadCoupons()
			} catch (e) {
				console.error('加载产品失败', e)
			}
		},
		async loadCoupons() {
			// 检查是否已登录，未登录不加载优惠券
			const token = uni.getStorageSync('token')
			if (!token) {
				this.availableCoupons = []
				return
			}
			try {
				const res = await couponApi.getOrderAvailableCoupons(this.currentPrice, this.productId)
				this.availableCoupons = res.data || []
			} catch (e) {
				console.error('加载优惠券失败', e)
			}
		},
		selectYearPrice(yp) {
			this.selectedYearPrice = yp
			// 切换年度后重新加载可用优惠券
			this.selectedCoupon = null
			this.loadCoupons()
		},
		getOptions(field) {
			if (field.placeholder && field.placeholder.includes(',')) {
				return field.placeholder.split(',')
			}
			return []
		},
		onPickerChange(e, field) {
			const options = this.getOptions(field)
			this.formData[field.fieldKey] = options[e.detail.value]
		},
		onDateChange(e, field) {
			this.formData[field.fieldKey] = e.detail.value
		},
		// 判断是否为企业名称字段
		isCompanyField(field) {
			const key = field.fieldKey.toLowerCase()
			const name = field.fieldName
			return key.includes('company_name') || key.includes('companyname') || 
				   name.includes('企业名称') || name.includes('公司名称') ||
				   field.fieldType === 'company'
		},
		chooseImage(fieldKey) {
			uni.chooseImage({
				count: 9 - (this.formData[fieldKey] || []).length,
				success: async (res) => {
					for (const path of res.tempFilePaths) {
						uni.showLoading({ title: '上传中...' })
						try {
							const uploadRes = await uploadFile({
								url: '/common/upload',
								filePath: path,
								name: 'file'
							})
							if (!this.formData[fieldKey]) {
								this.$set(this.formData, fieldKey, [])
							}
							this.formData[fieldKey].push(uploadRes.data.url)
						} catch (e) {
							console.error('上传失败', e)
						}
						uni.hideLoading()
					}
				}
			})
		},
		removeImage(fieldKey, idx) {
			this.formData[fieldKey].splice(idx, 1)
		},
		previewImage(images, idx) {
			uni.previewImage({ urls: images, current: idx })
		},
		selectCoupon() {
			if (!this.availableCoupons.length) return
			const price = this.currentPrice
			const items = this.availableCoupons.map(c => {
				let discount = 0
				if (c.type === 'fixed') {
					discount = parseFloat(c.value) || 0
				} else {
					discount = price * (1 - parseFloat(c.value))
					if (c.maxDiscount && discount > parseFloat(c.maxDiscount)) {
						discount = parseFloat(c.maxDiscount)
					}
				}
				return `${c.name} -¥${discount.toFixed(2)}`
			})
			uni.showActionSheet({
				itemList: ['不使用优惠券', ...items],
				success: (res) => {
					if (res.tapIndex === 0) {
						this.selectedCoupon = null
					} else {
						this.selectedCoupon = this.availableCoupons[res.tapIndex - 1]
					}
				}
			})
		},
		validateForm() {
			if (!this.product.fields) return true
			const phoneReg = /^1[3-9]\d{9}$/
			for (const field of this.product.fields) {
				const val = this.formData[field.fieldKey]
				if (field.isRequired) {
					if (field.fieldType === 'image') {
						if (!val || !val.length) {
							uni.showToast({ title: `请上传${field.fieldName}`, icon: 'none' })
							return false
						}
					} else if (!val) {
						uni.showToast({ title: `请填写${field.fieldName}`, icon: 'none' })
						return false
					}
				}
				// 手机号格式验证
				if (field.fieldType === 'phone' && val && !phoneReg.test(val)) {
					uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
					return false
				}
			}
			return true
		},
		async submitOrder() {
			// 检查是否已登录
			const token = uni.getStorageSync('token')
			if (!token) {
				uni.showToast({ title: '请先登录', icon: 'none' })
				setTimeout(() => {
					uni.switchTab({ url: '/pages/mine/mine' })
				}, 1500)
				return
			}
			if (!this.validateForm()) return
			uni.showLoading({ title: '提交中...' })
			try {
				const res = await orderApi.createOrder({
					productId: this.productId,
					yearPriceId: this.selectedYearPrice?.id,
					formData: JSON.stringify(this.formData),
					userCouponId: this.selectedCoupon?.id,
					remark: this.remark
				})
				uni.hideLoading()
				uni.showToast({ title: '提交成功', icon: 'success' })
				setTimeout(() => {
					uni.redirectTo({ url: `/pages/order/detail?id=${res.data.id}` })
				}, 1500)
			} catch (e) {
				uni.hideLoading()
				console.error('提交订单失败', e)
			}
		},
		// 企业搜索相关方法
		openEnterpriseSearch(field) {
			this.currentEnterpriseField = field
			this.enterpriseSearchKeyword = ''
			this.enterpriseSearchResults = []
			this.showEnterprisePopup = true
		},
		closeEnterpriseSearch() {
			this.showEnterprisePopup = false
			this.currentEnterpriseField = null
		},
		onEnterpriseSearch() {
			// 防抖处理
			if (this.searchTimer) {
				clearTimeout(this.searchTimer)
			}
			this.searchTimer = setTimeout(() => {
				this.doEnterpriseSearch()
			}, 500)
		},
		async doEnterpriseSearch() {
			const keyword = this.enterpriseSearchKeyword.trim()
			if (keyword.length < 2) {
				this.enterpriseSearchResults = []
				return
			}
			try {
				const res = await enterpriseApi.searchEnterprise(keyword)
				this.enterpriseSearchResults = res.data || []
			} catch (e) {
				console.error('搜索企业失败', e)
			}
		},
		selectEnterprise(item) {
			// 填充企业名称
			if (this.currentEnterpriseField) {
				this.formData[this.currentEnterpriseField.fieldKey] = item.companyName
			}
			// 自动填充关联字段
			this.autoFillEnterpriseFields(item)
			this.closeEnterpriseSearch()
		},
		autoFillEnterpriseFields(enterprise) {
			// 遍历所有字段，自动填充法人、信用代码、注册号等
			if (!this.product.fields) return
			for (const field of this.product.fields) {
				const key = field.fieldKey.toLowerCase()
				const name = field.fieldName
				// 法人姓名（排除法人手机号、法人身份证等）
				if (enterprise.legalPerson) {
					const isLegalPerson = (key === 'legal_person' || key === 'legalperson' || 
						name === '法人姓名' || name === '法人' || name === '法定代表人')
					const isNotPhone = !key.includes('phone') && !key.includes('mobile') && !key.includes('tel') && !name.includes('手机') && !name.includes('电话')
					const isNotIdCard = !key.includes('id') && !key.includes('card') && !name.includes('身份证')
					if (isLegalPerson && isNotPhone && isNotIdCard) {
						this.formData[field.fieldKey] = enterprise.legalPerson
					}
				}
				// 统一社会信用代码
				if ((key.includes('credit') || key.includes('信用代码') || key === 'creditNo') && enterprise.creditNo) {
					this.formData[field.fieldKey] = enterprise.creditNo
				}
				// 注册号
				if ((key.includes('companycode') || key.includes('注册号') || key === 'companyCode') && enterprise.companyCode) {
					this.formData[field.fieldKey] = enterprise.companyCode
				}
				// 成立日期
				if ((key.includes('establish') || key.includes('成立')) && enterprise.establishDate) {
					// 格式化日期 20180523 -> 2018-05-23
					const date = enterprise.establishDate
					if (date && date.length === 8) {
						this.formData[field.fieldKey] = `${date.substring(0,4)}-${date.substring(4,6)}-${date.substring(6,8)}`
					} else {
						this.formData[field.fieldKey] = date
					}
				}
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

/* 产品卡片 */
.product-card {
	display: flex;
	padding: 24rpx;
	background: #fff;
	margin: 20rpx;
	border-radius: 16rpx;
}
.product-img {
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}
.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}
.product-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}
.product-tags {
	display: flex;
	gap: 12rpx;
	margin-top: 8rpx;
}
.tag {
	font-size: 22rpx;
	color: #1976d2;
	background: #e3f2fd;
	padding: 4rpx 12rpx;
	border-radius: 6rpx;
}
.product-price {
	display: flex;
	align-items: baseline;
	gap: 12rpx;
}
.price {
	font-size: 36rpx;
	font-weight: bold;
	color: #d11a1a;
}
.original-price {
	font-size: 26rpx;
	color: #999;
	text-decoration: line-through;
}

/* 产品信息区域 */
.info-section {
	margin: 20rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.info-section .section-content {
	font-size: 28rpx;
	color: #666;
	line-height: 1.8;
	white-space: pre-wrap;
}

/* 年度选择 */
.year-section {
	margin: 20rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.year-list {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}
.year-item {
	flex: 0 0 calc(33.33% - 12rpx);
	padding: 20rpx 16rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
	text-align: center;
	border: 2rpx solid transparent;
	box-sizing: border-box;
}
.year-item.active {
	background: #e3f2fd;
	border-color: #1976d2;
}
.year-name {
	font-size: 28rpx;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}
.year-item.active .year-name {
	color: #d11a1a;
	font-weight: bold;
}
.year-price-info {
	display: flex;
	align-items: baseline;
	justify-content: center;
	gap: 8rpx;
}
.year-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #d11a1a;
}
.year-original {
	font-size: 22rpx;
	color: #999;
	text-decoration: line-through;
}

/* 表单区域 */
.form-section {
	margin: 20rpx;
	background: #fff;
	border-radius: 16rpx;
	padding: 24rpx;
}
.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 24rpx;
	padding-left: 16rpx;
	border-left: 6rpx solid #1976d2;
}
.form-item {
	margin-bottom: 24rpx;
}
.form-label {
	font-size: 28rpx;
	color: #333;
	margin-bottom: 12rpx;
}
.required {
	color: #d11a1a;
	margin-right: 4rpx;
}
.form-input {
	width: 100%;
	height: 80rpx;
	padding: 0 20rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}
.form-textarea {
	width: 100%;
	height: 160rpx;
	padding: 20rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}
.form-picker {
	display: flex;
	justify-content: space-between;
	align-items: center;
	height: 80rpx;
	padding: 0 20rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
	font-size: 28rpx;
}
.form-picker .placeholder {
	color: #999;
}
.arrow {
	color: #999;
	font-size: 32rpx;
}
.search-icon {
	font-size: 28rpx;
}

/* 企业名称输入 */
.form-company {
	width: 100%;
}
.company-input-wrapper {
	display: flex;
	align-items: center;
	gap: 16rpx;
}
.company-input {
	flex: 1;
	margin: 0;
}
.search-btn {
	display: flex;
	align-items: center;
	gap: 8rpx;
	padding: 0 24rpx;
	height: 80rpx;
	background: #1976d2;
	color: #fff;
	border-radius: 12rpx;
	font-size: 26rpx;
	white-space: nowrap;
}
.search-btn .search-icon {
	font-size: 28rpx;
}
.search-text {
	font-size: 26rpx;
}

/* 图片上传 */
.form-images {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}
.image-item {
	position: relative;
	width: 200rpx;
	height: 200rpx;
}
.image-item image {
	width: 100%;
	height: 100%;
	border-radius: 12rpx;
}
.del-btn {
	position: absolute;
	top: -16rpx;
	right: -16rpx;
	width: 40rpx;
	height: 40rpx;
	background: #d11a1a;
	color: #fff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
}
.add-image {
	width: 200rpx;
	height: 200rpx;
	background: #f9f9f9;
	border: 2rpx dashed #ddd;
	border-radius: 12rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}
.add-icon {
	font-size: 60rpx;
	color: #999;
}
.add-text {
	font-size: 24rpx;
	color: #999;
	margin-top: 8rpx;
}

/* 优惠券 */
.coupon-section {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin: 20rpx;
	padding: 24rpx;
	background: #fff;
	border-radius: 16rpx;
}
.coupon-label {
	font-size: 28rpx;
	color: #333;
}
.coupon-value {
	display: flex;
	align-items: center;
	gap: 8rpx;
}
.coupon-discount {
	color: #d11a1a;
	font-size: 28rpx;
}
.coupon-placeholder {
	color: #999;
	font-size: 28rpx;
}

/* 备注 */
.remark-section {
	margin: 20rpx;
	padding: 24rpx;
	background: #fff;
	border-radius: 16rpx;
}
.remark-label {
	font-size: 28rpx;
	color: #333;
	display: block;
	margin-bottom: 16rpx;
}
.remark-input {
	width: 100%;
	height: 120rpx;
	padding: 16rpx;
	background: #f9f9f9;
	border-radius: 12rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}

/* 底部结算 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background: #fff;
	box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
	padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.price-info {
	display: flex;
	align-items: baseline;
}
.total-label {
	font-size: 28rpx;
	color: #666;
}
.total-price {
	font-size: 40rpx;
	font-weight: bold;
	color: #d11a1a;
}
.submit-btn {
	padding: 24rpx 60rpx;
	background: #d11a1a;
	color: #fff;
	font-size: 30rpx;
	border-radius: 40rpx;
}

/* 企业搜索弹窗 */
.enterprise-popup {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 999;
	display: flex;
	align-items: flex-end;
}
.popup-mask {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background: rgba(0,0,0,0.5);
}
.popup-content {
	position: relative;
	width: 100%;
	height: 70vh;
	background: #fff;
	border-radius: 32rpx 32rpx 0 0;
	display: flex;
	flex-direction: column;
}
.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}
.popup-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}
.popup-close {
	font-size: 48rpx;
	color: #999;
	line-height: 1;
}
.search-box {
	padding: 20rpx 30rpx;
}
.search-input {
	width: 100%;
	height: 80rpx;
	padding: 0 24rpx;
	background: #f5f5f5;
	border-radius: 40rpx;
	font-size: 28rpx;
	box-sizing: border-box;
}
.search-results {
	flex: 1;
	padding: 0 30rpx;
	height: 0;
	overflow: hidden;
}
.result-item {
	padding: 24rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}
.company-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}
.company-info {
	display: flex;
	flex-wrap: wrap;
	gap: 16rpx;
}
.company-info text {
	font-size: 24rpx;
	color: #999;
}
.no-result, .search-tip {
	text-align: center;
	padding: 60rpx 0;
	color: #999;
	font-size: 28rpx;
}
</style>
