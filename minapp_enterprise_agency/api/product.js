/**
 * 产品相关API
 */
import { get } from '@/utils/request.js'

// 获取产品列表
export const getProductList = (params) => get('/product/list', params)

// 获取产品详情
export const getProductDetail = (id) => get(`/product/detail/${id}`)

// 获取产品分类
export const getCategories = () => get('/product/categories')

export default {
	getProductList,
	getProductDetail,
	getCategories
}
