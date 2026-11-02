package com.examly.springapp.service;

import com.examly.springapp.dto.request.ShelterUpdateDto;
import com.examly.springapp.dto.response.ShelterResponseDto;
import com.examly.springapp.model.Shelter;
import com.examly.springapp.repository.ShelterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShelterServiceImpl implements ShelterService {

    private final ShelterRepository shelterRepository;

    public ShelterServiceImpl(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    @Override
    @Transactional
    public ShelterResponseDto getShelterInfo() {
        Shelter shelter = shelterRepository.findFirstByOrderByShelterIdAsc()
                .orElseGet(() -> shelterRepository.save(Shelter.builder()
                        .name("Happy Paws Animal Shelter")
                        .address("123 Rescue Way, Springfield, IL 62701")
                        .phone("+1 (555) 123-4567")
                        .email("contact@happypaws.org")
                        .operatingHours("Mon - Sat: 9:00 AM - 6:00 PM, Sun: 10:00 AM - 4:00 PM")
                        .description("Dedicated to rescuing, rehabilitating, and finding forever homes for abandoned and homeless pets.")
                        .website("https://www.happypawsshelter.org")
                        .build()));

        return mapToDto(shelter);
    }

    @Override
    @Transactional
    public ShelterResponseDto updateShelterInfo(ShelterUpdateDto updateDto) {
        Shelter shelter = shelterRepository.findFirstByOrderByShelterIdAsc()
                .orElseGet(() -> Shelter.builder().build());

        shelter.setName(updateDto.getName().trim());
        shelter.setAddress(updateDto.getAddress().trim());
        shelter.setPhone(updateDto.getPhone().trim());
        shelter.setEmail(updateDto.getEmail().trim().toLowerCase());
        shelter.setOperatingHours(updateDto.getOperatingHours());
        shelter.setDescription(updateDto.getDescription());
        shelter.setWebsite(updateDto.getWebsite());

        Shelter saved = shelterRepository.save(shelter);
        return mapToDto(saved);
    }

    private ShelterResponseDto mapToDto(Shelter shelter) {
        return ShelterResponseDto.builder()
                .shelterId(shelter.getShelterId())
                .name(shelter.getName())
                .address(shelter.getAddress())
                .phone(shelter.getPhone())
                .email(shelter.getEmail())
                .operatingHours(shelter.getOperatingHours())
                .description(shelter.getDescription())
                .website(shelter.getWebsite())
                .lastUpdatedDate(shelter.getLastUpdatedDate())
                .build();
    }
}
