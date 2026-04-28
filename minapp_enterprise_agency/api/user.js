/**
 * 用户模块接口
 */
import { post, get } from '@/utils/request.js'

/**
 * 微信登录（含手机号）
 * POST /api/minapp/user/wxLogin
 * @param {Object} data
 * @param {string} data.code - 微信登录凭证，通过 wx.login() 获取
 * @param {string} data.phoneCode - 手机号code，通过 getPhoneNumber 获取
 * @param {string} data.inviter - 邀请人openid（可选）
 * @returns {Promise} { token, userInfo }
 */
export const wxLogin = (data) => {
	return post('/user/wxLogin', {
		code: data.code,
		phoneCode: data.phoneCode || '',
		inviter: data.inviter || ''
	})
}

/**
 * 获取用户信息
 * GET /api/minapp/user/info
 * @returns {Promise} 用户详细信息
 */
export const getUserInfo = () => {
	return get('/user/info')
}

/**
 * 更新用户信息
 * POST /api/minapp/user/update
 * @param {Object} data
 * @param {string} data.nickName - 昵称（可选）
 * @param {string} data.avatarUrl - 头像URL（可选）
 * @param {number} data.gender - 性别 0未知 1男 2女（可选）
 * @returns {Promise}
 */
export const updateUserInfo = (data) => {
	return post('/user/update', {
		nickName: data.nickName,
		avatarUrl: data.avatarUrl,
		gender: data.gender
	})
}

/**
 * 检查登录状态
 * GET /api/minapp/user/checkLogin
 * @returns {Promise} { isLogin, userId }
 */
export const checkLogin = () => {
	return get('/user/checkLogin')
}

/**
 * 退出登录
 * POST /api/minapp/user/logout
 * @returns {Promise}
 */
export const logout = () => {
	return post('/user/logout')
}

export default {
	wxLogin,
	getUserInfo,
	updateUserInfo,
	checkLogin,
	logout
}
