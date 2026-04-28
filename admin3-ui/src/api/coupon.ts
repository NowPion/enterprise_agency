import request from '../utils/request';
import { BASE_URI } from './base'

// 获取优惠券列表
export function getCouponList(params: { keyword?: string; status?: number; page: number; size: number }) {
  return request({
    url: `${BASE_URI}/coupon`,
    method: 'get',
    params
  })
}

// 获取优惠券详情
export function getCouponDetail(id: number) {
  return request({
    url: `${BASE_URI}/coupon/${id}`,
    method: 'get'
  })
}

// 创建优惠券
export function createCoupon(data: any) {
  return request({
    url: `${BASE_URI}/coupon`,
    method: 'post',
    data
  })
}

// 更新优惠券
export function updateCoupon(id: number, data: any) {
  return request({
    url: `${BASE_URI}/coupon/${id}`,
    method: 'put',
    data
  })
}

// 更新优惠券状态
export function updateCouponStatus(id: number, status: number) {
  return request({
    url: `${BASE_URI}/coupon/${id}/status`,
    method: 'put',
    params: { status }
  })
}

// 删除优惠券
export function deleteCoupon(id: number) {
  return request({
    url: `${BASE_URI}/coupon/${id}`,
    method: 'delete'
  })
}
