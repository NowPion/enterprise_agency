export const BASE_URI = import.meta.env.VITE_BASE_URI;

// 获取完整图片URL
export const getFullUrl = (url: string) => {
  if (!url) return '';
  // 如果已经是完整URL（OSS存储或已拼接），直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 如果URL已经以/api开头，说明已经是完整路径
  if (url.startsWith('/api/')) {
    return url;
  }
  // 本地存储路径，拼接/api前缀
  if (url.startsWith('/')) {
    return '/api' + url;
  }
  // 其他情况，拼接BASE_URI
  return `${BASE_URI}/${url}`;
};
