package com.examly.springapp.controller;

import com.examly.springapp.dto.response.ApiResponse;
import com.examly.springapp.dto.response.FavoritePetResponseDto;
import com.examly.springapp.service.FavoritePetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorite Pets", description = "User favorite pets management")
@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
public class FavoritePetController {

    private final FavoritePetService favoritePetService;

    public FavoritePetController(FavoritePetService favoritePetService) {
        this.favoritePetService = favoritePetService;
    }

    @GetMapping
    @Operation(summary = "Get list of favorited pets for current user")
    public ResponseEntity<List<FavoritePetResponseDto>> getFavorites(Authentication authentication) {
        List<FavoritePetResponseDto> favorites = favoritePetService.getUserFavorites(authentication.getName());
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/{petId}")
    @Operation(summary = "Add a pet to current user's favorites")
    public ResponseEntity<FavoritePetResponseDto> addFavorite(
            @PathVariable Long petId,
            Authentication authentication) {
        FavoritePetResponseDto favorite = favoritePetService.addFavorite(authentication.getName(), petId);
        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    @DeleteMapping("/{petId}")
    @Operation(summary = "Remove a pet from current user's favorites")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long petId,
            Authentication authentication) {
        favoritePetService.removeFavorite(authentication.getName(), petId);
        return ResponseEntity.ok(ApiResponse.success("Pet removed from favorites", null));
    }

    @GetMapping("/check/{petId}")
    @Operation(summary = "Check if a pet is in current user's favorites")
    public ResponseEntity<ApiResponse<Boolean>> checkFavorite(
            @PathVariable Long petId,
            Authentication authentication) {
        boolean isFavorited = favoritePetService.isPetFavorited(authentication.getName(), petId);
        return ResponseEntity.ok(ApiResponse.success(isFavorited));
    }
}
