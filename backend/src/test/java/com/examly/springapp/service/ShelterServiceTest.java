package com.examly.springapp.service;

import com.examly.springapp.dto.request.ShelterUpdateDto;
import com.examly.springapp.dto.response.ShelterResponseDto;
import com.examly.springapp.model.Shelter;
import com.examly.springapp.repository.ShelterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShelterServiceTest {

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private ShelterServiceImpl shelterService;

    @Test
    @DisplayName("Should return existing shelter info")
    void shouldReturnExistingShelter() {
        Shelter shelter = Shelter.builder()
                .shelterId(1L)
                .name("Happy Paws")
                .address("123 Main St")
                .phone("+1234567890")
                .email("info@happypaws.org")
                .operatingHours("9am - 5pm")
                .build();

        when(shelterRepository.findFirstByOrderByShelterIdAsc()).thenReturn(Optional.of(shelter));

        ShelterResponseDto result = shelterService.getShelterInfo();

        assertNotNull(result);
        assertEquals("Happy Paws", result.getName());
        assertEquals("+1234567890", result.getPhone());
    }

    @Test
    @DisplayName("Should update shelter info")
    void shouldUpdateShelterInfo() {
        Shelter existing = Shelter.builder()
                .shelterId(1L)
                .name("Old Name")
                .address("Old Address")
                .phone("+000")
                .email("old@example.com")
                .build();

        ShelterUpdateDto updateDto = ShelterUpdateDto.builder()
                .name("New Shelter Name")
                .address("New Address 456")
                .phone("+1-800-PET-CARE")
                .email("new@example.com")
                .operatingHours("8am - 6pm")
                .description("Updated description")
                .website("https://example.com")
                .build();

        when(shelterRepository.findFirstByOrderByShelterIdAsc()).thenReturn(Optional.of(existing));
        when(shelterRepository.save(any(Shelter.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShelterResponseDto result = shelterService.updateShelterInfo(updateDto);

        assertNotNull(result);
        assertEquals("New Shelter Name", result.getName());
        assertEquals("New Address 456", result.getAddress());
        assertEquals("+1-800-PET-CARE", result.getPhone());
        verify(shelterRepository, times(1)).save(any(Shelter.class));
    }
}
