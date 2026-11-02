import axios from 'axios';

// Get base URL from environment or default to local backend
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token if present
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('pet_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor: Handle errors cleanly
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      if (status === 401) {
        // Clear auth state if unauthorized (e.g., token expired)
        localStorage.removeItem('pet_token');
        localStorage.removeItem('pet_user');
        window.dispatchEvent(new Event('auth:unauthorized'));
      }

      // Extract user-friendly error message from backend ErrorResponseDto
      let message = data?.message || 'An unexpected error occurred.';
      if (data?.errors && typeof data.errors === 'object') {
        const fieldErrors = Object.values(data.errors).join(', ');
        if (fieldErrors) {
          message = `${message}: ${fieldErrors}`;
        }
      }

      const enhancedError = new Error(message);
      enhancedError.status = status;
      enhancedError.data = data;
      enhancedError.originalError = error;
      return Promise.reject(enhancedError);
    } else if (error.request) {
      return Promise.reject(new Error('Network error: Unable to connect to server. Please ensure the backend is running.'));
    }
    return Promise.reject(error);
  }
);

export default api;
