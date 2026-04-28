import request from '../utils/request';
import { BASE_URI } from './base';

// ========================================
// 交易记录管理
// ========================================
export function getTransactions(params: { type?: string; page?: number; size?: number }) {
    return request({ url: `${BASE_URI}/transactions`, method: 'get', params });
}

export function getTransaction(id: number) {
    return request({ url: `${BASE_URI}/transactions/${id}`, method: 'get' });
}

export function getTransactionsByOrder(orderId: number) {
    return request({ url: `${BASE_URI}/transactions/order/${orderId}`, method: 'get' });
}

export function getTransactionStats() {
    return request({ url: `${BASE_URI}/transactions/stats`, method: 'get' });
}

// ========================================
// 发票管理
// ========================================
export function getInvoices(params: { status?: string; page?: number; size?: number }) {
    return request({ url: `${BASE_URI}/invoices`, method: 'get', params });
}

export function getInvoice(id: number) {
    return request({ url: `${BASE_URI}/invoices/${id}`, method: 'get' });
}

export function issueInvoice(id: number, data: { invoiceNo: string; invoiceUrl: string }) {
    return request({ url: `${BASE_URI}/invoices/${id}/issue`, method: 'post', data });
}

export function failInvoice(id: number, data: { reason: string }) {
    return request({ url: `${BASE_URI}/invoices/${id}/fail`, method: 'post', data });
}

export function getInvoiceStats() {
    return request({ url: `${BASE_URI}/invoices/stats`, method: 'get' });
}
