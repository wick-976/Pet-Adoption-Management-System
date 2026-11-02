package com.examly.springapp.service;

import com.examly.springapp.dto.request.PetImageRequestDto;
import com.examly.springapp.dto.response.PetImageResponseDto;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetImage;
import com.examly.springapp.repository.PetImageRepository;
import com.examly.springapp.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PetImageServiceImpl implements PetImageService {

    private final PetImageRepository petImageRepository;
    private final PetRepository petRepository;

    public PetImageServiceImpl(PetImageRepository petImageRepository, PetRepository petRepository) {
        this.petImageRepository = petImageRepository;
        this.petRepository = petRepository;
    }

    @Override
    @Transactional
    public PetImageResponseDto addImage(Long petId, PetImageRequestDto requestDto) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        boolean isFirstImage = petImageRepository.findByPet_PetId(petId).isEmpty();
        boolean shouldBePrimary = Boolean.TRUE.equals(requestDto.getIsPrimary()) || isFirstImage;

        if (shouldBePrimary) {
            petImageRepository.resetPrimaryForPet(petId);
        }

        PetImage petImage = PetImage.builder()
                .pet(pet)
                .imageUrl(requestDto.getImageUrl())
                .isPrimary(shouldBePrimary)
                .build();

        PetImage saved = petImageRepository.save(petImage);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetImageResponseDto> getImagesByPetId(Long petId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }
        return petImageRepository.findByPet_PetId(petId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PetImageResponseDto setPrimaryImage(Long petId, Long imageId) {
        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }

        PetImage targetImage = petImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));

        if (!targetImage.getPet().getPetId().equals(petId)) {
            throw new ResourceNotFoundException("Image #" + imageId + " does not belong to pet #" + petId);
        }

        petImageRepository.resetPrimaryForPet(petId);
        targetImage.setIsPrimary(true);
        PetImage updated = petImageRepository.save(targetImage);

        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        PetImage image = petImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with ID: " + imageId));
        Long petId = image.getPet().getPetId();
        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());

        petImageRepository.delete(image);

        // If the deleted image was primary, set another image as primary if available
        if (wasPrimary) {
            List<PetImage> remaining = petImageRepository.findByPet_PetId(petId);
            if (!remaining.isEmpty()) {
                PetImage newPrimary = remaining.get(0);
                newPrimary.setIsPrimary(true);
                petImageRepository.save(newPrimary);
            }
        }
    }

    private PetImageResponseDto mapToDto(PetImage image) {
        return PetImageResponseDto.builder()
                .imageId(image.getImageId())
                .petId(image.getPet().getPetId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}
