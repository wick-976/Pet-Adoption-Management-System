package com.examly.springapp.dto.response;

import com.examly.springapp.model.PetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetStaffResponseDto {
    private Long petId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String description;
    private PetStatus adoptionStatus;
    private Long viewCount;

    // Staff-only medical and health details
    private String vaccinationStatus;
    private String spayNeuterStatus;
    private String specialMedicalNeeds;

    private LocalDateTime creationDate;
    private LocalDateTime lastUpdatedDate;

    private String primaryImageUrl;
    private List<PetImageResponseDto> images;
}
