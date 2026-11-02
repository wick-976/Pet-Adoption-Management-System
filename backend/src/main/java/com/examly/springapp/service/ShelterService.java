package com.examly.springapp.service;

import com.examly.springapp.dto.request.ShelterUpdateDto;
import com.examly.springapp.dto.response.ShelterResponseDto;

public interface ShelterService {

    ShelterResponseDto getShelterInfo();

    ShelterResponseDto updateShelterInfo(ShelterUpdateDto updateDto);
}
