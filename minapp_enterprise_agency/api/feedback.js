/**
 * 反馈模块接口
 */
import { post } from '@/utils/request.js'

/**
 * 提交反馈
 * POST /api/minapp/feedback/submit
 * @param {Object} data - { type, content, contact }
 */
export const submitFeedback = (data) => {
	return post('/feedback/submit', data)
}

export default {
	submitFeedback
}
