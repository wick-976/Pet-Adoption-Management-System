package com.examly.springapp.repository;

import com.examly.springapp.model.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {

    List<PetImage> findByPet_PetId(Long petId);

    Optional<PetImage> findByPet_PetIdAndIsPrimaryTrue(Long petId);

    @Modifying
    @Query("UPDATE PetImage pi SET pi.isPrimary = false WHERE pi.pet.petId = :petId")
    void resetPrimaryForPet(@Param("petId") Long petId);
}
