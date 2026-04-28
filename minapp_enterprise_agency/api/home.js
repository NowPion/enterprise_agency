/**
 * 首页模块接口
 */
import { get } from '@/utils/request.js'

/**
 * 获取轮播图列表
 * GET /api/minapp/home/banners
 * @returns {Promise} { list: [{ id, title, image, linkType, linkUrl }] }
 */
export const getBannerList = () => {
	return get('/home/banners')
}

/**
 * 获取首页统计数据
 * GET /api/minapp/home/stats
 * @returns {Promise} { todayUsers, totalUsers }
 */
export const getHomeStats = () => {
	return get('/home/stats')
}

export default {
	getBannerList,
	getHomeStats
}
