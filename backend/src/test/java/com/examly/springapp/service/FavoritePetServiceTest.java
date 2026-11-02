package com.examly.springapp.service;

import com.examly.springapp.dto.response.FavoritePetResponseDto;
import com.examly.springapp.exception.DuplicateResourceException;
import com.examly.springapp.model.FavoritePet;
import com.examly.springapp.model.Pet;
import com.examly.springapp.model.PetStatus;
import com.examly.springapp.model.User;
import com.examly.springapp.repository.FavoritePetRepository;
import com.examly.springapp.repository.PetRepository;
import com.examly.springapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritePetServiceTest {

    @Mock
    private FavoritePetRepository favoritePetRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private FavoritePetServiceImpl favoritePetService;

    private User sampleUser;
    private Pet samplePet;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        samplePet = Pet.builder()
                .petId(2L)
                .name("Max")
                .species("Dog")
                .age(3)
                .adoptionStatus(PetStatus.AVAILABLE)
                .images(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Should add pet to favorites")
    void shouldAddFavorite() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(petRepository.findById(2L)).thenReturn(Optional.of(samplePet));
        when(favoritePetRepository.existsByUser_UserIdAndPet_PetId(1L, 2L)).thenReturn(false);
        when(favoritePetRepository.save(any(FavoritePet.class))).thenAnswer(inv -> {
            FavoritePet fp = inv.getArgument(0);
            fp.setFavoriteId(100L);
            return fp;
        });

        FavoritePetResponseDto result = favoritePetService.addFavorite("testuser", 2L);

        assertNotNull(result);
        assertEquals(100L, result.getFavoriteId());
        assertEquals("Max", result.getPet().getName());
        verify(favoritePetRepository, times(1)).save(any(FavoritePet.class));
    }

    @Test
    @DisplayName("Should prevent duplicate favorites")
    void shouldPreventDuplicateFavorites() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(petRepository.findById(2L)).thenReturn(Optional.of(samplePet));
        when(favoritePetRepository.existsByUser_UserIdAndPet_PetId(1L, 2L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> favoritePetService.addFavorite("testuser", 2L));
        verify(favoritePetRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should remove pet from favorites")
    void shouldRemoveFavorite() {
        FavoritePet favorite = FavoritePet.builder()
                .favoriteId(100L)
                .user(sampleUser)
                .pet(samplePet)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(petRepository.existsById(2L)).thenReturn(true);
        when(favoritePetRepository.findByUser_UserIdAndPet_PetId(1L, 2L)).thenReturn(Optional.of(favorite));

        assertDoesNotThrow(() -> favoritePetService.removeFavorite("testuser", 2L));
        verify(favoritePetRepository, times(1)).delete(favorite);
    }
}
