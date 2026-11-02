package com.examly.springapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionRequestCreateDto {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotBlank(message = "Applicant email is required")
    @Email(message = "Please provide a valid email address")
    private String applicantEmail;

    @NotBlank(message = "Applicant phone number is required")
    @Pattern(regexp = "^[+0-9\\-\\s()]{7,20}$", message = "Please provide a valid phone number")
    private String applicantPhone;

    private String applicantAddress;

    private String housingType;

    private Boolean hasOtherPets;

    private String reasonForAdoption;
}
