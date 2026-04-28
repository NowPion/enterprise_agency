import request from '../utils/request';
import { BASE_URI } from './base';

export function getBannerList() {
    return request({ url: `${BASE_URI}/banners`, method: 'get' });
}

export function getEnabledBanners() {
    return request({ url: `${BASE_URI}/banners/enabled`, method: 'get' });
}

export function getBannerById(id: number) {
    return request({ url: `${BASE_URI}/banners/${id}`, method: 'get' });
}

export function createBanner(data: {
    title?: string;
    image: string;
    linkType?: number;
    linkUrl?: string;
    sort?: number;
    status?: number;
}) {
    return request({ url: `${BASE_URI}/banners`, method: 'post', data });
}

export function updateBanner(id: number, data: {
    title?: string;
    image?: string;
    linkType?: number;
    linkUrl?: string;
    sort?: number;
    status?: number;
}) {
    return request({ url: `${BASE_URI}/banners/${id}`, method: 'put', data });
}

export function deleteBanner(id: number) {
    return request({ url: `${BASE_URI}/banners/${id}`, method: 'delete' });
}
