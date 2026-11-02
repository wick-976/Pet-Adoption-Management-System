package com.examly.springapp.dto.response;

import com.examly.springapp.model.AdoptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionRequestResponseDto {
    private Long requestId;
    private Long petId;
    private String petName;
    private String petSpecies;
    private Long userId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String applicantAddress;
    private String housingType;
    private Boolean hasOtherPets;
    private String reasonForAdoption;
    private AdoptionStatus status;
    private LocalDateTime submissionDate;
    private LocalDateTime reviewedDate;
    private String staffNotes;
}
