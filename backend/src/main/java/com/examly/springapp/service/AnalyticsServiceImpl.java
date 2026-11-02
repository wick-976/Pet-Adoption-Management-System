package com.examly.springapp.service;

import com.examly.springapp.dto.response.AdoptionAnalyticsDto;
import com.examly.springapp.dto.response.AnalyticsResponseDto;
import com.examly.springapp.dto.response.PetAnalyticsDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.model.AdoptionRequest;
import com.examly.springapp.model.AdoptionStatus;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.repository.AdoptionRequestRepository;
import com.examly.springapp.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PetRepository petRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetService petService;

    public AnalyticsServiceImpl(
            PetRepository petRepository,
            AdoptionRequestRepository adoptionRequestRepository,
            PetService petService) {
        this.petRepository = petRepository;
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.petService = petService;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyticsResponseDto getOverallAnalytics() {
        long available = petRepository.countByAdoptionStatus(PetStatus.AVAILABLE);
        long totalRequests = adoptionRequestRepository.count();

        Map<String, Long> statusBreakdown = new LinkedHashMap<>();
        for (AdoptionStatus status : AdoptionStatus.values()) {
            statusBreakdown.put(status.name(), adoptionRequestRepository.countByStatus(status));
        }

        long completedAdoptions = adoptionRequestRepository.countByStatus(AdoptionStatus.APPROVED);

        Map<String, Long> speciesBreakdown = new LinkedHashMap<>();
        List<Object[]> speciesCounts = petRepository.countPetsBySpecies();
        for (Object[] row : speciesCounts) {
            String species = (String) row[0];
            Long count = (Long) row[1];
            speciesBreakdown.put(species != null ? species : "Unknown", count);
        }

        List<PetPublicResponseDto> topPets = petService.getMostViewedPets(5);

        return AnalyticsResponseDto.builder()
                .totalAvailablePets(available)
                .totalAdoptionRequests(totalRequests)
                .adoptionStatusBreakdown(statusBreakdown)
                .completedAdoptions(completedAdoptions)
                .petsBySpecies(speciesBreakdown)
                .mostViewedPets(topPets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AdoptionAnalyticsDto getAdoptionAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

        List<AdoptionRequest> requests = adoptionRequestRepository.findBySubmissionDateBetween(start, end);

        long total = requests.size();
        long approved = requests.stream().filter(r -> r.getStatus() == AdoptionStatus.APPROVED).count();
        long rejected = requests.stream().filter(r -> r.getStatus() == AdoptionStatus.REJECTED).count();
        long pending = requests.stream().filter(r -> r.getStatus() == AdoptionStatus.PENDING || r.getStatus() == AdoptionStatus.RECEIVED).count();

        double approvalRate = total > 0 ? ((double) approved / total) * 100.0 : 0.0;
        // round to 2 decimals
        approvalRate = Math.round(approvalRate * 100.0) / 100.0;

        return AdoptionAnalyticsDto.builder()
                .totalRequests(total)
                .approvedRequests(approved)
                .rejectedRequests(rejected)
                .pendingRequests(pending)
                .approvalRatePercentage(approvalRate)
                .periodStart(start)
                .periodEnd(end)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PetAnalyticsDto getPetAnalytics() {
        long total = petRepository.count();
        long available = petRepository.countByAdoptionStatus(PetStatus.AVAILABLE);
        long pending = petRepository.countByAdoptionStatus(PetStatus.PENDING);
        long adopted = petRepository.countByAdoptionStatus(PetStatus.ADOPTED);

        Map<String, Long> speciesBreakdown = new LinkedHashMap<>();
        List<Object[]> speciesCounts = petRepository.countPetsBySpecies();
        for (Object[] row : speciesCounts) {
            String species = (String) row[0];
            Long count = (Long) row[1];
            speciesBreakdown.put(species != null ? species : "Unknown", count);
        }

        List<PetPublicResponseDto> topPets = petService.getMostViewedPets(10);

        return PetAnalyticsDto.builder()
                .totalPets(total)
                .availablePets(available)
                .pendingPets(pending)
                .adoptedPets(adopted)
                .speciesBreakdown(speciesBreakdown)
                .mostViewedPets(topPets)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getExportReadyData() {
        Map<String, Object> export = new HashMap<>();
        export.put("generatedAt", LocalDateTime.now());
        export.put("overallMetrics", getOverallAnalytics());
        export.put("petMetrics", getPetAnalytics());
        export.put("adoption30DayMetrics", getAdoptionAnalytics(null, null));
        return export;
    }
}
