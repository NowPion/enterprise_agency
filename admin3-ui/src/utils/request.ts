import axios, {AxiosError, AxiosInstance, AxiosRequestConfig, AxiosResponse} from 'axios';
import {ElMessage} from "element-plus";
import router from "../router";

const service: AxiosInstance = axios.create({
    timeout: 5000
});

service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const headerToken = localStorage.getItem('token');
        if (headerToken) {
            // @ts-ignore
            config.headers['Authorization'] = 'Bearer ' + headerToken
        }

        return config;
    },
    (error: AxiosError) => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    (response: AxiosResponse) => {
        console.log("源数据", response)
        if (response.status >= 200 && response.status < 300) {
            return response;
        } else {
            return Promise.reject(new Error(`HTTP Error: ${response.status}`));
        }
    },
    (error: AxiosError) => {
        console.log("请求错误:", error);
        console.log("错误响应:", error.response);
        // @ts-ignore
        const errorMessage = error.response?.data?.message || '请求失败';
        if (error.response?.status === 401) {
            // 显示错误消息
            ElMessage.error(errorMessage);
            // 如果已经在登录页，不需要跳转
            if (router.currentRoute.value.path !== '/login') {
                localStorage.removeItem('token');
                router.push('/login');
            }
        } else if (error.response?.status === 403) {
            ElMessage.error('没有权限访问该资源');
        } else {
            ElMessage.error(errorMessage);
        }
        return Promise.reject(error);
    }
);

export default service;
