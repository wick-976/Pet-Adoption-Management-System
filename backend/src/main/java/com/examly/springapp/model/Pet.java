package com.examly.springapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(length = 100)
    private String breed;

    @Column(nullable = false)
    private Integer age;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PetStatus adoptionStatus = PetStatus.AVAILABLE;

    @Column(nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    // Staff-only pet health & medical information
    @Column(length = 100)
    private String vaccinationStatus;

    @Column(length = 100)
    private String spayNeuterStatus;

    @Column(columnDefinition = "TEXT")
    private String specialMedicalNeeds;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PetImage> images = new ArrayList<>();

    public void addImage(PetImage image) {
        images.add(image);
        image.setPet(this);
    }

    public void removeImage(PetImage image) {
        images.remove(image);
        image.setPet(null);
    }
}
