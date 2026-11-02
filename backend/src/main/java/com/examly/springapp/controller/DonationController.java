package com.examly.springapp.controller;

import com.examly.springapp.dto.request.DonationRequestDto;
import com.examly.springapp.dto.response.DonationResponseDto;
import com.examly.springapp.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/donations")
@Tag(name = "Donations", description = "Public shelter support and donation processing endpoints")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    @Operation(summary = "Initiate a donation checkout session with third-party payment gateway")
    public ResponseEntity<DonationResponseDto> initiateDonation(@Valid @RequestBody DonationRequestDto requestDto) {
        DonationResponseDto response = donationService.initiateDonation(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Check status of a donation by ID")
    public ResponseEntity<DonationResponseDto> getDonationStatus(@PathVariable Long id) {
        DonationResponseDto response = donationService.getDonationById(id);
        return ResponseEntity.ok(response);
    }
}
