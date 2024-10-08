package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.GameImageRequest;
import com.example.steamreplica.dtos.response.game.GameImageResponse;
import com.example.steamreplica.service.GameImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games/{game_id}/images")
@RequiredArgsConstructor
public class GameImageController {
    private final GameImageService gameImageService;
    
    @PostMapping()
    @PreAuthorize("hasPermission(#game_id, 'game_Pub', 'ownedData')")
    public ResponseEntity<?> createGameImage(@PathVariable long game_id, @RequestBody GameImageRequest request, Authentication authentication) {
        return new ResponseEntity<>(gameImageService.addGameImageToGame(game_id, request, authentication), HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GameImageResponse>> getGameImage(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(gameImageService.findGameImage(id, authentication));
    }
    
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<GameImageResponse>>> getGameImageOfGame(@PathVariable long game_id, Authentication authentication) {
        return ResponseEntity.ok(gameImageService.findGameImageOfGame(game_id, authentication));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Game_Image', 'ownedData')")
    public ResponseEntity<?> updateGameImage(@PathVariable long id, @RequestBody GameImageRequest request, Authentication authentication) {
        EntityModel<GameImageResponse> gameImageResponseEntityModel = gameImageService.updateGameImageToGame(id, request, authentication);
        return ResponseEntity.ok(gameImageResponseEntityModel);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Game_Image', 'ownedData')")
    public void deleteGameImage(@PathVariable long id) {
        gameImageService.deleteGameImage(id);
    }
}
