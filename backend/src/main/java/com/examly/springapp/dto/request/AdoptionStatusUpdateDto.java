package com.examly.springapp.dto.request;

import com.examly.springapp.model.AdoptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionStatusUpdateDto {

    @NotNull(message = "Status is required")
    private AdoptionStatus status;

    private String staffNotes;
}
