package com.examly.springapp.controller;

import com.examly.springapp.dto.request.AdoptionRequestCreateDto;
import com.examly.springapp.dto.request.AdoptionStatusUpdateDto;
import com.examly.springapp.dto.response.AdoptionRequestResponseDto;
import com.examly.springapp.model.AdoptionStatus;
import com.examly.springapp.service.AdoptionRequestService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdoptionRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdoptionRequestService adoptionRequestService;

    @Test
    @DisplayName("Public user can submit an adoption request")
    void shouldAllowPublicAdoptionSubmission() throws Exception {
        AdoptionRequestCreateDto request = AdoptionRequestCreateDto.builder()
                .petId(1L)
                .applicantName("Alice Smith")
                .applicantEmail("alice@example.com")
                .applicantPhone("+1-555-1234567")
                .housingType("House")
                .hasOtherPets(false)
                .reasonForAdoption("Great home for pet")
                .build();

        AdoptionRequestResponseDto response = AdoptionRequestResponseDto.builder()
                .requestId(100L)
                .petId(1L)
                .petName("Max")
                .applicantName("Alice Smith")
                .applicantEmail("alice@example.com")
                .applicantPhone("+1-555-1234567")
                .status(AdoptionStatus.PENDING)
                .build();

        when(adoptionRequestService.submitAdoptionRequest(any(AdoptionRequestCreateDto.class), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/adoption-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.requestId").value(100))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.applicantEmail").value("alice@example.com"));
    }

    @Test
    @DisplayName("Invalid email in adoption submission should return 400 Bad Request")
    void shouldReturnBadRequestOnInvalidEmail() throws Exception {
        AdoptionRequestCreateDto request = AdoptionRequestCreateDto.builder()
                .petId(1L)
                .applicantName("Alice Smith")
                .applicantEmail("not-an-email")
                .applicantPhone("+1-555-1234567")
                .build();

        mockMvc.perform(post("/api/adoption-requests")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.applicantEmail").exists());
    }

    @Test
    @DisplayName("Non-staff cannot view adoption requests list (401 or 403)")
    void shouldBlockUnauthorizedRequestsListing() throws Exception {
        mockMvc.perform(get("/api/adoption-requests"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staffuser", roles = {"STAFF"})
    @DisplayName("Staff can view all adoption requests")
    void shouldAllowStaffToListRequests() throws Exception {
        AdoptionRequestResponseDto response = AdoptionRequestResponseDto.builder()
                .requestId(100L)
                .petId(1L)
                .applicantName("Alice Smith")
                .status(AdoptionStatus.PENDING)
                .build();

        when(adoptionRequestService.getAdoptionRequests(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/adoption-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].requestId").value(100));
    }

    @Test
    @WithMockUser(username = "staffuser", roles = {"STAFF"})
    @DisplayName("Staff can update adoption request status")
    void shouldAllowStaffToUpdateStatus() throws Exception {
        AdoptionStatusUpdateDto updateDto = AdoptionStatusUpdateDto.builder()
                .status(AdoptionStatus.APPROVED)
                .staffNotes("Home approved")
                .build();

        AdoptionRequestResponseDto response = AdoptionRequestResponseDto.builder()
                .requestId(100L)
                .petId(1L)
                .status(AdoptionStatus.APPROVED)
                .staffNotes("Home approved")
                .build();

        when(adoptionRequestService.updateAdoptionRequestStatus(eq(100L), any(AdoptionStatusUpdateDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/adoption-requests/100/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
