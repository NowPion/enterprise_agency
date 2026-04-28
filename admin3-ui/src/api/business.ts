import request from '../utils/request';
import { BASE_URI } from './base';

// ========================================
// 产品分类管理
// ========================================
export function getProductCategories() {
    return request({ url: `${BASE_URI}/product-category`, method: 'get' });
}

export function getEnabledProductCategories() {
    return request({ url: `${BASE_URI}/product-category/enabled`, method: 'get' });
}

export function createProductCategory(data: any) {
    return request({ url: `${BASE_URI}/product-category`, method: 'post', data });
}

export function updateProductCategory(id: number, data: any) {
    return request({ url: `${BASE_URI}/product-category/${id}`, method: 'put', data });
}

export function deleteProductCategory(id: number) {
    return request({ url: `${BASE_URI}/product-category/${id}`, method: 'delete' });
}

// ========================================
// 产品管理
// ========================================
export function getProducts() {
    return request({ url: `${BASE_URI}/products`, method: 'get' });
}

export function getProduct(id: number) {
    return request({ url: `${BASE_URI}/products/${id}`, method: 'get' });
}

export function createProduct(data: any) {
    return request({ url: `${BASE_URI}/products`, method: 'post', data });
}

export function updateProduct(id: number, data: any) {
    return request({ url: `${BASE_URI}/products/${id}`, method: 'put', data });
}

export function deleteProduct(id: number) {
    return request({ url: `${BASE_URI}/products/${id}`, method: 'delete' });
}

// 产品字段
export function getProductFields(productId: number) {
    return request({ url: `${BASE_URI}/products/${productId}/fields`, method: 'get' });
}

export function createProductField(productId: number, data: any) {
    return request({ url: `${BASE_URI}/products/${productId}/fields`, method: 'post', data });
}

export function updateProductField(fieldId: number, data: any) {
    return request({ url: `${BASE_URI}/products/fields/${fieldId}`, method: 'put', data });
}

export function deleteProductField(fieldId: number) {
    return request({ url: `${BASE_URI}/products/fields/${fieldId}`, method: 'delete' });
}

// 产品年度价格
export function getProductYearPrices(productId: number) {
    return request({ url: `${BASE_URI}/products/${productId}/year-prices`, method: 'get' });
}

export function createProductYearPrice(productId: number, data: any) {
    return request({ url: `${BASE_URI}/products/${productId}/year-prices`, method: 'post', data });
}

export function updateProductYearPrice(priceId: number, data: any) {
    return request({ url: `${BASE_URI}/products/year-prices/${priceId}`, method: 'put', data });
}

export function deleteProductYearPrice(priceId: number) {
    return request({ url: `${BASE_URI}/products/year-prices/${priceId}`, method: 'delete' });
}

// ========================================
// 订单管理
// ========================================
export function getOrders(params: { keyword?: string; status?: string; page?: number; size?: number; userId?: number | null }) {
    return request({ url: `${BASE_URI}/orders`, method: 'get', params });
}

export function getOrder(id: number) {
    return request({ url: `${BASE_URI}/orders/${id}`, method: 'get' });
}

export function reviewOrder(id: number, data: { approved: boolean; remark?: string }) {
    return request({ url: `${BASE_URI}/orders/${id}/review`, method: 'post', data });
}

export function assignOrder(id: number, data: { assigneeId: number; assigneeName: string }) {
    return request({ url: `${BASE_URI}/orders/${id}/assign`, method: 'post', data });
}

export function updateOrderProgress(id: number, data: { title: string; content?: string }) {
    return request({ url: `${BASE_URI}/orders/${id}/progress`, method: 'post', data });
}

export function completeOrder(id: number, data: { remark?: string }) {
    return request({ url: `${BASE_URI}/orders/${id}/complete`, method: 'post', data });
}

export function cancelOrder(id: number, data: { reason: string }) {
    return request({ url: `${BASE_URI}/orders/${id}/cancel`, method: 'post', data });
}

export function getOrderProgress(orderId: number) {
    return request({ url: `${BASE_URI}/orders/${orderId}/progress`, method: 'get' });
}

export function getOrderMaterials(orderId: number) {
    return request({ url: `${BASE_URI}/orders/${orderId}/materials`, method: 'get' });
}

export function uploadOrderMaterial(orderId: number, data: any) {
    return request({ url: `${BASE_URI}/orders/${orderId}/materials`, method: 'post', data });
}

export function deleteOrderMaterial(materialId: number) {
    return request({ url: `${BASE_URI}/orders/materials/${materialId}`, method: 'delete' });
}

export function getOrderStats() {
    return request({ url: `${BASE_URI}/orders/stats`, method: 'get' });
}

// ========================================
// 退款管理
// ========================================
export function getRefunds(params: { status?: string; page?: number; size?: number }) {
    return request({ url: `${BASE_URI}/refunds`, method: 'get', params });
}

export function getRefund(id: number) {
    return request({ url: `${BASE_URI}/refunds/${id}`, method: 'get' });
}

export function approveRefund(id: number, data: { operatorId: number; operatorName: string }) {
    return request({ url: `${BASE_URI}/refunds/${id}/approve`, method: 'post', data });
}

export function rejectRefund(id: number, data: { reason: string; operatorId: number; operatorName: string }) {
    return request({ url: `${BASE_URI}/refunds/${id}/reject`, method: 'post', data });
}

export function completeRefund(id: number, data: { wechatRefundId?: string }) {
    return request({ url: `${BASE_URI}/refunds/${id}/complete`, method: 'post', data });
}

export function syncRefundStatus(id: number) {
    return request({ url: `${BASE_URI}/refunds/${id}/sync`, method: 'post' });
}
