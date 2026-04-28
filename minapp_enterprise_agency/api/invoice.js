/**
 * 发票相关API
 */
import { get, post } from '@/utils/request.js'

// 申请发票
export const applyInvoice = (data) => post('/invoice/apply', data)

// 获取发票列表
export const getInvoiceList = (params) => get('/invoice/list', params)

// 获取发票详情
export const getInvoiceDetail = (id) => get(`/invoice/detail/${id}`)

// 检查订单是否可开票
export const checkCanApply = (orderId) => get(`/invoice/check/${orderId}`)

export default {
	applyInvoice,
	getInvoiceList,
	getInvoiceDetail,
	checkCanApply
}
