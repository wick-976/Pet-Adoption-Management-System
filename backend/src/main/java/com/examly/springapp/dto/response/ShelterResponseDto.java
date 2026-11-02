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
public class ShelterResponseDto {
    private Long shelterId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String operatingHours;
    private String description;
    private String website;
    private LocalDateTime lastUpdatedDate;
}
