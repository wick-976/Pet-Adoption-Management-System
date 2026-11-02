package com.examly.springapp.service;

import com.examly.springapp.dto.request.PetImageRequestDto;
import com.examly.springapp.dto.response.PetImageResponseDto;

import java.util.List;

public interface PetImageService {

    PetImageResponseDto addImage(Long petId, PetImageRequestDto requestDto);

    List<PetImageResponseDto> getImagesByPetId(Long petId);

    PetImageResponseDto setPrimaryImage(Long petId, Long imageId);

    void deleteImage(Long imageId);
}
