package com.examly.springapp.controller;

import com.examly.springapp.dto.request.AdoptionRequestCreateDto;
import com.examly.springapp.dto.request.AdoptionStatusUpdateDto;
import com.examly.springapp.dto.response.AdoptionRequestResponseDto;
import com.examly.springapp.model.AdoptionStatus;
import com.examly.springapp.service.AdoptionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption-requests")
@Tag(name = "Adoption Requests", description = "Adoption application submission and status management endpoints")
public class AdoptionRequestController {

    private final AdoptionRequestService adoptionRequestService;

    public AdoptionRequestController(AdoptionRequestService adoptionRequestService) {
        this.adoptionRequestService = adoptionRequestService;
    }

    @PostMapping
    @Operation(summary = "Submit an adoption request for an available pet")
    public ResponseEntity<AdoptionRequestResponseDto> submitRequest(
            @Valid @RequestBody AdoptionRequestCreateDto requestDto,
            Authentication authentication) {
        String currentUsername = authentication != null ? authentication.getName() : null;
        AdoptionRequestResponseDto response = adoptionRequestService.submitAdoptionRequest(requestDto, currentUsername);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "List all adoption requests with optional status and pet filters")
    public ResponseEntity<Page<AdoptionRequestResponseDto>> getAdoptionRequests(
            @RequestParam(required = false) AdoptionStatus status,
            @RequestParam(required = false) Long petId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submissionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AdoptionRequestResponseDto> requests = adoptionRequestService.getAdoptionRequests(status, petId, pageable);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Get adoption request details by ID")
    public ResponseEntity<AdoptionRequestResponseDto> getAdoptionRequestById(@PathVariable Long id) {
        AdoptionRequestResponseDto request = adoptionRequestService.getAdoptionRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Update adoption request status (APPROVED -> ADOPTED, REJECTED -> AVAILABLE if no other pending requests)")
    public ResponseEntity<AdoptionRequestResponseDto> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody AdoptionStatusUpdateDto updateDto) {
        AdoptionRequestResponseDto updated = adoptionRequestService.updateAdoptionRequestStatus(id, updateDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
    @Operation(summary = "Get adoption requests submitted by the currently logged-in user")
    public ResponseEntity<List<AdoptionRequestResponseDto>> getMyRequests(Authentication authentication) {
        List<AdoptionRequestResponseDto> myRequests = adoptionRequestService.getAdoptionRequestsByUser(authentication.getName());
        return ResponseEntity.ok(myRequests);
    }
}
