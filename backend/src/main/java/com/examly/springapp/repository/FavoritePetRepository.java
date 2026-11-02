package com.examly.springapp.repository;

import com.examly.springapp.model.FavoritePet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritePetRepository extends JpaRepository<FavoritePet, Long> {

    List<FavoritePet> findByUser_UserId(Long userId);

    boolean existsByUser_UserIdAndPet_PetId(Long userId, Long petId);

    void deleteByUser_UserIdAndPet_PetId(Long userId, Long petId);

    Optional<FavoritePet> findByUser_UserIdAndPet_PetId(Long userId, Long petId);
}
