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
import org.springframework.hateoas.EntityModel;
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
    private final ServiceHelper serviceHelper;
    
    public EntityModel<GameImageResponse> getGameImageById(long gameId, Authentication authentication) {
        GameImage gameImage = gameImageRepository.findById(gameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", gameId)));
        return serviceHelper.makeGameImageResponse(GameImageResponse.class, gameImage, authentication);
    }
    
    public List<EntityModel<ImageResponse>> getAllImagesByGameId(long gameId, Authentication authentication) {
        Collection<GameImage> gameImages = gameImageRepository.findAllByGameId(gameId);
        return gameImages.stream().map(gameImage -> serviceHelper.makeGameImageResponse(ImageResponse.class, gameImage, authentication)).toList();
    }
    
    public List<EntityModel<GameImageResponse>> addGameImagesToGame(long GameId, List<GameImageRequest> gameImageRequests, Authentication authentication) {
        Game game = gameRepository.findById(GameId).orElseThrow(() -> new ResourceNotFoundException(String.format("Game with id [%d] not found", GameId)));
        List<GameImage> newGameImages = gameImageRequests.stream().map(gameImageRequest -> new GameImage(gameImageRequest.getImageName(), StaticHelper.convertToBlob(gameImageRequest.getImage()), game)).toList();
        List<GameImage> newCreatedGameImages = gameImageRepository.saveAll(newGameImages);
        return newCreatedGameImages.stream().map(gameImage -> serviceHelper.makeGameImageResponse(GameImageResponse.class, gameImage, authentication)).toList();
    }

    public void deleteGameImages(Set<Long> imageIdsToDelete) {
        imageIdsToDelete.forEach(this::deleteGameImage);
    }

    public void deleteGameImage(long id) {
        gameImageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Game image with id [%d] not found", id)));
        gameImageRepository.deleteById(id);
    }
}
