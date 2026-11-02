package com.examly.springapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetImageResponseDto {
    private Long imageId;
    private Long petId;
    private String imageUrl;
    private Boolean isPrimary;
    private LocalDateTime uploadedAt;
}
