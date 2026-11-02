package com.examly.springapp.service;

import com.examly.springapp.dto.request.DonationRequestDto;
import com.examly.springapp.dto.response.DonationResponseDto;

public interface DonationService {

    DonationResponseDto initiateDonation(DonationRequestDto requestDto);

    DonationResponseDto getDonationById(Long donationId);
}
