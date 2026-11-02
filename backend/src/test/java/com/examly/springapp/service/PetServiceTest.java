package com.examly.springapp.service;

import com.examly.springapp.dto.request.PetCreateRequestDto;
import com.examly.springapp.dto.request.PetUpdateRequestDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.dto.response.PetStaffResponseDto;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.repository.PetImageRepository;
import com.examly.springapp.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetImageRepository petImageRepository;

    @InjectMocks
    private PetServiceImpl petService;

    private Pet samplePet;

    @BeforeEach
    void setUp() {
        samplePet = Pet.builder()
                .petId(1L)
                .name("Buddy")
                .species("Dog")
                .breed("Golden Retriever")
                .age(2)
                .description("Friendly and energetic")
                .adoptionStatus(PetStatus.AVAILABLE)
                .viewCount(5L)
                .vaccinationStatus("Vaccinated")
                .spayNeuterStatus("Neutered")
                .specialMedicalNeeds("None")
                .images(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should create pet with default status AVAILABLE and view count 0")
    void shouldCreatePetSuccessfully() {
        PetCreateRequestDto createDto = PetCreateRequestDto.builder()
                .name("Buddy")
                .species("Dog")
                .breed("Golden Retriever")
                .age(2)
                .description("Friendly and energetic")
                .vaccinationStatus("Vaccinated")
                .spayNeuterStatus("Neutered")
                .specialMedicalNeeds("None")
                .build();

        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet p = invocation.getArgument(0);
            p.setPetId(1L);
            return p;
        });

        PetStaffResponseDto result = petService.createPet(createDto);

        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertEquals(PetStatus.AVAILABLE, result.getAdoptionStatus());
        assertEquals(0L, result.getViewCount());
        assertEquals("Vaccinated", result.getVaccinationStatus());
        verify(petRepository, times(1)).save(any(Pet.class));
    }

    @Test
    @DisplayName("Should get public pet profile and increment view count")
    void shouldGetPublicPetProfileAndIncrementViewCount() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));
        when(petRepository.save(any(Pet.class))).thenReturn(samplePet);

        PetPublicResponseDto result = petService.getPetPublicById(1L);

        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertEquals(6L, result.getViewCount());
        verify(petRepository, times(1)).save(samplePet);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when pet does not exist")
    void shouldThrowWhenPetNotFound() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> petService.getPetPublicById(99L));
    }

    @Test
    @DisplayName("Should get staff profile with health information")
    void shouldGetStaffProfileWithHealthInfo() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));

        PetStaffResponseDto result = petService.getPetStaffById(1L);

        assertNotNull(result);
        assertEquals("Vaccinated", result.getVaccinationStatus());
        assertEquals("Neutered", result.getSpayNeuterStatus());
        assertEquals("None", result.getSpecialMedicalNeeds());
    }

    @Test
    @DisplayName("Should search pets with filters")
    void shouldSearchPets() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pet> page = new PageImpl<>(List.of(samplePet));

        when(petRepository.searchPets(eq("Buddy"), isNull(), eq("Dog"), isNull(), eq(PetStatus.AVAILABLE), any(Pageable.class)))
                .thenReturn(page);

        Page<PetPublicResponseDto> result = petService.searchPetsPublic("Buddy", null, "Dog", null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Buddy", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should update pet details")
    void shouldUpdatePetDetails() {
        PetUpdateRequestDto updateDto = PetUpdateRequestDto.builder()
                .name("Buddy Updated")
                .species("Dog")
                .breed("Golden Retriever")
                .age(3)
                .description("Updated description")
                .adoptionStatus(PetStatus.PENDING)
                .vaccinationStatus("Fully vaccinated")
                .spayNeuterStatus("Neutered")
                .specialMedicalNeeds("Special diet")
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PetStaffResponseDto result = petService.updatePet(1L, updateDto);

        assertNotNull(result);
        assertEquals("Buddy Updated", result.getName());
        assertEquals(3, result.getAge());
        assertEquals(PetStatus.PENDING, result.getAdoptionStatus());
        assertEquals("Special diet", result.getSpecialMedicalNeeds());
    }

    @Test
    @DisplayName("Should delete pet")
    void shouldDeletePet() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(samplePet));
        doNothing().when(petRepository).delete(samplePet);

        assertDoesNotThrow(() -> petService.deletePet(1L));
        verify(petRepository, times(1)).delete(samplePet);
    }
}
