package com.examly.springapp.service;

import com.examly.springapp.dto.response.FavoritePetResponseDto;

import java.util.List;

public interface FavoritePetService {

    FavoritePetResponseDto addFavorite(String username, Long petId);

    void removeFavorite(String username, Long petId);

    List<FavoritePetResponseDto> getUserFavorites(String username);

    boolean isPetFavorited(String username, Long petId);
}
