package com.examly.springapp.controller;

import com.examly.springapp.config.JwtService;
import com.examly.springapp.dto.request.PetCreateRequestDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.dto.response.PetStaffResponseDto;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    @Test
    @DisplayName("Public user can browse pets without authentication")
    void shouldAllowPublicBrowsing() throws Exception {
        PetPublicResponseDto petDto = PetPublicResponseDto.builder()
                .petId(1L)
                .name("Max")
                .species("Dog")
                .age(2)
                .adoptionStatus(PetStatus.AVAILABLE)
                .viewCount(10L)
                .build();

        when(petService.searchPetsPublic(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(petDto)));

        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Max"))
                .andExpect(jsonPath("$.content[0].adoptionStatus").value("AVAILABLE"))
                // Ensure staff-only fields are NOT in public response
                .andExpect(jsonPath("$.content[0].vaccinationStatus").doesNotExist())
                .andExpect(jsonPath("$.content[0].spayNeuterStatus").doesNotExist());
    }

    @Test
    @DisplayName("Public user can view pet details by ID")
    void shouldAllowPublicPetDetails() throws Exception {
        PetPublicResponseDto petDto = PetPublicResponseDto.builder()
                .petId(1L)
                .name("Max")
                .species("Dog")
                .age(2)
                .adoptionStatus(PetStatus.AVAILABLE)
                .viewCount(11L)
                .build();

        when(petService.getPetPublicById(1L)).thenReturn(petDto);

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max"))
                .andExpect(jsonPath("$.viewCount").value(11));
    }

    @Test
    @DisplayName("Unauthenticated user cannot create a pet (401 or 403)")
    void shouldBlockPetCreationWithoutAuth() throws Exception {
        PetCreateRequestDto request = PetCreateRequestDto.builder()
                .name("New Pet")
                .species("Dog")
                .age(2)
                .build();

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staffuser", roles = {"STAFF"})
    @DisplayName("Staff user can create a pet")
    void shouldAllowStaffToCreatePet() throws Exception {
        PetCreateRequestDto request = PetCreateRequestDto.builder()
                .name("Cooper")
                .species("Dog")
                .breed("Beagle")
                .age(2)
                .description("Friendly beagle")
                .vaccinationStatus("Up to date")
                .spayNeuterStatus("Neutered")
                .specialMedicalNeeds("None")
                .build();

        PetStaffResponseDto response = PetStaffResponseDto.builder()
                .petId(5L)
                .name("Cooper")
                .species("Dog")
                .breed("Beagle")
                .age(2)
                .adoptionStatus(PetStatus.AVAILABLE)
                .vaccinationStatus("Up to date")
                .build();

        when(petService.createPet(any(PetCreateRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cooper"))
                .andExpect(jsonPath("$.adoptionStatus").value("AVAILABLE"))
                .andExpect(jsonPath("$.vaccinationStatus").value("Up to date"));
    }

    @Test
    @WithMockUser(username = "staffuser", roles = {"STAFF"})
    @DisplayName("Invalid pet creation input should return 400 Bad Request with validation errors")
    void shouldReturnBadRequestOnInvalidPetInput() throws Exception {
        // Missing name and species
        PetCreateRequestDto request = PetCreateRequestDto.builder()
                .age(-5) // Invalid negative age
                .build();

        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
