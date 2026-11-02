package com.examly.springapp.service;

import com.examly.springapp.dto.request.PetCreateRequestDto;
import com.examly.springapp.dto.request.PetUpdateRequestDto;
import com.examly.springapp.dto.response.PetImageResponseDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.dto.response.PetStaffResponseDto;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetImage;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.repository.PetImageRepository;
import com.examly.springapp.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;

    public PetServiceImpl(PetRepository petRepository, PetImageRepository petImageRepository) {
        this.petRepository = petRepository;
        this.petImageRepository = petImageRepository;
    }

    @Override
    @Transactional
    public PetStaffResponseDto createPet(PetCreateRequestDto requestDto) {
        Pet pet = Pet.builder()
                .name(requestDto.getName().trim())
                .species(requestDto.getSpecies().trim())
                .breed(requestDto.getBreed() != null ? requestDto.getBreed().trim() : null)
                .age(requestDto.getAge())
                .description(requestDto.getDescription())
                .adoptionStatus(PetStatus.AVAILABLE)
                .viewCount(0L)
                .vaccinationStatus(requestDto.getVaccinationStatus())
                .spayNeuterStatus(requestDto.getSpayNeuterStatus())
                .specialMedicalNeeds(requestDto.getSpecialMedicalNeeds())
                .build();

        Pet savedPet = petRepository.save(pet);

        if (requestDto.getInitialImageUrl() != null && !requestDto.getInitialImageUrl().isBlank()) {
            PetImage initialImage = PetImage.builder()
                    .pet(savedPet)
                    .imageUrl(requestDto.getInitialImageUrl().trim())
                    .isPrimary(true)
                    .build();
            petImageRepository.save(initialImage);
            savedPet.getImages().add(initialImage);
        }

        return mapToStaffDto(savedPet);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetPublicResponseDto> searchPetsPublic(
            String name,
            String breed,
            String species,
            Integer age,
            PetStatus adoptionStatus,
            Pageable pageable) {
        // Public browsing defaults to AVAILABLE if not specified
        PetStatus effectiveStatus = adoptionStatus != null ? adoptionStatus : PetStatus.AVAILABLE;

        Page<Pet> pets = petRepository.searchPets(
                emptyToNull(name),
                emptyToNull(breed),
                emptyToNull(species),
                age,
                effectiveStatus,
                pageable
        );

        return pets.map(this::mapToPublicDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PetStaffResponseDto> searchPetsStaff(
            String name,
            String breed,
            String species,
            Integer age,
            PetStatus adoptionStatus,
            Pageable pageable) {
        Page<Pet> pets = petRepository.searchPets(
                emptyToNull(name),
                emptyToNull(breed),
                emptyToNull(species),
                age,
                adoptionStatus,
                pageable
        );

        return pets.map(this::mapToStaffDto);
    }

    @Override
    @Transactional
    public PetPublicResponseDto getPetPublicById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        // Increment public view count
        pet.setViewCount(pet.getViewCount() + 1);
        petRepository.save(pet);

        return mapToPublicDto(pet);
    }

    @Override
    @Transactional(readOnly = true)
    public PetStaffResponseDto getPetStaffById(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        return mapToStaffDto(pet);
    }

    @Override
    @Transactional
    public PetStaffResponseDto updatePet(Long petId, PetUpdateRequestDto requestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        pet.setName(requestDto.getName().trim());
        pet.setSpecies(requestDto.getSpecies().trim());
        pet.setBreed(requestDto.getBreed() != null ? requestDto.getBreed().trim() : null);
        pet.setAge(requestDto.getAge());
        pet.setDescription(requestDto.getDescription());

        if (requestDto.getAdoptionStatus() != null) {
            pet.setAdoptionStatus(requestDto.getAdoptionStatus());
        }

        pet.setVaccinationStatus(requestDto.getVaccinationStatus());
        pet.setSpayNeuterStatus(requestDto.getSpayNeuterStatus());
        pet.setSpecialMedicalNeeds(requestDto.getSpecialMedicalNeeds());

        Pet updated = petRepository.save(pet);
        return mapToStaffDto(updated);
    }

    @Override
    @Transactional
    public PetStaffResponseDto updatePetStatus(Long petId, PetStatus adoptionStatus) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        pet.setAdoptionStatus(adoptionStatus);
        Pet updated = petRepository.save(pet);
        return mapToStaffDto(updated);
    }

    @Override
    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));
        petRepository.delete(pet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetPublicResponseDto> getMostViewedPets(int limit) {
        Page<Pet> topPets = petRepository.findAll(PageRequest.of(0, limit, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "viewCount")));
        return topPets.stream()
                .map(this::mapToPublicDto)
                .collect(Collectors.toList());
    }

    private PetPublicResponseDto mapToPublicDto(Pet pet) {
        List<PetImageResponseDto> imageDtos = pet.getImages() != null
                ? pet.getImages().stream().map(this::mapImageToDto).collect(Collectors.toList())
                : Collections.emptyList();

        String primaryUrl = imageDtos.stream()
                .filter(PetImageResponseDto::getIsPrimary)
                .map(PetImageResponseDto::getImageUrl)
                .findFirst()
                .orElse(imageDtos.isEmpty() ? null : imageDtos.get(0).getImageUrl());

        return PetPublicResponseDto.builder()
                .petId(pet.getPetId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .description(pet.getDescription())
                .adoptionStatus(pet.getAdoptionStatus())
                .primaryImageUrl(primaryUrl)
                .images(imageDtos)
                .viewCount(pet.getViewCount())
                .build();
    }

    private PetStaffResponseDto mapToStaffDto(Pet pet) {
        List<PetImageResponseDto> imageDtos = pet.getImages() != null
                ? pet.getImages().stream().map(this::mapImageToDto).collect(Collectors.toList())
                : Collections.emptyList();

        String primaryUrl = imageDtos.stream()
                .filter(PetImageResponseDto::getIsPrimary)
                .map(PetImageResponseDto::getImageUrl)
                .findFirst()
                .orElse(imageDtos.isEmpty() ? null : imageDtos.get(0).getImageUrl());

        return PetStaffResponseDto.builder()
                .petId(pet.getPetId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .age(pet.getAge())
                .description(pet.getDescription())
                .adoptionStatus(pet.getAdoptionStatus())
                .viewCount(pet.getViewCount())
                .vaccinationStatus(pet.getVaccinationStatus())
                .spayNeuterStatus(pet.getSpayNeuterStatus())
                .specialMedicalNeeds(pet.getSpecialMedicalNeeds())
                .creationDate(pet.getCreationDate())
                .lastUpdatedDate(pet.getLastUpdatedDate())
                .primaryImageUrl(primaryUrl)
                .images(imageDtos)
                .build();
    }

    private PetImageResponseDto mapImageToDto(PetImage image) {
        return PetImageResponseDto.builder()
                .imageId(image.getImageId())
                .petId(image.getPet() != null ? image.getPet().getPetId() : null)
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .uploadedAt(image.getUploadedAt())
                .build();
    }

    private String emptyToNull(String str) {
        return (str != null && !str.trim().isEmpty()) ? str.trim() : null;
    }
}
