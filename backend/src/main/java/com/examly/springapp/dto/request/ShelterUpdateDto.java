package com.examly.springapp.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterUpdateDto {

    @NotBlank(message = "Shelter name is required")
    private String name;

    @NotBlank(message = "Shelter address is required")
    private String address;

    @NotBlank(message = "Shelter phone number is required")
    private String phone;

    @NotBlank(message = "Shelter contact email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    private String operatingHours;

    private String description;

    private String website;
}
