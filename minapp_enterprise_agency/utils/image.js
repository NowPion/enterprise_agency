/**
 * 图片URL处理工具
 */
import request from './request.js'

// 后端服务器基础URL（只保留协议+域名+端口+/api）
const SERVER_URL = request.BASE_URL.replace(/\/minapp$/, '')

/**
 * 获取完整图片URL
 * @param {string} url - 图片路径（可能是相对路径或完整URL）
 * @param {string} defaultUrl - 默认图片路径
 * @returns {string} 完整的图片URL
 */
export function getFullImageUrl(url, defaultUrl = '/static/mr.png') {
	if (!url) return defaultUrl
	// 已经是完整URL或本地静态资源
	if (url.startsWith('http://') || url.startsWith('https://') || url.startsWith('/static')) {
		return url
	}
	// 相对路径，拼接后端地址（确保不会有双斜杠）
	const baseUrl = SERVER_URL.endsWith('/') ? SERVER_URL.slice(0, -1) : SERVER_URL
	const path = url.startsWith('/') ? url : `/${url}`
	return `${baseUrl}${path}`
}

/**
 * 批量处理图片URL
 * @param {Array} list - 包含图片字段的对象数组
 * @param {string|Array} fields - 需要处理的图片字段名
 * @returns {Array} 处理后的数组
 */
export function processImageUrls(list, fields) {
	if (!list || !Array.isArray(list)) return list
	const fieldArr = Array.isArray(fields) ? fields : [fields]
	return list.map(item => {
		const newItem = { ...item }
		fieldArr.forEach(field => {
			if (newItem[field]) {
				newItem[field] = getFullImageUrl(newItem[field])
			}
		})
		return newItem
	})
}

export default {
	getFullImageUrl,
	processImageUrls,
	SERVER_URL
}
