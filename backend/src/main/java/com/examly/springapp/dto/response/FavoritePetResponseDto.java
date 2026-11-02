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
public class FavoritePetResponseDto {
    private Long favoriteId;
    private Long userId;
    private PetPublicResponseDto pet;
    private LocalDateTime favoritedAt;
}
