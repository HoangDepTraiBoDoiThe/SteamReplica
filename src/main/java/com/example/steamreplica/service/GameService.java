package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.GameAssembler;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.model.game.GameImage;
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
    private final GameAssembler gameAssembler;
    
    public CollectionModel<EntityModel<GameResponse>> getAllGames(Authentication authentication) {
        return gameAssembler.toCollectionModel(gameRepository.findAll(), authentication);
    }

    public EntityModel<GameResponse> getGameById(long id, Authentication authentication) {
        Game game = gameRepository.findGameWithAllImagesById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        return gameAssembler.toModel(game, authentication);
    }
    
    public EntityModel<GameResponse> addGame(GameRequest gameRequest, Authentication authentication) {
        // Todo: others are required.
        if (gameRepository.findGameByGameName(gameRequest.getName()).isPresent()) throw new GameException("Game already exists");
        Game newGame = gameRequest.toModel();
        newGame.setDevelopers(new HashSet<>(userRepository.findAllById(gameRequest.getDeveloperIds())));
        newGame.setPublishers(new HashSet<>(userRepository.findAllById(gameRequest.getPublisherIds())));
        newGame.setDiscounts(new HashSet<>(discountRepository.findAllById(gameRequest.getDiscountIds())));
        List<GameImage> newGameImages = gameRequest.getGameImagesRequest().stream().map(image -> new GameImage(image.getImageName(), StaticHelper.convertToBlob(image.getImage()), newGame)).toList();
        newGame.setGameImages(new HashSet<>(newGameImages));

        Game newCreatedGame = gameRepository.save(newGame);
        return gameAssembler.toModel(newCreatedGame, authentication);
    }

    public EntityModel<GameResponse> updateGame(long id, GameRequest gameRequest, Authentication authentication) {
        // Todo: publishers, devs should be put into the consideration.
        
        Game gameToUpdate = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Game not found"));
        gameToUpdate.setGameName(gameRequest.getName());
        gameToUpdate.setGameDescription(gameRequest.getDescription());
        gameToUpdate.setGameBasePrice(gameRequest.getPrice());
        gameToUpdate.setReleaseDate(gameRequest.getReleaseDate());
        gameToUpdate.setDevelopers(new HashSet<>(userRepository.findAllById(gameRequest.getDeveloperIds())));
        gameToUpdate.setPublishers(new HashSet<>(userRepository.findAllById(gameRequest.getPublisherIds())));
        gameToUpdate.setDiscounts(new HashSet<>(discountRepository.findAllById(gameRequest.getPublisherIds())));
//        gameToUpdate.setGameImages(gameRequest.getGameImages());

        Game updatedGame = gameRepository.save(gameToUpdate);
        return gameAssembler.toModel(updatedGame, authentication);
    }

    public void deleteGame(long id) {
        gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        gameRepository.deleteById(id);
    }
}
