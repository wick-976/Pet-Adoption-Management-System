package com.examly.springapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetImageRequestDto {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Builder.Default
    private Boolean isPrimary = false;
}
