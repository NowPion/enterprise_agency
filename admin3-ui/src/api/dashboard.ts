import request from '../utils/request'
import { BASE_URI } from './base'

// 获取统计概览
export function getStats() {
  return request({
    url: `${BASE_URI}/dashboard/stats`,
    method: 'get'
  })
}

// 获取用户增长趋势
export function getUserTrend() {
  return request({
    url: `${BASE_URI}/dashboard/user-trend`,
    method: 'get'
  })
}

// 获取订单趋势
export function getOrderTrend() {
  return request({
    url: `${BASE_URI}/dashboard/order-trend`,
    method: 'get'
  })
}

// 获取订单状态分布
export function getOrderStatus() {
  return request({
    url: `${BASE_URI}/dashboard/order-status`,
    method: 'get'
  })
}

// 获取产品销量排行
export function getProductRanking() {
  return request({
    url: `${BASE_URI}/dashboard/product-ranking`,
    method: 'get'
  })
}
