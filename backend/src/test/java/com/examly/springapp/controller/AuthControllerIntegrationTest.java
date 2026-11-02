package com.examly.springapp.controller;

import com.examly.springapp.dto.request.LoginRequestDto;
import com.examly.springapp.dto.request.RegisterRequestDto;
import com.examly.springapp.dto.response.AuthResponseDto;
import com.examly.springapp.dto.response.UserResponseDto;
import com.examly.springapp.service.StaffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffService staffService;

    @Test
    @DisplayName("Should allow new user registration")
    void shouldRegisterUser() throws Exception {
        RegisterRequestDto request = RegisterRequestDto.builder()
                .username("newuser123")
                .email("newuser@example.com")
                .password("Secret123!")
                .build();

        UserResponseDto response = UserResponseDto.builder()
                .userId(1L)
                .username("newuser123")
                .email("newuser@example.com")
                .role("ROLE_USER")
                .build();

        when(staffService.registerUser(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser123"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    @DisplayName("Should authenticate and return JWT token on login")
    void shouldLoginAndReturnJwt() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("teststaff")
                .password("Password123!")
                .build();

        AuthResponseDto response = AuthResponseDto.builder()
                .token("sample.jwt.token")
                .tokenType("Bearer")
                .id(1L)
                .username("teststaff")
                .role("ROLE_STAFF")
                .expiresIn(86400000L)
                .build();

        when(staffService.loginStaff(any(LoginRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("sample.jwt.token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("teststaff"));
    }
}
