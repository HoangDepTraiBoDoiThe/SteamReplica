package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.repository.UserRepository;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final DiscountService discountService;
    private final CategoryService categoryService;
    private final ServiceHelper serviceHelper;
    
    public List<EntityModel<GameResponse_Basic>> getAllGames(Authentication authentication) {
        return gameRepository.findAll().stream().map(game -> serviceHelper.makeGameResponse(GameResponse_Basic.class, game, authentication)).toList();
    }

    public EntityModel<GameResponse_Full> getGameById(long id, Authentication authentication) {
        Game game = gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        return serviceHelper.makeGameResponse(GameResponse_Full.class, game, authentication);
    }

    public Game getGameById_entity(long id) {
        return gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }
    
    public EntityModel<GameResponse_Full> addGame(GameRequest gameRequest, Authentication authentication) {
        if (gameRepository.findGameByGameName(gameRequest.getName()).isPresent()) throw new GameException("Game already exists");
        Game newGame = gameRequest.toModel();

        newGame.setDevelopers(gameRequest.getDeveloperIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        newGame.setPublishers(gameRequest.getPublisherIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        newGame.setDiscounts(gameRequest.getDiscountIds().stream().map(aLong -> discountService.getDiscountById_entity(aLong, authentication)).collect(Collectors.toSet()));
        newGame.setCategories(gameRequest.getCategoryIds().stream().map(aLong -> categoryService.getCategoryById_entity(aLong, authentication)).collect(Collectors.toSet()));
        List<GameImage> newGameImages = gameRequest.getGameImagesRequest().stream().map(image -> new GameImage(image.getImageName(), StaticHelper.convertToBlob(image.getImage()), newGame)).toList();
        newGame.setGameImages(new HashSet<>(newGameImages));

        Game newCreatedGame = gameRepository.save(newGame);
        return serviceHelper.makeGameResponse(GameResponse_Full.class, newCreatedGame, authentication);
    }

    public EntityModel<GameResponse_Full> updateGame(long id, GameRequest gameRequest, Authentication authentication) {
        Game gameToUpdate = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        gameToUpdate.setGameName(gameRequest.getName());
        gameToUpdate.setGameDescription(gameRequest.getDescription());
        gameToUpdate.setGameBasePrice(gameRequest.getPrice());
        gameToUpdate.setReleaseDate(gameRequest.getReleaseDate());
        gameToUpdate.setDevelopers(gameRequest.getDeveloperIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        gameToUpdate.setPublishers(gameRequest.getPublisherIds().stream().map(userService::findUsersWithById_entity).collect(Collectors.toSet()));
        gameToUpdate.setDiscounts(gameRequest.getDiscountIds().stream().map(aLong -> discountService.getDiscountById_entity(aLong, authentication)).collect(Collectors.toSet())); 
        gameToUpdate.setCategories(gameRequest.getCategoryIds().stream().map(aLong -> categoryService.getCategoryById_entity(aLong, authentication)).collect(Collectors.toSet()));
    
        Game updatedGame = gameRepository.save(gameToUpdate);
        return serviceHelper.makeGameResponse(GameResponse_Full.class, updatedGame, authentication);
    }

    public void deleteGame(long id) {
        gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        gameRepository.deleteById(id);
    }
}
