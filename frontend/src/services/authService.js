import api from './api';

export const authService = {
  // Unified login for both staff/admin and regular users
  async login(credentials) {
    const response = await api.post('/api/auth/login', credentials);
    return response.data;
  },

  // Dedicated staff login
  async loginStaff(credentials) {
    const response = await api.post('/api/auth/staff/login', credentials);
    return response.data;
  },

  // Dedicated user login
  async loginUser(credentials) {
    const response = await api.post('/api/auth/user/login', credentials);
    return response.data;
  },

  // Register regular user
  async registerUser(userData) {
    const response = await api.post('/api/auth/register', userData);
    return response.data;
  },

  // Admin registers new staff member
  async registerStaff(staffData) {
    const response = await api.post('/api/auth/staff/register', staffData);
    return response.data;
  },

  // Get all staff members (staff/admin only)
  async getAllStaff() {
    const response = await api.get('/api/staff');
    return response.data;
  },

  // Get current staff profile
  async getMyProfile() {
    const response = await api.get('/api/staff/me');
    return response.data;
  },
};

export default authService;
