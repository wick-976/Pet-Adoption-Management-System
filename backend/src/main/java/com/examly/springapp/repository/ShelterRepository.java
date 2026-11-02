package com.examly.springapp.repository;

import com.examly.springapp.model.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Long> {

    Optional<Shelter> findFirstByOrderByShelterIdAsc();
}
