import request from '../utils/request';
import { BASE_URI } from './base';

// 小程序用户
export function getMiniappUserList(params: { keyword?: string; status?: number; page?: number; size?: number }) {
    return request({ url: `${BASE_URI}/miniapp-users`, method: 'get', params });
}

export function getMiniappUserById(id: number) {
    return request({ url: `${BASE_URI}/miniapp-users/${id}`, method: 'get' });
}

export function updateMiniappUser(id: number, data: { nickname?: string; phone?: string; status?: number }) {
    return request({ url: `${BASE_URI}/miniapp-users/${id}`, method: 'put', data });
}

export function updateMiniappUserStatus(id: number, status: number) {
    return request({ url: `${BASE_URI}/miniapp-users/${id}/status`, method: 'patch', params: { status } });
}

export function blockMiniappUser(id: number, reason: string) {
    return request({ url: `${BASE_URI}/miniapp-users/${id}/block`, method: 'post', data: { reason } });
}

export function unblockMiniappUser(id: number) {
    return request({ url: `${BASE_URI}/miniapp-users/${id}/unblock`, method: 'post' });
}
