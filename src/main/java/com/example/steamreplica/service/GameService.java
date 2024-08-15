package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.CategoryResponse;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.dtos.response.GameImageResponse;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.dtos.response.user.UserResponse;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.repository.UserRepository;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final DiscountRepository discountRepository;
    private final CategoryRepository categoryRepository;
    private final GameAssembler gameAssembler;
    private final UserAssembler userAssembler;
    private final DiscountAssembler discountAssembler;
    private final CategoryAssembler categoryAssembler;
    private final GameImageAssembler gameImageAssembler;
    
    public CollectionModel<EntityModel<GameResponse>> getAllGames(Authentication authentication) {
        List<GameResponse> list = gameRepository.findAll().stream().map(game -> makeGameResponse(authentication, game)).toList();
        return gameAssembler.toCollectionModel(list, authentication);
    }

    public EntityModel<GameResponse> getGameById(long id, Authentication authentication) {
        Game game = gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        GameResponse gameResponse = makeGameResponse(authentication, game);
        return gameAssembler.toModel(gameResponse, authentication);
    }

    public Game getGameById_entity(long id) {
        return gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
    }
    
    public EntityModel<GameResponse> addGame(GameRequest gameRequest, Authentication authentication) {
        if (gameRepository.findGameByGameName(gameRequest.getName()).isPresent()) throw new GameException("Game already exists");
        Game newGame = gameRequest.toModel();
        newGame.setDevelopers(new HashSet<>(userRepository.findAllById(gameRequest.getDeveloperIds())));
        newGame.setPublishers(new HashSet<>(userRepository.findAllById(gameRequest.getPublisherIds())));
        newGame.setDiscounts(new HashSet<>(discountRepository.findAllById(gameRequest.getDiscountIds())));
        newGame.setCategories(new HashSet<>(categoryRepository.findAllById(gameRequest.getCategoryIds())));
        List<GameImage> newGameImages = gameRequest.getGameImagesRequest().stream().map(image -> new GameImage(image.getImageName(), StaticHelper.convertToBlob(image.getImage()), newGame)).toList();
        newGame.setGameImages(new HashSet<>(newGameImages));

        Game newCreatedGame = gameRepository.save(newGame);
        return gameAssembler.toModel(makeGameResponse(authentication, newCreatedGame), authentication);
    }

    public EntityModel<GameResponse> updateGame(long id, GameRequest gameRequest, Authentication authentication) {
        Game gameToUpdate = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));

        gameToUpdate.setGameName(gameRequest.getName());
        gameToUpdate.setGameDescription(gameRequest.getDescription());
        gameToUpdate.setGameBasePrice(gameRequest.getPrice());
        gameToUpdate.setReleaseDate(gameRequest.getReleaseDate());
        gameToUpdate.setDevelopers(new HashSet<>(userRepository.findAllById(gameRequest.getDeveloperIds())));
        gameToUpdate.setPublishers(new HashSet<>(userRepository.findAllById(gameRequest.getPublisherIds())));
        gameToUpdate.setDiscounts(new HashSet<>(discountRepository.findAllById(gameRequest.getDiscountIds()))); 
        gameToUpdate.setCategories(new HashSet<>(categoryRepository.findAllById(gameRequest.getCategoryIds())));
    
        Game updatedGame = gameRepository.save(gameToUpdate);
        return gameAssembler.toModel(makeGameResponse(authentication, updatedGame), authentication);
    }

    public void deleteGame(long id) {
        gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        gameRepository.deleteById(id);
    }


    private GameResponse makeGameResponse(Authentication authentication, Game game) {
        List<UserResponse> usersAsPublisherResponses = game.getPublishers().stream().map(UserResponse::new).toList();
        CollectionModel<?> publisherEntityModel = userAssembler.toCollectionModel(usersAsPublisherResponses, authentication);

        List<UserResponse> usersAsDevResponses = game.getPublishers().stream().map(UserResponse::new).toList();
        CollectionModel<?> DeveloperEntityModel = userAssembler.toCollectionModel(usersAsDevResponses, authentication);

        List<DiscountResponse> discountResponses = game.getDiscounts().stream().map(DiscountResponse::new).toList();
        CollectionModel<?> discountCollectionModel = discountAssembler.toCollectionModel(discountResponses, authentication);

        List<CategoryResponse> categoryResponses = game.getCategories().stream().map(CategoryResponse::new).toList();
        CollectionModel<?> categoryCollectionModel = categoryAssembler.toCollectionModel(categoryResponses, authentication);

        List<GameImageResponse> gameImageResponses = game.getGameImages().stream().map(gameImage -> new GameImageResponse(game.getId(), gameImage)).toList();
        CollectionModel<?> gameImageCollectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);

        GameResponse gameResponse = new GameResponse(game.getId(), game.getGameName(), game.getGameDescription(), game.getReleaseDate(), game.getGameBasePrice(), publisherEntityModel, DeveloperEntityModel, discountCollectionModel, categoryCollectionModel, gameImageCollectionModel, StaticHelper.convertBlobToString(game.getGameThumbnail()));
        return gameResponse;
    }
}
