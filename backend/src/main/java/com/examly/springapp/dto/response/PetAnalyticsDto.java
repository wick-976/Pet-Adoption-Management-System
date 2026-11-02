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
public class PetAnalyticsDto {
    private Long totalPets;
    private Long availablePets;
    private Long pendingPets;
    private Long adoptedPets;
    private Map<String, Long> speciesBreakdown;
    private List<PetPublicResponseDto> mostViewedPets;
}
