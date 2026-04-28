/**
 * 请求封装
 * 统一处理请求、响应、错误
 */

// 后端服务器地址
// TODO: 请替换为你的真实后端API地址
// 开发环境示例: http://localhost:8080/api
// 生产环境示例: https://api.yourdomain.com/api
//const BASE_URL = 'http://localhost:8080/api/minapp'  // 留空则使用模拟数据，配置后端地址后启用真实接口
const BASE_URL = 'https://www.xinkuai.ltd/api/minapp'

// 是否正在登录中（防止登录成功后立即被清除token）
let isLoggingIn = false

// 设置登录状态
export const setLoggingIn = (status) => {
	isLoggingIn = status
}

// 请求拦截器
const requestInterceptor = (options) => {
	const token = uni.getStorageSync('token') || ''
	return {
		...options,
		header: {
			'Content-Type': 'application/json',
			'Authorization': token ? `Bearer ${token}` : '',
			...options.header
		}
	}
}

// 处理认证错误，跳转登录
const handleAuthError = () => {
	uni.removeStorageSync('token')
	uni.removeStorageSync('userInfo')
	uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
	setTimeout(() => {
		// 登录功能在"我的"页面，使用switchTab跳转
		uni.switchTab({ url: '/pages/mine/mine' })
	}, 1500)
}

// 响应拦截器 - 返回Promise
const responseInterceptor = (res) => {
	console.log("响应数据", res)
	
	return new Promise((resolve, reject) => {
		// 检查是否返回了HTML（说明API地址配置错误）
		if (typeof res.data === 'string' && res.data.includes('<!doctype html>')) {
			console.error('API地址配置错误，返回了HTML页面，请检查BASE_URL配置')
			uni.showToast({ title: 'API地址配置错误', icon: 'none' })
			reject({ code: -1, msg: 'API地址配置错误' })
			return
		}
		
		if (res.statusCode === 200) {
			if (res.data.code === 0 || res.data.code === 200) {
				resolve(res.data)
			} else if (res.data.code === 401 || res.data.code === 1004) {
				// token过期或无效登录凭证
				// 如果不在登录过程中，自动清除登录状态
				if (!isLoggingIn) {
					handleAuthError()
				}
				reject(res.data)
			} else {
				uni.showToast({ title: res.data.msg || res.data.message || '请求失败', icon: 'none' })
				reject(res.data)
			}
		} else if (res.statusCode === 401) {
			// HTTP 401 状态码，登录凭证无效
			// 如果不在登录过程中，自动清除登录状态
			if (!isLoggingIn) {
				handleAuthError()
			}
			reject(res.data || { code: 401, msg: '请先登录' })
		} else {
			// 其他HTTP错误状态码（400、500等），尝试读取后端返回的错误信息
			const errMsg = res.data?.message || res.data?.msg || '请求失败'
			uni.showToast({ title: errMsg, icon: 'none' })
			reject(res.data || { code: res.statusCode, msg: errMsg })
		}
	})
}

// 错误处理
const errorHandler = (err) => {
	uni.showToast({ title: '网络连接失败', icon: 'none' })
	return Promise.reject(err)
}

/**
 * 通用请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径
 * @param {string} options.method - 请求方法 GET/POST/PUT/DELETE
 * @param {Object} options.data - 请求参数
 * @param {Object} options.header - 请求头
 */
export const request = (options) => {
	return new Promise((resolve, reject) => {
		const config = requestInterceptor(options)
		// console.log('发送请求:', {
		// 	url: BASE_URL + config.url,
		// 	method: config.method || 'GET',
		// 	header: config.header
		// })
		uni.request({
			url: BASE_URL + config.url,
			method: config.method || 'GET',
			data: config.data || {},
			header: config.header,
			success: (res) => {
				responseInterceptor(res).then(resolve).catch(reject)
			},
			fail: (err) => {
				errorHandler(err).catch(reject)
			}
		})
	})
}

/**
 * 文件上传方法
 * @param {Object} options - 上传配置
 * @param {string} options.url - 上传路径
 * @param {string} options.filePath - 文件路径
 * @param {string} options.name - 文件字段名
 * @param {Object} options.formData - 额外表单数据
 */
export const uploadFile = (options) => {
	return new Promise((resolve, reject) => {
		const token = uni.getStorageSync('token') || ''
		uni.uploadFile({
			url: BASE_URL + options.url,
			filePath: options.filePath,
			name: options.name || 'file',
			formData: options.formData || {},
			header: {
				'Authorization': token ? `Bearer ${token}` : ''
			},
			success: (res) => {
				try {
					const data = JSON.parse(res.data)
					if (data.code === 0 || data.code === 200) {
						resolve(data)
					} else {
						uni.showToast({ title: data.msg || '上传失败', icon: 'none' })
						reject(data)
					}
				} catch (e) {
					reject(e)
				}
			},
			fail: (err) => {
				uni.showToast({ title: '上传失败', icon: 'none' })
				reject(err)
			}
		})
	})
}

/**
 * GET 请求
 */
export const get = (url, data = {}, header = {}) => {
	return request({ url, method: 'GET', data, header })
}

/**
 * POST 请求
 */
export const post = (url, data = {}, header = {}) => {
	return request({ url, method: 'POST', data, header })
}

/**
 * PUT 请求
 */
export const put = (url, data = {}, header = {}) => {
	return request({ url, method: 'PUT', data, header })
}

/**
 * DELETE 请求
 */
export const del = (url, data = {}, header = {}) => {
	return request({ url, method: 'DELETE', data, header })
}

export default {
	request,
	uploadFile,
	get,
	post,
	put,
	del,
	BASE_URL,
	setLoggingIn
}
