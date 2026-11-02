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
import com.examly.springapp.repository.AdoptionRequestRepository;
import com.examly.springapp.repository.PetRepository;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionRequestServiceTest {

    @Mock
    private AdoptionRequestRepository adoptionRequestRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdoptionRequestServiceImpl adoptionRequestService;

    private Pet availablePet;
    private Pet pendingPet;
    private AdoptionRequest sampleRequest;

    @BeforeEach
    void setUp() {
        availablePet = Pet.builder()
                .petId(1L)
                .name("Bella")
                .species("Dog")
                .breed("Labrador")
                .age(3)
                .adoptionStatus(PetStatus.AVAILABLE)
                .build();

        pendingPet = Pet.builder()
                .petId(2L)
                .name("Charlie")
                .species("Cat")
                .breed("Persian")
                .age(2)
                .adoptionStatus(PetStatus.PENDING)
                .build();

        sampleRequest = AdoptionRequest.builder()
                .requestId(10L)
                .pet(availablePet)
                .applicantName("Jane Doe")
                .applicantEmail("jane@example.com")
                .applicantPhone("+1234567890")
                .status(AdoptionStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully submit adoption request and transition pet to PENDING")
    void shouldSubmitAdoptionRequestSuccessfully() {
        AdoptionRequestCreateDto createDto = AdoptionRequestCreateDto.builder()
                .petId(1L)
                .applicantName("Jane Doe")
                .applicantEmail("jane@example.com")
                .applicantPhone("+1234567890")
                .housingType("House")
                .hasOtherPets(false)
                .reasonForAdoption("Loving home")
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(availablePet));
        when(adoptionRequestRepository.existsByPet_PetIdAndApplicantEmailAndStatusIn(eq(1L), anyString(), anyList()))
                .thenReturn(false);
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenAnswer(invocation -> {
            AdoptionRequest req = invocation.getArgument(0);
            req.setRequestId(10L);
            return req;
        });

        AdoptionRequestResponseDto response = adoptionRequestService.submitAdoptionRequest(createDto, null);

        assertNotNull(response);
        assertEquals(10L, response.getRequestId());
        assertEquals(AdoptionStatus.PENDING, response.getStatus());
        assertEquals(PetStatus.PENDING, availablePet.getAdoptionStatus());

        verify(petRepository, times(1)).save(availablePet);
        verify(adoptionRequestRepository, times(1)).save(any(AdoptionRequest.class));
        verify(emailService, times(1)).sendAdoptionConfirmation(any(AdoptionRequest.class));
    }

    @Test
    @DisplayName("Should reject submission when pet is not AVAILABLE")
    void shouldRejectSubmissionWhenPetNotAvailable() {
        AdoptionRequestCreateDto createDto = AdoptionRequestCreateDto.builder()
                .petId(2L)
                .applicantName("Jane Doe")
                .applicantEmail("jane@example.com")
                .applicantPhone("+1234567890")
                .build();

        when(petRepository.findById(2L)).thenReturn(Optional.of(pendingPet));

        InvalidStatusException ex = assertThrows(
                InvalidStatusException.class,
                () -> adoptionRequestService.submitAdoptionRequest(createDto, null)
        );

        assertTrue(ex.getMessage().contains("not available for adoption"));
        verify(adoptionRequestRepository, never()).save(any());
        verify(emailService, never()).sendAdoptionConfirmation(any());
    }

    @Test
    @DisplayName("Should reject duplicate active adoption request for same pet and applicant")
    void shouldRejectDuplicateAdoptionRequest() {
        AdoptionRequestCreateDto createDto = AdoptionRequestCreateDto.builder()
                .petId(1L)
                .applicantName("Jane Doe")
                .applicantEmail("jane@example.com")
                .applicantPhone("+1234567890")
                .build();

        when(petRepository.findById(1L)).thenReturn(Optional.of(availablePet));
        when(adoptionRequestRepository.existsByPet_PetIdAndApplicantEmailAndStatusIn(eq(1L), eq("jane@example.com"), anyList()))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> adoptionRequestService.submitAdoptionRequest(createDto, null)
        );

        verify(adoptionRequestRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should transition pet to ADOPTED when adoption request is APPROVED")
    void shouldTransitionPetToAdoptedOnApproval() {
        AdoptionStatusUpdateDto updateDto = AdoptionStatusUpdateDto.builder()
                .status(AdoptionStatus.APPROVED)
                .staffNotes("Applicant passed home check successfully.")
                .build();

        availablePet.setAdoptionStatus(PetStatus.PENDING);
        when(adoptionRequestRepository.findById(10L)).thenReturn(Optional.of(sampleRequest));
        when(adoptionRequestRepository.findByPet_PetId(1L)).thenReturn(List.of(sampleRequest));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(sampleRequest);

        AdoptionRequestResponseDto result = adoptionRequestService.updateAdoptionRequestStatus(10L, updateDto);

        assertNotNull(result);
        assertEquals(AdoptionStatus.APPROVED, sampleRequest.getStatus());
        assertEquals(PetStatus.ADOPTED, availablePet.getAdoptionStatus());
        verify(petRepository, times(1)).save(availablePet);
        verify(emailService, times(1)).sendStatusUpdateNotification(sampleRequest);
    }

    @Test
    @DisplayName("Should return pet to AVAILABLE when request REJECTED and no other pending requests exist")
    void shouldReturnPetToAvailableWhenRejectedAndNoOtherPending() {
        AdoptionStatusUpdateDto updateDto = AdoptionStatusUpdateDto.builder()
                .status(AdoptionStatus.REJECTED)
                .staffNotes("Applicant does not meet landlord requirements.")
                .build();

        availablePet.setAdoptionStatus(PetStatus.PENDING);
        when(adoptionRequestRepository.findById(10L)).thenReturn(Optional.of(sampleRequest));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(sampleRequest);
        when(adoptionRequestRepository.countByPet_PetIdAndStatus(1L, AdoptionStatus.PENDING)).thenReturn(0L);
        when(adoptionRequestRepository.countByPet_PetIdAndStatus(1L, AdoptionStatus.RECEIVED)).thenReturn(0L);

        AdoptionRequestResponseDto result = adoptionRequestService.updateAdoptionRequestStatus(10L, updateDto);

        assertNotNull(result);
        assertEquals(AdoptionStatus.REJECTED, sampleRequest.getStatus());
        assertEquals(PetStatus.AVAILABLE, availablePet.getAdoptionStatus());
        verify(petRepository, times(1)).save(availablePet);
        verify(emailService, times(1)).sendStatusUpdateNotification(sampleRequest);
    }

    @Test
    @DisplayName("Should keep pet PENDING when request REJECTED but other pending requests exist")
    void shouldKeepPetPendingWhenRejectedAndOtherPendingExist() {
        AdoptionStatusUpdateDto updateDto = AdoptionStatusUpdateDto.builder()
                .status(AdoptionStatus.REJECTED)
                .staffNotes("First applicant declined.")
                .build();

        availablePet.setAdoptionStatus(PetStatus.PENDING);
        when(adoptionRequestRepository.findById(10L)).thenReturn(Optional.of(sampleRequest));
        when(adoptionRequestRepository.save(any(AdoptionRequest.class))).thenReturn(sampleRequest);
        when(adoptionRequestRepository.countByPet_PetIdAndStatus(1L, AdoptionStatus.PENDING)).thenReturn(1L);
        when(adoptionRequestRepository.countByPet_PetIdAndStatus(1L, AdoptionStatus.RECEIVED)).thenReturn(0L);

        AdoptionRequestResponseDto result = adoptionRequestService.updateAdoptionRequestStatus(10L, updateDto);

        assertNotNull(result);
        assertEquals(AdoptionStatus.REJECTED, sampleRequest.getStatus());
        // Pet should remain PENDING because another request is pending!
        assertEquals(PetStatus.PENDING, availablePet.getAdoptionStatus());
        verify(petRepository, never()).save(availablePet);
    }
}
