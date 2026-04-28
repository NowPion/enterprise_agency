/**
 * API 统一导出
 * 使用方式：
 * import { userApi, homeApi } from '@/api/index.js'
 * 或
 * import userApi from '@/api/user.js'
 */

import * as userApi from './user.js'
import * as homeApi from './home.js'
import commonApi from './common.js'
import * as orderApi from './order.js'
import * as productApi from './product.js'
import * as couponApi from './coupon.js'
import * as contentApi from './content.js'
import * as feedbackApi from './feedback.js'
import * as invoiceApi from './invoice.js'
import * as enterpriseApi from './enterprise.js'

export {
	userApi,
	homeApi,
	commonApi,
	orderApi,
	productApi,
	couponApi,
	contentApi,
	feedbackApi,
	invoiceApi,
	enterpriseApi
}

export default {
	userApi,
	homeApi,
	commonApi,
	orderApi,
	productApi,
	couponApi,
	contentApi,
	feedbackApi,
	invoiceApi,
	enterpriseApi
}
