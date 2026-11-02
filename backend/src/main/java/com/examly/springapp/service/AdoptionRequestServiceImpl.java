package com.examly.springapp.service;

import com.examly.springapp.dto.request.AdoptionRequestCreateDto;
import com.examly.springapp.dto.request.AdoptionStatusUpdateDto;
import com.examly.springapp.dto.response.AdoptionRequestResponseDto;
import com.examly.springapp.exception.DuplicateResourceException;
import com.examly.springapp.exception.InvalidStatusException;
import com.examly.springapp.exception.ResourceNotFoundException;
import com.examly.springapp.model.AdoptionRequest;
import com.examly.springapp.model.AdoptionStatus;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.AdoptionRequestRepository;
import com.examly.springapp.repository.PetRepository;
import com.examly.springapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdoptionRequestServiceImpl implements AdoptionRequestService {

    private final AdoptionRequestRepository adoptionRequestRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AdoptionRequestServiceImpl(
            AdoptionRequestRepository adoptionRequestRepository,
            PetRepository petRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.adoptionRequestRepository = adoptionRequestRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public AdoptionRequestResponseDto submitAdoptionRequest(AdoptionRequestCreateDto requestDto, String currentUsername) {
        // 1 & 2: Find pet and verify exists
        Pet pet = petRepository.findById(requestDto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + requestDto.getPetId()));

        // 3: Verify adoption status is AVAILABLE
        if (pet.getAdoptionStatus() != PetStatus.AVAILABLE) {
            throw new InvalidStatusException(
                    "Pet '" + pet.getName() + "' is not available for adoption. Current status: " + pet.getAdoptionStatus()
            );
        }

        // Prevent duplicate active requests for same applicant & pet
        boolean existsDuplicate = adoptionRequestRepository.existsByPet_PetIdAndApplicantEmailAndStatusIn(
                pet.getPetId(),
                requestDto.getApplicantEmail().trim().toLowerCase(),
                List.of(AdoptionStatus.PENDING, AdoptionStatus.RECEIVED)
        );
        if (existsDuplicate) {
            throw new DuplicateResourceException(
                    "An active adoption request already exists for this pet from email: " + requestDto.getApplicantEmail()
            );
        }

        // Optional registered user link
        User user = null;
        if (currentUsername != null && !currentUsername.isBlank()) {
            user = userRepository.findByUsername(currentUsername).orElse(null);
        }

        // 4 & 5: Create adoption request
        AdoptionRequest adoptionRequest = AdoptionRequest.builder()
                .pet(pet)
                .user(user)
                .applicantName(requestDto.getApplicantName().trim())
                .applicantEmail(requestDto.getApplicantEmail().trim().toLowerCase())
                .applicantPhone(requestDto.getApplicantPhone().trim())
                .applicantAddress(requestDto.getApplicantAddress())
                .housingType(requestDto.getHousingType())
                .hasOtherPets(requestDto.getHasOtherPets())
                .reasonForAdoption(requestDto.getReasonForAdoption())
                .status(AdoptionStatus.PENDING)
                .build();

        // 7: Automatically change the pet status to PENDING
        pet.setAdoptionStatus(PetStatus.PENDING);
        petRepository.save(pet);

        // 8: Persist changes transactionally
        AdoptionRequest savedRequest = adoptionRequestRepository.save(adoptionRequest);

        // 9: Trigger confirmation email
        try {
            emailService.sendAdoptionConfirmation(savedRequest);
        } catch (Exception e) {
            // Email failure should not roll back adoption request submission
        }

        return mapToDto(savedRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdoptionRequestResponseDto> getAdoptionRequests(AdoptionStatus status, Long petId, Pageable pageable) {
        Page<AdoptionRequest> requests = adoptionRequestRepository.findByFilters(status, petId, pageable);
        return requests.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public AdoptionRequestResponseDto getAdoptionRequestById(Long requestId) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found with ID: " + requestId));
        return mapToDto(request);
    }

    @Override
    @Transactional
    public AdoptionRequestResponseDto updateAdoptionRequestStatus(Long requestId, AdoptionStatusUpdateDto updateDto) {
        AdoptionRequest request = adoptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Adoption request not found with ID: " + requestId));

        AdoptionStatus newStatus = updateDto.getStatus();
        request.setStatus(newStatus);
        request.setReviewedDate(LocalDateTime.now());
        if (updateDto.getStaffNotes() != null) {
            request.setStaffNotes(updateDto.getStaffNotes());
        }

        Pet pet = request.getPet();

        // Business rules for status update
        if (newStatus == AdoptionStatus.APPROVED) {
            // When an adoption request is APPROVED: change associated pet status to ADOPTED
            pet.setAdoptionStatus(PetStatus.ADOPTED);
            petRepository.save(pet);

            // Automatically reject other pending requests for this same pet
            List<AdoptionRequest> otherRequests = adoptionRequestRepository.findByPet_PetId(pet.getPetId());
            for (AdoptionRequest other : otherRequests) {
                if (!other.getRequestId().equals(requestId) &&
                    (other.getStatus() == AdoptionStatus.PENDING || other.getStatus() == AdoptionStatus.RECEIVED)) {
                    other.setStatus(AdoptionStatus.REJECTED);
                    other.setReviewedDate(LocalDateTime.now());
                    other.setStaffNotes("Pet was adopted by another applicant.");
                    adoptionRequestRepository.save(other);
                    try {
                        emailService.sendStatusUpdateNotification(other);
                    } catch (Exception ignored) {}
                }
            }
        } else if (newStatus == AdoptionStatus.REJECTED) {
            // When an adoption request is REJECTED:
            // If there are no other pending/active adoption requests for that pet, return pet status to AVAILABLE
            AdoptionRequest savedFirst = adoptionRequestRepository.save(request);

            long remainingPending = adoptionRequestRepository.countByPet_PetIdAndStatus(pet.getPetId(), AdoptionStatus.PENDING)
                    + adoptionRequestRepository.countByPet_PetIdAndStatus(pet.getPetId(), AdoptionStatus.RECEIVED);

            if (remainingPending == 0 && pet.getAdoptionStatus() != PetStatus.ADOPTED) {
                pet.setAdoptionStatus(PetStatus.AVAILABLE);
                petRepository.save(pet);
            }

            try {
                emailService.sendStatusUpdateNotification(savedFirst);
            } catch (Exception ignored) {}

            return mapToDto(savedFirst);
        }

        AdoptionRequest saved = adoptionRequestRepository.save(request);

        try {
            emailService.sendStatusUpdateNotification(saved);
        } catch (Exception ignored) {}

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdoptionRequestResponseDto> getAdoptionRequestsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return adoptionRequestRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private AdoptionRequestResponseDto mapToDto(AdoptionRequest req) {
        return AdoptionRequestResponseDto.builder()
                .requestId(req.getRequestId())
                .petId(req.getPet() != null ? req.getPet().getPetId() : null)
                .petName(req.getPet() != null ? req.getPet().getName() : null)
                .petSpecies(req.getPet() != null ? req.getPet().getSpecies() : null)
                .userId(req.getUser() != null ? req.getUser().getUserId() : null)
                .applicantName(req.getApplicantName())
                .applicantEmail(req.getApplicantEmail())
                .applicantPhone(req.getApplicantPhone())
                .applicantAddress(req.getApplicantAddress())
                .housingType(req.getHousingType())
                .hasOtherPets(req.getHasOtherPets())
                .reasonForAdoption(req.getReasonForAdoption())
                .status(req.getStatus())
                .submissionDate(req.getSubmissionDate())
                .reviewedDate(req.getReviewedDate())
                .staffNotes(req.getStaffNotes())
                .build();
    }
}
