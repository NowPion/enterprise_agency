/**
 * 内容模块接口（公告、FAQ、文章）
 */
import { get } from '@/utils/request.js'

/**
 * 获取公告列表
 * GET /api/minapp/content/notice/list
 */
export const getNoticeList = (params = {}) => {
	return get('/content/notice/list', { page: 1, size: 10, ...params })
}

/**
 * 获取公告详情
 * GET /api/minapp/content/notice/detail/:id
 */
export const getNoticeDetail = (id) => {
	return get(`/content/notice/detail/${id}`)
}

/**
 * 获取FAQ列表
 * GET /api/minapp/content/faq/list
 */
export const getFaqList = (params = {}) => {
	return get('/content/faq/list', { page: 1, size: 10, ...params })
}

/**
 * 获取文章列表
 * GET /api/minapp/content/article/list
 */
export const getArticleList = (params = {}) => {
	return get('/content/article/list', { page: 1, size: 10, ...params })
}

/**
 * 获取文章详情
 * GET /api/minapp/content/article/detail/:id
 */
export const getArticleDetail = (id) => {
	return get(`/content/article/detail/${id}`)
}

/**
 * 获取用户协议
 * GET /api/minapp/content/agreement
 */
export const getAgreement = () => {
	return get('/content/agreement')
}

/**
 * 获取隐私政策
 * GET /api/minapp/content/privacy
 */
export const getPrivacy = () => {
	return get('/content/privacy')
}

/**
 * 获取办理动态
 * GET /api/minapp/content/dynamics
 */
export const getDynamics = (page = 0, size = 7) => {
	return get('/content/dynamics', { page, size })
}

export default {
	getNoticeList,
	getNoticeDetail,
	getFaqList,
	getArticleList,
	getArticleDetail,
	getAgreement,
	getPrivacy,
	getDynamics
}
