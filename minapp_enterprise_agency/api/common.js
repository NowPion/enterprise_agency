/**
 * 公共接口
 */
import { get, post } from '@/utils/request.js'
import { uploadFile } from '@/utils/request.js'

/**
 * 上传图片
 * @param {string} filePath - 图片本地路径
 * @param {string} type - 图片类型 avatar/tongue/other
 * @returns {Promise} { url }
 */
export const uploadImage = (filePath, type = 'other') => {
	return uploadFile({
		url: '/common/upload',
		filePath: filePath,
		name: 'file',
		formData: { type }
	})
}

/**
 * 获取系统配置
 * @returns {Promise} { appName, version, servicePhone, serviceWechat }
 */
export const getSystemConfig = () => {
	return get('/common/config')
}

/**
 * 获取用户协议
 * @returns {Promise} { content }
 */
export const getUserAgreement = () => {
	return get('/common/agreement/user')
}

/**
 * 获取隐私政策
 * @returns {Promise} { content }
 */
export const getPrivacyPolicy = () => {
	return get('/common/agreement/privacy')
}

/**
 * 提交意见反馈
 * @param {Object} data
 * @param {string} data.content - 反馈内容
 * @param {string} data.contact - 联系方式（可选）
 * @param {string[]} data.images - 图片列表（可选）
 * @returns {Promise}
 */
export const submitFeedback = (data) => {
	return post('/common/feedback', data)
}

/**
 * 获取帮助中心列表
 * @returns {Promise} { list: [{ id, question, answer }] }
 */
export const getHelpList = () => {
	return get('/common/help')
}

/**
 * 获取版本更新信息
 * @param {Object} params
 * @param {string} params.version - 当前版本号
 * @param {string} params.platform - 平台 ios/android/mp-weixin
 * @returns {Promise} { hasUpdate, version, content, forceUpdate, downloadUrl }
 */
export const checkUpdate = (params) => {
	return get('/common/checkUpdate', params)
}

/**
 * 获取客服信息
 * @returns {Promise} { phone, wechat, workTime }
 */
export const getServiceInfo = () => {
	return get('/common/service')
}

/**
 * 获取单个配置项
 * @param {string} key - 配置键
 * @returns {Promise} 配置值
 */
export const getConfig = (key) => {
	return get('/common/config/' + key)
}

export default {
	uploadImage,
	getSystemConfig,
	getUserAgreement,
	getPrivacyPolicy,
	submitFeedback,
	getHelpList,
	checkUpdate,
	getServiceInfo,
	getConfig
}
