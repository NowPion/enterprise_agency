/**
 * 订单相关API
 */
import { get, post, del } from '@/utils/request.js'

// 创建订单
export const createOrder = (data) => post('/order/create', data)

// 获取订单列表
export const getOrderList = (params) => get('/order/list', params)

// 获取订单详情
export const getOrderDetail = (id) => get(`/order/detail/${id}`)

// 取消订单
export const cancelOrder = (id) => post(`/order/cancel/${id}`)

// 申请退款
export const applyRefund = (id, reason) => post(`/order/refund/${id}?reason=${encodeURIComponent(reason)}`)

// 发起支付
export const prepay = (id) => post(`/order/pay/${id}`)

// 查询支付状态
export const queryPayStatus = (id) => get(`/order/pay-status/${id}`)

// 获取订单材料
export const getMaterials = (orderId) => get(`/order/${orderId}/materials`)

// 上传订单材料
export const uploadMaterial = (orderId, data) => post(`/order/${orderId}/materials`, data)

// 删除订单材料
export const deleteMaterial = (orderId, materialId) => del(`/order/${orderId}/materials/${materialId}`)

export default {
	createOrder,
	getOrderList,
	getOrderDetail,
	cancelOrder,
	applyRefund,
	prepay,
	queryPayStatus,
	getMaterials,
	uploadMaterial,
	deleteMaterial
}
