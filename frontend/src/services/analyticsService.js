import api from './api';

export const analyticsService = {
  // Overall analytics
  async getOverallAnalytics() {
    const response = await api.get('/api/analytics');
    return response.data;
  },

  // Adoption statistics for period
  async getAdoptionAnalytics(startDate, endDate) {
    const params = {};
    if (startDate) params.startDate = startDate;
    if (endDate) params.endDate = endDate;
    const response = await api.get('/api/analytics/adoptions', { params });
    return response.data;
  },

  // Pet statistics and species breakdown
  async getPetAnalytics() {
    const response = await api.get('/api/analytics/pets');
    return response.data;
  },

  // Export-ready analytics data
  async getExportData() {
    const response = await api.get('/api/analytics/export');
    return response.data;
  },
};

export default analyticsService;
