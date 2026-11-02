import api from './api';

export const adoptionService = {
  // Public or authenticated user submit adoption request
  async submitAdoptionRequest(requestData) {
    const response = await api.post('/api/adoption-requests', requestData);
    return response.data;
  },

  // Authenticated user get their submitted requests
  async getMyRequests() {
    const response = await api.get('/api/adoption-requests/my-requests');
    return response.data;
  },

  // Staff list all requests with optional status and pet filters
  async getAdoptionRequests(params = {}) {
    const response = await api.get('/api/adoption-requests', { params });
    return response.data;
  },

  // Staff get request details by ID
  async getAdoptionRequestById(id) {
    const response = await api.get(`/api/adoption-requests/${id}`);
    return response.data;
  },

  // Staff update adoption request status
  async updateAdoptionRequestStatus(id, updateData) {
    const response = await api.put(`/api/adoption-requests/${id}/status`, updateData);
    return response.data;
  },
};

export default adoptionService;
