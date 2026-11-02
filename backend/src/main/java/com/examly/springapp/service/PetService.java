package com.examly.springapp.service;

import com.examly.springapp.dto.request.PetCreateRequestDto;
import com.examly.springapp.dto.request.PetUpdateRequestDto;
import com.examly.springapp.dto.response.PetPublicResponseDto;
import com.examly.springapp.dto.response.PetStaffResponseDto;
import com.examly.springapp.model.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PetService {

    PetStaffResponseDto createPet(PetCreateRequestDto requestDto);

    Page<PetPublicResponseDto> searchPetsPublic(
            String name,
            String breed,
            String species,
            Integer age,
            PetStatus adoptionStatus,
            Pageable pageable
    );

    Page<PetStaffResponseDto> searchPetsStaff(
            String name,
            String breed,
            String species,
            Integer age,
            PetStatus adoptionStatus,
            Pageable pageable
    );

    PetPublicResponseDto getPetPublicById(Long petId);

    PetStaffResponseDto getPetStaffById(Long petId);

    PetStaffResponseDto updatePet(Long petId, PetUpdateRequestDto requestDto);

    PetStaffResponseDto updatePetStatus(Long petId, PetStatus adoptionStatus);

    void deletePet(Long petId);

    List<PetPublicResponseDto> getMostViewedPets(int limit);
}
