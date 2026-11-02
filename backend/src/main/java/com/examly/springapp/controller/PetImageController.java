package com.examly.springapp.controller;

import com.examly.springapp.dto.request.PetImageRequestDto;
import com.examly.springapp.dto.response.ApiResponse;
import com.examly.springapp.dto.response.PetImageResponseDto;
import com.examly.springapp.service.PetImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet Images", description = "Pet image management endpoints")
public class PetImageController {

    private final PetImageService petImageService;

    public PetImageController(PetImageService petImageService) {
        this.petImageService = petImageService;
    }

    @PostMapping("/{petId}/images")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Add an image to a pet")
    public ResponseEntity<PetImageResponseDto> addImage(
            @PathVariable Long petId,
            @Valid @RequestBody PetImageRequestDto requestDto) {
        PetImageResponseDto created = petImageService.addImage(petId, requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{petId}/images")
    @Operation(summary = "Get all images for a pet")
    public ResponseEntity<List<PetImageResponseDto>> getImages(@PathVariable Long petId) {
        List<PetImageResponseDto> images = petImageService.getImagesByPetId(petId);
        return ResponseEntity.ok(images);
    }

    @PutMapping("/{petId}/images/{imageId}/primary")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Set an image as primary for a pet")
    public ResponseEntity<PetImageResponseDto> setPrimaryImage(
            @PathVariable Long petId,
            @PathVariable Long imageId) {
        PetImageResponseDto updated = petImageService.setPrimaryImage(petId, imageId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Delete a pet image")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        petImageService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image #" + imageId + " deleted successfully.", null));
    }
}
