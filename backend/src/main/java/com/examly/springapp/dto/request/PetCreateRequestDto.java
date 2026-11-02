package com.examly.springapp.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateRequestDto {

    @NotBlank(message = "Pet name is required")
    @Size(max = 100, message = "Pet name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 50, message = "Age must be realistic")
    private Integer age;

    private String description;

    // Staff-only medical fields
    private String vaccinationStatus;
    private String spayNeuterStatus;
    private String specialMedicalNeeds;

    private String initialImageUrl;
}
