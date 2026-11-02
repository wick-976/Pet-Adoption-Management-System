package com.examly.springapp.service;

import com.examly.springapp.config.JwtService;
import com.examly.springapp.dto.request.LoginRequestDto;
import com.examly.springapp.dto.request.StaffRegisterRequestDto;
import com.examly.springapp.dto.response.AuthResponseDto;
import com.examly.springapp.dto.response.StaffResponseDto;
import com.examly.springapp.exception.DuplicateResourceException;
import com.examly.springapp.model.Role;
import com.examly.springapp.model.Staff;
import com.examly.springapp.repository.StaffRepository;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private StaffServiceImpl staffService;

    private Staff sampleStaff;

    @BeforeEach
    void setUp() {
        sampleStaff = Staff.builder()
                .staffId(1L)
                .username("staffuser")
                .email("staff@example.com")
                .passwordHash("hashedPassword123")
                .role(Role.ROLE_STAFF)
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate staff and return JWT")
    void shouldLoginStaffSuccessfully() {
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .username("staffuser")
                .password("Password123!")
                .build();

        when(staffRepository.findByUsername("staffuser")).thenReturn(Optional.of(sampleStaff));
        when(passwordEncoder.matches("Password123!", "hashedPassword123")).thenReturn(true);
        when(jwtService.generateToken("staffuser", "ROLE_STAFF")).thenReturn("mock-jwt-token");
        when(jwtService.getExpirationMs()).thenReturn(86400000L);

        AuthResponseDto response = staffService.loginStaff(loginDto);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("staffuser", response.getUsername());
        assertEquals("ROLE_STAFF", response.getRole());
        verify(staffRepository, times(1)).save(sampleStaff);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid password")
    void shouldThrowOnInvalidPassword() {
        LoginRequestDto loginDto = LoginRequestDto.builder()
                .username("staffuser")
                .password("WrongPassword")
                .build();

        when(staffRepository.findByUsername("staffuser")).thenReturn(Optional.of(sampleStaff));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword123")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> staffService.loginStaff(loginDto));
    }

    @Test
    @DisplayName("Should register new staff member with hashed password")
    void shouldRegisterStaffSuccessfully() {
        StaffRegisterRequestDto requestDto = StaffRegisterRequestDto.builder()
                .username("newstaff")
                .email("newstaff@example.com")
                .password("SecurePass123!")
                .role(Role.ROLE_STAFF)
                .build();

        when(staffRepository.existsByUsername("newstaff")).thenReturn(false);
        when(staffRepository.existsByEmail("newstaff@example.com")).thenReturn(false);
        when(passwordEncoder.encode("SecurePass123!")).thenReturn("encodedHashValue");
        when(staffRepository.save(any(Staff.class))).thenAnswer(invocation -> {
            Staff s = invocation.getArgument(0);
            s.setStaffId(2L);
            return s;
        });

        StaffResponseDto response = staffService.registerStaff(requestDto);

        assertNotNull(response);
        assertEquals("newstaff", response.getUsername());
        assertEquals("ROLE_STAFF", response.getRole());
        verify(passwordEncoder, times(1)).encode("SecurePass123!");
        verify(staffRepository, times(1)).save(any(Staff.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException if staff username exists")
    void shouldThrowIfUsernameExists() {
        StaffRegisterRequestDto requestDto = StaffRegisterRequestDto.builder()
                .username("existinguser")
                .email("test@example.com")
                .password("Password123!")
                .build();

        when(staffRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> staffService.registerStaff(requestDto));
        verify(staffRepository, never()).save(any());
    }
}
