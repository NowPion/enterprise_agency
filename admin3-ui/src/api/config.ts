import request from '../utils/request';
import { BASE_URI } from './base';

// 获取所有配置
export function getConfigList() {
    return request({
        url: `${BASE_URI}/system-configs`,
        method: 'get'
    });
}

// 获取配置Map
export function getConfigMap() {
    return request({
        url: `${BASE_URI}/system-configs/map`,
        method: 'get'
    });
}

// 根据ID获取配置
export function getConfigById(id: number) {
    return request({
        url: `${BASE_URI}/system-configs/${id}`,
        method: 'get'
    });
}

// 根据前缀获取配置
export function getConfigByPrefix(prefix: string) {
    return request({
        url: `${BASE_URI}/system-configs/prefix/${prefix}`,
        method: 'get'
    });
}

// 创建配置
export function createConfig(data: {
    configKey: string;
    configValue: string;
    configType: string;
    description: string;
}) {
    return request({
        url: `${BASE_URI}/system-configs`,
        method: 'post',
        data
    });
}

// 更新配置
export function updateConfig(id: number, data: {
    configValue: string;
    configType?: string;
    description?: string;
}) {
    return request({
        url: `${BASE_URI}/system-configs/${id}`,
        method: 'put',
        data
    });
}

// 批量更新配置
export function batchUpdateConfig(data: Array<{
    id: number;
    configValue: string;
    description?: string;
}>) {
    return request({
        url: `${BASE_URI}/system-configs/batch`,
        method: 'put',
        data
    });
}

// 删除配置
export function deleteConfig(id: number) {
    return request({
        url: `${BASE_URI}/system-configs/${id}`,
        method: 'delete'
    });
}

// 根据配置键更新配置值
export function updateConfigByKey(configKey: string, configValue: string) {
    return request({
        url: `${BASE_URI}/system-configs/key/${configKey}`,
        method: 'put',
        data: { configValue }
    });
}
