import request from '../utils/request';
import { BASE_URI } from './base';

// ========================================
// 公告管理
// ========================================
export function getNotices() {
    return request({ url: `${BASE_URI}/notices`, method: 'get' });
}

export function getNotice(id: number) {
    return request({ url: `${BASE_URI}/notices/${id}`, method: 'get' });
}

export function createNotice(data: {
    title: string;
    content?: string;
    noticeType?: string;
    isTop?: number;
    sort?: number;
    status?: number;
}) {
    return request({ url: `${BASE_URI}/notices`, method: 'post', data });
}

export function updateNotice(id: number, data: any) {
    return request({ url: `${BASE_URI}/notices/${id}`, method: 'put', data });
}

export function deleteNotice(id: number) {
    return request({ url: `${BASE_URI}/notices/${id}`, method: 'delete' });
}

// ========================================
// FAQ管理
// ========================================
export function getFaqs() {
    return request({ url: `${BASE_URI}/faqs`, method: 'get' });
}

export function getFaq(id: number) {
    return request({ url: `${BASE_URI}/faqs/${id}`, method: 'get' });
}

export function createFaq(data: {
    question: string;
    answer?: string;
    category?: string;
    sort?: number;
    status?: number;
}) {
    return request({ url: `${BASE_URI}/faqs`, method: 'post', data });
}

export function updateFaq(id: number, data: any) {
    return request({ url: `${BASE_URI}/faqs/${id}`, method: 'put', data });
}

export function deleteFaq(id: number) {
    return request({ url: `${BASE_URI}/faqs/${id}`, method: 'delete' });
}

// ========================================
// 文章管理
// ========================================
export function getArticles() {
    return request({ url: `${BASE_URI}/articles`, method: 'get' });
}

export function getArticle(id: number) {
    return request({ url: `${BASE_URI}/articles/${id}`, method: 'get' });
}

export function createArticle(data: {
    title: string;
    content?: string;
    cover?: string;
    summary?: string;
    articleType?: string;
    category?: string;
    sort?: number;
    status?: number;
}) {
    return request({ url: `${BASE_URI}/articles`, method: 'post', data });
}

export function updateArticle(id: number, data: any) {
    return request({ url: `${BASE_URI}/articles/${id}`, method: 'put', data });
}

export function deleteArticle(id: number) {
    return request({ url: `${BASE_URI}/articles/${id}`, method: 'delete' });
}
