package com.example.steamreplica.service;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.controller.GameController;
import com.example.steamreplica.controller.GameImageController;
import com.example.steamreplica.controller.assembler.GameImageAssembler;
import com.example.steamreplica.dtos.request.GameImageRequest;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.dtos.response.GameImageResponse;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.repository.GameImageRepository;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GameImageService {
    private final GameImageRepository gameImageRepository;
    private final GameRepository gameRepository;
    private final GameImageAssembler gameImageAssembler;
    
    public EntityModel<GameImageResponse> getGameImageById(long gameId, Authentication authentication) {
        GameImage gameImage = gameImageRepository.findById(gameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", gameId)));
        GameImageResponse gameImageResponse = new GameImageResponse(gameId, gameImage);
        return gameImageAssembler.toModel(gameImageResponse, authentication);
    }
    
    public CollectionModel<EntityModel<GameImageResponse>> getAllImagesByGameId(long gameId, Authentication authentication) {
        Collection<GameImage> gameImages = gameImageRepository.findAllByGameId(gameId);
        List<GameImageResponse> gameImageResponses = gameImages.stream().map(gameImage -> new GameImageResponse(gameId, gameImage)).toList();
        return gameImageAssembler.toCollectionModel(gameImageResponses, authentication);
    }

    public CollectionModel<EntityModel<GameImageResponse>> addGameImagesToGame(long GameId, List<GameImageRequest> gameImageRequests, Authentication authentication) {
        Game game = gameRepository.findById(GameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id [%d] not found", GameId)));
        List<GameImage> newGameImages = gameImageRequests.stream().map(gameImageRequest -> new GameImage(gameImageRequest.getImageName(), gameImageRequest.getImage(), game)).toList();
        List<GameImage> newCreatedGameImages = gameImageRepository.saveAll(newGameImages);
        List<GameImageResponse> gameImageResponses = newCreatedGameImages.stream().map(gameImage -> new GameImageResponse(GameId, gameImage)).toList();

        CollectionModel<?> collectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);
        collectionModel.add(
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameImageController.class).getAllImagesByGameId(GameId, authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGame(GameId, authentication)).withRel("Get game").withType(HttpRequestTypes.GET.name())
        );
        return gameImageAssembler.toCollectionModel(gameImageResponses, authentication);
    }

    public void deleteGameImages(Set<Long> imageIdsToDelete) {
        imageIdsToDelete.forEach(this::deleteGameImage);
    }

    public void deleteGameImage(long id) {
        gameImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", id)));
        gameImageRepository.deleteById(id);
    }
}
