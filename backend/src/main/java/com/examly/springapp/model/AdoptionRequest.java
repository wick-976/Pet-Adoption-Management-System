package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String applicantName;

    @Column(nullable = false, length = 100)
    private String applicantEmail;

    @Column(nullable = false, length = 20)
    private String applicantPhone;

    @Column(length = 255)
    private String applicantAddress;

    @Column(length = 50)
    private String housingType;

    @Column
    private Boolean hasOtherPets;

    @Column(columnDefinition = "TEXT")
    private String reasonForAdoption;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AdoptionStatus status = AdoptionStatus.PENDING;

    @CreationTimestamp
    @Column(name = "submission_date", updatable = false)
    private LocalDateTime submissionDate;

    @Column(name = "reviewed_date")
    private LocalDateTime reviewedDate;

    @Column(columnDefinition = "TEXT")
    private String staffNotes;
}
