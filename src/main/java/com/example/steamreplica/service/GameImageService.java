package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.GameImageRequest;
import com.example.steamreplica.dtos.response.game.ImageResponse;
import com.example.steamreplica.dtos.response.game.GameImageResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.repository.GameImageRepository;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.ServiceHelper;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameImageService {
    private final GameImageRepository gameImageRepository;
    private final GameRepository gameRepository;
    private final ServiceHelper serviceHelper;
    
    public List<EntityModel<GameImageResponse>> addGameImagesToGame(long GameId, List<GameImageRequest> gameImageRequests, Authentication authentication) {
        Game game = gameRepository.findById(GameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id [%d] not found", GameId)));
        List<GameImage> newGameImages = gameImageRequests.stream().map(gameImageRequest -> new GameImage(gameImageRequest.getImageName(), StaticHelper.convertToBlob(gameImageRequest.getImage()), game)).toList();
        List<GameImage> newCreatedGameImages = gameImageRepository.saveAll(newGameImages);
        return newCreatedGameImages.stream().map(gameImage -> serviceHelper.makeGameImageResponse(GameImageResponse.class, gameImage, authentication)).toList();
    }
    
    public EntityModel<GameImageResponse> addGameImageToGame(long GameId, GameImageRequest gameImageRequest, Authentication authentication) {
        Game game = gameRepository.findById(GameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id [%d] not found", GameId)));
        GameImage newGameImages = new GameImage(gameImageRequest.getImageName(), StaticHelper.convertToBlob(gameImageRequest.getImage()), game);
        GameImage newCreatedGameImages = gameImageRepository.save(newGameImages);
        return serviceHelper.makeGameImageResponse(GameImageResponse.class, newCreatedGameImages, authentication);
    }
    
    public EntityModel<GameImageResponse> findGameImage(long id, Authentication authentication) {
        GameImage image = gameImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", id)));
        return serviceHelper.makeGameImageResponse(GameImageResponse.class, image, authentication);
    }
    
    public CollectionModel<EntityModel<GameImageResponse>> findGameImageOfGame(long game_id, Authentication authentication) {
        Game game = gameRepository.findById_withWithImages(game_id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id [%d] not found", game_id)));
        Set<GameImage> gameImages = game.getGameImages();
        CollectionModel<EntityModel<GameImageResponse>> entityModels = gameImages.stream().map(gameImage -> serviceHelper.makeGameImageResponse(GameImageResponse.class, gameImage, authentication)).collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
        return entityModels;
    }
    
    public EntityModel<GameImageResponse> updateGameImageToGame(long id, GameImageRequest gameImageRequest, Authentication authentication) {
        GameImage image = gameImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", id)));
        image.setImageName(gameImageRequest.getImageName());
        image.setImage(StaticHelper.convertToBlob(gameImageRequest.getImage()));
        return serviceHelper.makeGameImageResponse(GameImageResponse.class, gameImageRepository.save(image), authentication);
    }

    public void deleteGameImages(Set<Long> imageIdsToDelete) {
        imageIdsToDelete.forEach(this::deleteGameImage);
    }

    public void deleteGameImage(long id) {
        gameImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", id)));
        gameImageRepository.deleteById(id);
    }
}
