package com.examly.springapp.controller;

import com.examly.springapp.dto.response.StaffResponseDto;
import com.examly.springapp.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@Tag(name = "Staff Management", description = "Staff administrative operations")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    @Operation(summary = "List all staff members")
    public ResponseEntity<List<StaffResponseDto>> getAllStaff() {
        List<StaffResponseDto> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated staff profile")
    public ResponseEntity<StaffResponseDto> getMyProfile(Authentication authentication) {
        StaffResponseDto profile = staffService.getStaffByUsername(authentication.getName());
        return ResponseEntity.ok(profile);
    }
}
