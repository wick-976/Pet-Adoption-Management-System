package com.examly.springapp.controller;

import com.examly.springapp.dto.request.PetCreateRequestDto;
import com.examly.springapp.dto.request.PetStatusUpdateDto;
import com.examly.springapp.dto.request.PetUpdateRequestDto;
import com.examly.springapp.dto.response.ApiResponse;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.dto.response.PetStaffResponseDto;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.service.PetService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet catalog, searching, filtering, and management endpoints")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    @Operation(summary = "Search and filter pets for public catalog")
    public ResponseEntity<Page<PetPublicResponseDto>> searchPets(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) PetStatus adoptionStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PetPublicResponseDto> result = petService.searchPetsPublic(name, breed, species, age, adoptionStatus, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed public pet profile (increments view count)")
    public ResponseEntity<PetPublicResponseDto> getPetById(@PathVariable Long id) {
        PetPublicResponseDto pet = petService.getPetPublicById(id);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("/most-viewed")
    @Operation(summary = "Get most viewed pet profiles")
    public ResponseEntity<List<PetPublicResponseDto>> getMostViewedPets(
            @RequestParam(defaultValue = "5") int limit) {
        List<PetPublicResponseDto> pets = petService.getMostViewedPets(limit);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Staff pet catalog including medical and administrative information")
    public ResponseEntity<Page<PetStaffResponseDto>> searchPetsStaff(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) PetStatus adoptionStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "creationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PetStaffResponseDto> result = petService.searchPetsStaff(name, breed, species, age, adoptionStatus, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/staff/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Get detailed pet profile with medical records for staff")
    public ResponseEntity<PetStaffResponseDto> getPetStaffById(@PathVariable Long id) {
        PetStaffResponseDto pet = petService.getPetStaffById(id);
        return ResponseEntity.ok(pet);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Create a new pet listing")
    public ResponseEntity<PetStaffResponseDto> createPet(@Valid @RequestBody PetCreateRequestDto requestDto) {
        PetStaffResponseDto created = petService.createPet(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Update pet details")
    public ResponseEntity<PetStaffResponseDto> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetUpdateRequestDto requestDto) {
        PetStaffResponseDto updated = petService.updatePet(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Update pet adoption status")
    public ResponseEntity<PetStaffResponseDto> updatePetStatus(
            @PathVariable Long id,
            @Valid @RequestBody PetStatusUpdateDto statusDto) {
        PetStaffResponseDto updated = petService.updatePetStatus(id, statusDto.getAdoptionStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
    @Operation(summary = "Delete a pet listing")
    public ResponseEntity<ApiResponse<Void>> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok(ApiResponse.success("Pet #" + id + " was deleted successfully.", null));
    }
}
