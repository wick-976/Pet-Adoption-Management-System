package com.examly.springapp.service;

import com.examly.springapp.dto.response.FavoritePetResponseDto;
import com.examly.springapp.dto.response.PetImageResponseDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.exception.DuplicateResourceException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.FavoritePet;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetImage;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FavoritePetRepository;
import com.examly.springapp.repository.PetRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritePetServiceImpl implements FavoritePetService {

    private final FavoritePetRepository favoritePetRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;

    public FavoritePetServiceImpl(
            FavoritePetRepository favoritePetRepository,
            UserRepository userRepository,
            PetRepository petRepository) {
        this.favoritePetRepository = favoritePetRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
    }

    @Override
    @Transactional
    public FavoritePetResponseDto addFavorite(String username, Long petId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + petId));

        if (favoritePetRepository.existsByUser_UserIdAndPet_PetId(user.getUserId(), petId)) {
            throw new DuplicateResourceException("Pet #" + petId + " is already in your favorites list.");
        }

        FavoritePet favorite = FavoritePet.builder()
                .user(user)
                .pet(pet)
                .build();

        FavoritePet saved = favoritePetRepository.save(favorite);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void removeFavorite(String username, Long petId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }

        FavoritePet favorite = favoritePetRepository.findByUser_UserIdAndPet_PetId(user.getUserId(), petId)
                .orElseThrow(() -> new ResourceNotFoundException("Pet #" + petId + " is not in your favorites."));

        favoritePetRepository.delete(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoritePetResponseDto> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return favoritePetRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPetFavorited(String username, Long petId) {
        return userRepository.findByUsername(username)
                .map(user -> favoritePetRepository.existsByUser_UserIdAndPet_PetId(user.getUserId(), petId))
                .orElse(false);
    }

    private FavoritePetResponseDto mapToDto(FavoritePet fav) {
        Pet pet = fav.getPet();
        List<PetImageResponseDto> imageDtos = pet.getImages() != null
                ? pet.getImages().stream().map(this::mapImageToDto).collect(Collectors.toList())
                : Collections.emptyList();

        String primaryUrl = imageDtos.stream()
                .filter(PetImageResponseDto::getIsPrimary)
                .map(PetImageResponseDto::getImageUrl)
                .findFirst()
                .orElse(imageDtos.isEmpty() ? null : imageDtos.get(0).getImageUrl());

        PetPublicResponseDto petDto = PetPublicResponseDto.builder()
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

        return FavoritePetResponseDto.builder()
                .favoriteId(fav.getFavoriteId())
                .userId(fav.getUser().getUserId())
                .pet(petDto)
                .favoritedAt(fav.getFavoritedAt())
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
}
