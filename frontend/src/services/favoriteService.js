import api from './api';

export const favoriteService = {
  // Get all favorites for currently logged-in user
  async getFavorites() {
    const response = await api.get('/api/favorites');
    return response.data;
  },

  // Add pet to favorites
  async addFavorite(petId) {
    const response = await api.post(`/api/favorites/${petId}`);
    return response.data;
  },

  // Remove pet from favorites
  async removeFavorite(petId) {
    const response = await api.delete(`/api/favorites/${petId}`);
    return response.data;
  },

  // Check if a pet is favorited
  async checkFavorite(petId) {
    const response = await api.get(`/api/favorites/check/${petId}`);
    // Backend returns ApiResponse<Boolean> where data is boolean
    return response.data?.data ?? false;
  },
};

export default favoriteService;
