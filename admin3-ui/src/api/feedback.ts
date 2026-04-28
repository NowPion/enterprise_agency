import request from '../utils/request';
import { BASE_URI } from './base';

// 获取反馈列表
export function getFeedbackList(params: { status?: string; page: number; size: number }) {
    return request({
        url: `${BASE_URI}/feedbacks`,
        method: 'get',
        params
    });
}

// 获取反馈详情
export function getFeedbackById(id: number) {
    return request({
        url: `${BASE_URI}/feedbacks/${id}`,
        method: 'get'
    });
}

// 回复反馈
export function replyFeedback(id: number, reply: string) {
    return request({
        url: `${BASE_URI}/feedbacks/${id}/reply`,
        method: 'post',
        data: { reply }
    });
}

// 更新状态
export function updateFeedbackStatus(id: number, status: string) {
    return request({
        url: `${BASE_URI}/feedbacks/${id}/status`,
        method: 'put',
        data: { status }
    });
}

// 删除反馈
export function deleteFeedback(id: number) {
    return request({
        url: `${BASE_URI}/feedbacks/${id}`,
        method: 'delete'
    });
}

// 获取待处理数量
export function getPendingCount() {
    return request({
        url: `${BASE_URI}/feedbacks/pending-count`,
        method: 'get'
    });
}
