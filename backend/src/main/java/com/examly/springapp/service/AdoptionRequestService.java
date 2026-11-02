package com.examly.springapp.service;

import com.examly.springapp.dto.request.AdoptionRequestCreateDto;
import com.examly.springapp.dto.request.AdoptionStatusUpdateDto;
import com.examly.springapp.dto.response.AdoptionRequestResponseDto;
import com.examly.springapp.model.AdoptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdoptionRequestService {

    AdoptionRequestResponseDto submitAdoptionRequest(AdoptionRequestCreateDto requestDto, String currentUsername);

    Page<AdoptionRequestResponseDto> getAdoptionRequests(AdoptionStatus status, Long petId, Pageable pageable);

    AdoptionRequestResponseDto getAdoptionRequestById(Long requestId);

    AdoptionRequestResponseDto updateAdoptionRequestStatus(Long requestId, AdoptionStatusUpdateDto updateDto);

    List<AdoptionRequestResponseDto> getAdoptionRequestsByUser(String username);
}
