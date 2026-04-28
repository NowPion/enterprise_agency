/**
 * 优惠券相关API
 */
import { get, post } from '@/utils/request.js'

// 获取我的优惠券
export const getMyCoupons = (params) => get('/coupon/my', params)

// 获取可领取的优惠券列表
export const getAvailableCoupons = () => get('/coupon/available')

// 领取优惠券
export const receiveCoupon = (couponId) => post(`/coupon/receive/${couponId}`)

// 获取订单可用的优惠券
export const getOrderAvailableCoupons = (orderAmount, productId) => get('/coupon/order-available', { orderAmount, productId })

// 获取未使用优惠券数量
export const getUnusedCount = () => get('/coupon/unused-count')

// 计算优惠金额
export const calculateDiscount = (userCouponId, orderAmount) => get('/coupon/calculate', { userCouponId, orderAmount })

export default {
	getMyCoupons,
	getAvailableCoupons,
	receiveCoupon,
	getOrderAvailableCoupons,
	getUnusedCount,
	calculateDiscount
}
