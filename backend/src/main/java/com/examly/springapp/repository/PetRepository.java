package com.examly.springapp.repository;

import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    @Query("SELECT p FROM Pet p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:breed IS NULL OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :breed, '%'))) AND " +
           "(:species IS NULL OR LOWER(p.species) = LOWER(:species)) AND " +
           "(:age IS NULL OR p.age = :age) AND " +
           "(:adoptionStatus IS NULL OR p.adoptionStatus = :adoptionStatus)")
    Page<Pet> searchPets(
            @Param("name") String name,
            @Param("breed") String breed,
            @Param("species") String species,
            @Param("age") Integer age,
            @Param("adoptionStatus") PetStatus adoptionStatus,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Pet p SET p.viewCount = p.viewCount + 1 WHERE p.petId = :petId")
    void incrementViewCount(@Param("petId") Long petId);

    long countByAdoptionStatus(PetStatus adoptionStatus);

    List<Pet> findTop10ByOrderByViewCountDesc();

    @Query("SELECT p.species AS species, COUNT(p) AS count FROM Pet p GROUP BY p.species")
    List<Object[]> countPetsBySpecies();
}
