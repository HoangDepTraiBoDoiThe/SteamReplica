package com.example.steamreplica;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.request.GameImageRequest;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.repository.UserRepository;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.util.StaticHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameTests {
    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DiscountRepository discountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserAssembler userAssembler;
    @Mock
    private DiscountAssembler discountAssembler;
    @Mock
    private CategoryAssembler categoryAssembler;
    @Mock
    private GameImageAssembler gameImageAssembler;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private GameAssembler gameAssembler;

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(gameRepository, userRepository, discountRepository, categoryRepository, gameAssembler);
    }
    // Successfully adds a new game when all required fields are provided
    @Test
    public void test_add_game_success() {
        // Arrange
        GameRequest gameRequest = new GameRequest();
        gameRequest.setName("New Game");
        gameRequest.setDeveloperIds(Set.of(1L));
        gameRequest.setPublisherIds(Set.of(2L));
        gameRequest.setDiscountIds(Set.of(3L));
        gameRequest.setCategoryIds(Set.of(4L));
        gameRequest.setGameImagesRequest(List.of(new GameImageRequest("image1", "imageData")));
        gameRequest.setReleaseDate(LocalDate.now());
        gameRequest.setPrice(BigDecimal.valueOf(59.99));

        when(gameRepository.findGameByGameName("New Game")).thenReturn(Optional.empty());
        when(userRepository.findAllById(Set.of(1L))).thenReturn(List.of(new User()));
        when(userRepository.findAllById(Set.of(2L))).thenReturn(List.of(new User()));
        when(discountRepository.findAllById(Set.of(3L))).thenReturn(List.of(new Discount()));
        when(categoryRepository.findAllById(Set.of(4L))).thenReturn(List.of(new Category()));

        Game newGame = gameRequest.toModel();
        newGame.setId(123L);
        when(gameRepository.save(any(Game.class))).thenReturn(newGame);

        // Act
        EntityModel<GameResponse> response = gameService.addGame(gameRequest, authentication);

        // Assert
        assertNotNull(response);

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
        verify(gameRepository).save(gameCaptor.capture());
        Game savedGame = gameCaptor.getValue();

        assertEquals("New Game", savedGame.getGameName());
        assertEquals(gameRequest.getPrice(), savedGame.getGameBasePrice());
        assertEquals(gameRequest.getReleaseDate(), savedGame.getReleaseDate());
        assertEquals(1, savedGame.getDevelopers().size());
        assertEquals(1, savedGame.getPublishers().size());
        assertEquals(1, savedGame.getDiscounts().size());
        assertEquals(1, savedGame.getCategories().size());
        assertEquals(1, savedGame.getGameImages().size());

        verify(gameRepository).findGameByGameName("New Game");
        verify(userRepository).findAllById(gameRequest.getDeveloperIds());
        verify(userRepository).findAllById(gameRequest.getPublisherIds());
        verify(discountRepository).findAllById(gameRequest.getDiscountIds());
        verify(categoryRepository).findAllById(gameRequest.getCategoryIds());
    
    }

    // Throws GameException when a game with the same name already exists
    @Test
    public void test_add_game_already_exists() {
        GameRepository gameRepository = mock(GameRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        DiscountRepository discountRepository = mock(DiscountRepository.class);
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        GameAssembler gameAssembler = mock(GameAssembler.class);
        Authentication authentication = mock(Authentication.class);

        GameService gameService = new GameService(gameRepository, userRepository, discountRepository, categoryRepository, gameAssembler);

        GameRequest gameRequest = new GameRequest();
        gameRequest.setName("Existing Game");

        when(gameRepository.findGameByGameName("Existing Game")).thenReturn(Optional.of(new Game()));

        assertThrows(GameException.class, () -> {
            gameService.addGame(gameRequest, authentication);
        });
    }

    @Test
    public void test_update_game_success() {
        // Arrange
        GameRepository gameRepository = Mockito.mock(GameRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        DiscountRepository discountRepository = Mockito.mock(DiscountRepository.class);
        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
        GameAssembler gameAssembler = Mockito.mock(GameAssembler.class);
        Authentication authentication = Mockito.mock(Authentication.class);

        GameService gameService = new GameService(gameRepository, userRepository, discountRepository, categoryRepository, gameAssembler);

        long gameId = 1L;
        GameRequest gameRequest = new GameRequest("New Game", "Description", Set.of(1L), Set.of(2L), Set.of(3L), Set.of(4L), List.of(), "thumbnail", LocalDate.now(), BigDecimal.valueOf(59.99));
        Game existingGame = new Game(gameId, "Old Game", BigDecimal.valueOf(49.99), "Old Description", LocalDate.now(), new HashSet<>(), new HashSet<>(), null);

        Mockito.when(gameRepository.findById(gameId)).thenReturn(Optional.of(existingGame));
        Mockito.when(userRepository.findAllById(gameRequest.getDeveloperIds())).thenReturn(List.of());
        Mockito.when(userRepository.findAllById(gameRequest.getPublisherIds())).thenReturn(List.of());
        Mockito.when(discountRepository.findAllById(gameRequest.getDiscountIds())).thenReturn(List.of());
        Mockito.when(categoryRepository.findAllById(gameRequest.getCategoryIds())).thenReturn(List.of());
        Mockito.when(gameRepository.save(existingGame)).thenReturn(existingGame);
        EntityModel<GameResponse> expectedResponse = EntityModel.of(new GameResponse());
        Mockito.when(gameAssembler.toModel(existingGame, authentication)).thenReturn(expectedResponse);

        // Act
        EntityModel<GameResponse> response = gameService.updateGame(gameId, gameRequest, authentication);

        // Assert
        Assertions.assertEquals(expectedResponse, response);
    }
}