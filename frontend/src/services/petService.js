import api from './api';

export const petService = {
  // Public pet search and filtering
  async searchPets(params = {}) {
    const response = await api.get('/api/pets', { params });
    return response.data;
  },

  // Public pet profile by ID (increments view count)
  async getPetById(id) {
    const response = await api.get(`/api/pets/${id}`);
    return response.data;
  },

  // Public most viewed pets
  async getMostViewedPets(limit = 5) {
    const response = await api.get('/api/pets/most-viewed', { params: { limit } });
    return response.data;
  },

  // Public get pet images
  async getPetImages(petId) {
    const response = await api.get(`/api/pets/${petId}/images`);
    return response.data;
  },

  // Staff search pets with medical and admin info
  async searchPetsStaff(params = {}) {
    const response = await api.get('/api/pets/staff', { params });
    return response.data;
  },

  // Staff get pet with medical info
  async getPetStaffById(id) {
    const response = await api.get(`/api/pets/staff/${id}`);
    return response.data;
  },

  // Staff create pet
  async createPet(petData) {
    const response = await api.post('/api/pets', petData);
    return response.data;
  },

  // Staff update pet
  async updatePet(id, petData) {
    const response = await api.put(`/api/pets/${id}`, petData);
    return response.data;
  },

  // Staff update pet status
  async updatePetStatus(id, adoptionStatus) {
    const response = await api.put(`/api/pets/${id}/status`, { adoptionStatus });
    return response.data;
  },

  // Staff delete pet
  async deletePet(id) {
    const response = await api.delete(`/api/pets/${id}`);
    return response.data;
  },

  // Staff add image to pet
  async addPetImage(petId, imageData) {
    const response = await api.post(`/api/pets/${petId}/images`, imageData);
    return response.data;
  },

  // Staff set image as primary
  async setPrimaryImage(petId, imageId) {
    const response = await api.put(`/api/pets/${petId}/images/${imageId}/primary`);
    return response.data;
  },

  // Staff delete pet image
  async deletePetImage(imageId) {
    const response = await api.delete(`/api/pets/images/${imageId}`);
    return response.data;
  },
};

export default petService;
