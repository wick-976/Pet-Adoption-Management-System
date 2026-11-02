package com.examly.springapp.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationRequestDto {

    @NotBlank(message = "Donor name is required")
    private String donorName;

    @NotBlank(message = "Donor email is required")
    @Email(message = "Please provide a valid email address")
    private String donorEmail;

    @NotNull(message = "Donation amount is required")
    @DecimalMin(value = "1.00", message = "Donation amount must be at least 1.00")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "USD";

    private String message;
}
