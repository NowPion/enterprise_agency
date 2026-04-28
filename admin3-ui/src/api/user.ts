import request from '../utils/request';
import { BASE_URI } from './base';

// 获取可派单的办理人员列表（角色为"办理人员"的用户）
export const getAssignees = () => {
  return request({
    url: `${BASE_URI}/orders/assignees`,
    method: 'get'
  });
};

// 创建用户
export const createUser = (data: any) => {
  return request({
    url: `${BASE_URI}/users`,
    method: 'post',
    data
  });
};

// 更新用户
export const updateUser = (id: number, data: any) => {
  return request({
    url: `${BASE_URI}/users/${id}`,
    method: 'put',
    data
  });
};

// 删除用户
export const deleteUser = (id: number) => {
  return request({
    url: `${BASE_URI}/users/${id}`,
    method: 'delete'
  });
};

// 禁用用户
export const disableUser = (id: number) => {
  return request({
    url: `${BASE_URI}/users/${id}:disable`,
    method: 'post'
  });
};

// 启用用户
export const enableUser = (id: number) => {
  return request({
    url: `${BASE_URI}/users/${id}:enable`,
    method: 'post'
  });
};

// 修改密码
export const changePassword = (data: { oldPassword: string; newPassword: string }) => {
  return request({
    url: `${BASE_URI}/users/password:change`,
    method: 'post',
    data
  });
};

// 重置用户密码（管理员）
export const resetPassword = (userId: number, newPassword: string) => {
  return request({
    url: `${BASE_URI}/users/${userId}/password:reset`,
    method: 'post',
    data: { newPassword }
  });
};
