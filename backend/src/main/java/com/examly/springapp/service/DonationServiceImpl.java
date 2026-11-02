package com.examly.springapp.service;

import com.examly.springapp.dto.request.DonationRequestDto;
import com.examly.springapp.dto.response.DonationResponseDto;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.Donation;
import com.examly.springapp.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;

    public DonationServiceImpl(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    @Override
    @Transactional
    public DonationResponseDto initiateDonation(DonationRequestDto requestDto) {
        String sessionId = "sess_" + UUID.randomUUID().toString().replace("-", "");
        String checkoutUrl = "https://checkout.external-payment-gateway.com/pay/" + sessionId;

        Donation donation = Donation.builder()
                .donorName(requestDto.getDonorName().trim())
                .donorEmail(requestDto.getDonorEmail().trim().toLowerCase())
                .amount(requestDto.getAmount())
                .currency(requestDto.getCurrency() != null ? requestDto.getCurrency().toUpperCase() : "USD")
                .status("PENDING")
                .paymentSessionId(sessionId)
                .paymentProvider("EXTERNAL_GATEWAY")
                .message(requestDto.getMessage())
                .build();

        Donation saved = donationRepository.save(donation);

        return DonationResponseDto.builder()
                .donationId(saved.getDonationId())
                .donorName(saved.getDonorName())
                .donorEmail(saved.getDonorEmail())
                .amount(saved.getAmount())
                .currency(saved.getCurrency())
                .status(saved.getStatus())
                .paymentSessionId(saved.getPaymentSessionId())
                .checkoutUrl(checkoutUrl)
                .message(saved.getMessage())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DonationResponseDto getDonationById(Long donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with ID: " + donationId));

        String checkoutUrl = "https://checkout.external-payment-gateway.com/pay/" + donation.getPaymentSessionId();

        return DonationResponseDto.builder()
                .donationId(donation.getDonationId())
                .donorName(donation.getDonorName())
                .donorEmail(donation.getDonorEmail())
                .amount(donation.getAmount())
                .currency(donation.getCurrency())
                .status(donation.getStatus())
                .paymentSessionId(donation.getPaymentSessionId())
                .checkoutUrl(checkoutUrl)
                .message(donation.getMessage())
                .createdAt(donation.getCreatedAt())
                .build();
    }
}
