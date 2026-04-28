/**
 * 企业信息查询API
 */
import { get } from '@/utils/request.js'

// 搜索企业信息
export const searchEnterprise = (keyword) => get('/enterprise/search', { keyword })

export default {
	searchEnterprise
}
