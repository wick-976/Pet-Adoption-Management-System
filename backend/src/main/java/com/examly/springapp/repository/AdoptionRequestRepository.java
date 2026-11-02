package com.examly.springapp.repository;

import com.examly.springapp.model.AdoptionRequest;
import com.examly.springapp.model.AdoptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {

    List<AdoptionRequest> findByPet_PetId(Long petId);

    Page<AdoptionRequest> findByStatus(AdoptionStatus status, Pageable pageable);

    List<AdoptionRequest> findByUser_UserId(Long userId);

    long countByStatus(AdoptionStatus status);

    long countByPet_PetIdAndStatus(Long petId, AdoptionStatus status);

    boolean existsByPet_PetIdAndApplicantEmailAndStatusIn(Long petId, String applicantEmail, List<AdoptionStatus> statuses);

    @Query("SELECT r FROM AdoptionRequest r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:petId IS NULL OR r.pet.petId = :petId)")
    Page<AdoptionRequest> findByFilters(
            @Param("status") AdoptionStatus status,
            @Param("petId") Long petId,
            Pageable pageable
    );

    List<AdoptionRequest> findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end);
}
