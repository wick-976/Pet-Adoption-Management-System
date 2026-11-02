import api from './api';

export const donationService = {
  // Public initiate donation
  async initiateDonation(donationData) {
    const response = await api.post('/api/donations', donationData);
    return response.data;
  },

  // Public check donation status
  async getDonationById(id) {
    const response = await api.get(`/api/donations/${id}`);
    return response.data;
  },
};

export default donationService;
