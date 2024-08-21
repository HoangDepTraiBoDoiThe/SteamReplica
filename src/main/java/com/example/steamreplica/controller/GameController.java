package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getGame(@PathVariable long id, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        EntityModel<GameResponse_Full> gameResponseEntityModel = gameService.getGameById(id, authentication);
        return ResponseEntity.ok(gameResponseEntityModel);
    }

    @GetMapping("/purchased-games")
    public ResponseEntity<?> getGamesPurchased(Authentication authentication) {
        return ResponseEntity.ok(gameService.getGamesPurchased(authentication));
    }

    @GetMapping("/purchased-games")
    public ResponseEntity<?> getPublisherOwnedGames(Authentication authentication) {
        return ResponseEntity.ok(gameService.getPublisherOwnedGames(authentication));
    }

    @GetMapping("/dev-owned-games")
    public ResponseEntity<?> getDevOwnedGames(Authentication authentication) {
        return ResponseEntity.ok(gameService.getDevOwnedGames(authentication));
    }

//    @GetMapping("/{id}/reviews")
//    public ResponseEntity<?> getGameReviews(Authentication authentication) {
//        return ResponseEntity.ok(gameService.getGamesPurchased(authentication));
//    }

    @GetMapping
    public ResponseEntity<?> getGames(Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        return ResponseEntity.ok(gameService.getAllGames(authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    @PostMapping("/create")
    public ResponseEntity<?> createNewGame(@RequestBody @Validated GameRequest gameRequest, Authentication authentication) {
        return ResponseEntity.ok(gameService.addGame(gameRequest, authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateGame(@RequestBody GameRequest gameRequest, @PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(gameService.updateGame(id, gameRequest, authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteGame(@PathVariable long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
