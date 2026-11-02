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
public class StaffResponseDto {
    private Long staffId;
    private String username;
    private String email;
    private String role;
    private LocalDateTime lastLoginDate;
    private LocalDateTime createdAt;
}
