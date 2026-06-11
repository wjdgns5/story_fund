import axios from "axios";

// Axios 기본 설정
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // .env 백엔드 주소
  withCredentials: true, // Cookie 자동 전송 (Refresh Token)
});

// 요청 인터셉터 — 모든 요청에 토큰 자동 추가
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 응답 인터셉터 — 401 나면 토큰 자동 갱신
api.interceptors.response.use(
  (response) => response, // 성공하면 그대로 반환

  async (error) => {
    const originalRequest = error.config;

    // 401 에러 + 재시도 안 한 요청이면
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Refresh Token 으로 새 Access Token 발급
        const response = await axios.post(
          "http://localhost:8080/api/auth/refresh",
          {},
          { withCredentials: true }, // Cookie 전송
        );

        const newToken = response.data;
        localStorage.setItem("accessToken", newToken);

        // 원래 요청 재시도
        originalRequest.headers.Authorization = `Bearer ${newToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh Token 도 만료됐으면 로그아웃
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
