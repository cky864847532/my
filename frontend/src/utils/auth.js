// src/utils/auth.js
// 自动刷新 token 工具
import axios from 'axios';
import { useUserStore } from '../store/user';

let refreshing = false;
let refreshPromise = null;

export async function refreshToken() {
  if (refreshing) return refreshPromise;
  refreshing = true;
  const userStore = useUserStore();
  refreshPromise = axios.post('/api/auth/refresh', {}, {
    headers: { Authorization: `Bearer ${userStore.token}` }
  })
    .then(res => {
      userStore.setUser(res.data);
      return res.data.token;
    })
    .catch(() => {
      userStore.logout();
      window.location.href = `${import.meta.env.BASE_URL}#/home`;
      return null;
    })
    .finally(() => {
      refreshing = false;
      refreshPromise = null;
    });
  return refreshPromise;
}

// 封装 axios 拦截器，自动处理 token 过期
export function setupAxiosInterceptors() {
  axios.interceptors.response.use(
    res => res,
    async error => {
      const userStore = useUserStore();
      if (error.response && error.response.status === 401 && userStore.token) {
        // token 过期，尝试刷新
        const newToken = await refreshToken();
        if (newToken) {
          error.config.headers['Authorization'] = `Bearer ${newToken}`;
          return axios(error.config);
        }
      }
      return Promise.reject(error);
    }
  );
}
