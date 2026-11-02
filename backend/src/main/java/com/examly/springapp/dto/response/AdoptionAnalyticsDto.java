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
public class AdoptionAnalyticsDto {
    private Long totalRequests;
    private Long approvedRequests;
    private Long rejectedRequests;
    private Long pendingRequests;
    private Double approvalRatePercentage;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}
