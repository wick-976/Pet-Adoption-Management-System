package com.examly.springapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationResponseDto {
    private Long donationId;
    private String donorName;
    private String donorEmail;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentSessionId;
    private String checkoutUrl;
    private String message;
    private LocalDateTime createdAt;
}
