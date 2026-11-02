package com.examly.springapp.controller;

import com.examly.springapp.dto.request.ShelterUpdateDto;
import com.examly.springapp.dto.response.ShelterResponseDto;
import com.examly.springapp.service.ShelterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelter")
@Tag(name = "Shelter Information", description = "Public shelter info retrieval and staff update endpoints")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    @GetMapping
    @Operation(summary = "Get shelter public profile, contact details, and operating hours")
    public ResponseEntity<ShelterResponseDto> getShelter() {
        ShelterResponseDto shelter = shelterService.getShelterInfo();
        return ResponseEntity.ok(shelter);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Update shelter profile, contact details, and operating hours")
    public ResponseEntity<ShelterResponseDto> updateShelter(@Valid @RequestBody ShelterUpdateDto updateDto) {
        ShelterResponseDto updated = shelterService.updateShelterInfo(updateDto);
        return ResponseEntity.ok(updated);
    }
}
