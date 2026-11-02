package com.examly.springapp.controller;

import com.examly.springapp.dto.request.LoginRequestDto;
import com.examly.springapp.dto.request.RegisterRequestDto;
import com.examly.springapp.dto.request.StaffRegisterRequestDto;
import com.examly.springapp.dto.response.AuthResponseDto;
import com.examly.springapp.dto.response.StaffResponseDto;
import com.examly.springapp.dto.response.UserResponseDto;
import com.examly.springapp.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and registration endpoints for Staff and Users")
public class AuthController {

    private final StaffService staffService;

    public AuthController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/login")
    @Operation(summary = "Unified login for staff and users (tries staff first, then user)")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginDto) {
        try {
            AuthResponseDto staffAuth = staffService.loginStaff(loginDto);
            return ResponseEntity.ok(staffAuth);
        } catch (BadCredentialsException e) {
            AuthResponseDto userAuth = staffService.loginUser(loginDto);
            return ResponseEntity.ok(userAuth);
        }
    }

    @PostMapping("/staff/login")
    @Operation(summary = "Staff / Admin specific login")
    public ResponseEntity<AuthResponseDto> loginStaff(@Valid @RequestBody LoginRequestDto loginDto) {
        AuthResponseDto response = staffService.loginStaff(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/login")
    @Operation(summary = "Registered user specific login")
    public ResponseEntity<AuthResponseDto> loginUser(@Valid @RequestBody LoginRequestDto loginDto) {
        AuthResponseDto response = staffService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new regular user account")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterRequestDto requestDto) {
        UserResponseDto user = staffService.registerUser(requestDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/staff/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin registers a new staff member")
    public ResponseEntity<StaffResponseDto> registerStaff(@Valid @RequestBody StaffRegisterRequestDto requestDto) {
        StaffResponseDto staff = staffService.registerStaff(requestDto);
        return new ResponseEntity<>(staff, HttpStatus.CREATED);
    }
}
