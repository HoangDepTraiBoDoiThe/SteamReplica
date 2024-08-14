package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.GameAssembler;
import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.repository.UserRepository;
import com.example.steamreplica.service.exception.GameException;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final GameAssembler gameAssembler;
    
    public CollectionModel<EntityModel<GameResponse>> getAllGames(Authentication authentication) {
        return gameAssembler.toCollectionModel(gameRepository.findAll(), authentication);
    }

    public EntityModel<GameResponse> getGameById(long id, Authentication authentication) {
        Game game = gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        return gameAssembler.toModel(game, authentication);
    }
    
    public EntityModel<GameResponse> addGame(GameRequest gameRequest, Authentication authentication) {
        // Todo: others are required.
        gameRepository.findGameByGameName(gameRequest.getName()).orElseThrow(() -> new GameException("Game already exists"));
        
        Game newCreatedGame = gameRepository.save(gameRequest.toModel());
        newCreatedGame.setDevelopers(new HashSet<>(userRepository.findAllById(gameRequest.getDeveloperIds())));
        newCreatedGame.setPublishers(new HashSet<>(userRepository.findAllById(gameRequest.getPublisherIds())));
        
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
//        gameToUpdate.setGameImages(gameRequest.getGameImages());
//        gameToUpdate.setCategories(gameRequest.getCategories());

        Game updatedGame = gameRepository.save(gameToUpdate);
        return gameAssembler.toModel(updatedGame, authentication);
    }

    public void deleteGame(long id) {
        gameRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with this id [%s] not found", id)));
        gameRepository.deleteById(id);
    }
}
