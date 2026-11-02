package com.examly.springapp.service;

import com.examly.springapp.dto.response.AnalyticsResponseDto;
import com.examly.springapp.model.AdoptionStatus;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.repository.AdoptionRequestRepository;
import com.examly.springapp.repository.PetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private PetService petService;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    @DisplayName("Should compile overall analytics correctly")
    void shouldCompileOverallAnalytics() {
        when(petRepository.countByAdoptionStatus(PetStatus.AVAILABLE)).thenReturn(15L);
        when(adoptionRequestRepository.count()).thenReturn(30L);
        when(adoptionRequestRepository.countByStatus(AdoptionStatus.PENDING)).thenReturn(10L);
        when(adoptionRequestRepository.countByStatus(AdoptionStatus.RECEIVED)).thenReturn(5L);
        when(adoptionRequestRepository.countByStatus(AdoptionStatus.APPROVED)).thenReturn(8L);
        when(adoptionRequestRepository.countByStatus(AdoptionStatus.REJECTED)).thenReturn(7L);
        when(petRepository.countPetsBySpecies()).thenReturn(List.of(new Object[]{"Dog", 12L}, new Object[]{"Cat", 8L}));
        when(petService.getMostViewedPets(5)).thenReturn(Collections.emptyList());

        AnalyticsResponseDto result = analyticsService.getOverallAnalytics();

        assertNotNull(result);
        assertEquals(15L, result.getTotalAvailablePets());
        assertEquals(30L, result.getTotalAdoptionRequests());
        assertEquals(8L, result.getCompletedAdoptions());
        assertEquals(10L, result.getAdoptionStatusBreakdown().get("PENDING"));
        assertEquals(8L, result.getAdoptionStatusBreakdown().get("APPROVED"));
        assertEquals(12L, result.getPetsBySpecies().get("Dog"));
    }
}
