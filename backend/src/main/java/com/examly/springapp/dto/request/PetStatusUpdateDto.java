package com.examly.springapp.dto.request;

import com.examly.springapp.model.PetStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetStatusUpdateDto {

    @NotNull(message = "Adoption status is required")
    private PetStatus adoptionStatus;
}
