package com.examly.springapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponseDto {
    private Long totalAvailablePets;
    private Long totalAdoptionRequests;
    private Map<String, Long> adoptionStatusBreakdown;
    private Long completedAdoptions;
    private Map<String, Long> petsBySpecies;
    private List<PetPublicResponseDto> mostViewedPets;
}
