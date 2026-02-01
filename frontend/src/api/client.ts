import axios from "axios";
import { useAuth } from "../modules/auth/AuthContext";

export const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const apiClient = axios.create({
  baseURL: `${apiBaseUrl}/api`
});

export const attachAuthInterceptor = (getToken: () => string | null) => {
  apiClient.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });
};

export const useApiClient = () => {
  const { token } = useAuth();
  attachAuthInterceptor(() => token);
  return apiClient;
};

