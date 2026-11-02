import api from './api';

export const shelterService = {
  // Public get shelter details
  async getShelterInfo() {
    const response = await api.get('/api/shelter');
    return response.data;
  },

  // Staff update shelter details
  async updateShelterInfo(shelterData) {
    const response = await api.put('/api/shelter', shelterData);
    return response.data;
  },
};

export default shelterService;
