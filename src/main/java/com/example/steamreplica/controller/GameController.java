package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
        EntityModel<GameResponse> gameResponseEntityModel = gameService.getGameById(id, authentication);
        return ResponseEntity.ok(gameResponseEntityModel);
    }

    @GetMapping
    public ResponseEntity<?> getGames(Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        return ResponseEntity.ok(gameService.getAllGames(authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    @PostMapping("/create")
    public ResponseEntity<?> createNewGame(@RequestBody GameRequest gameRequest, Authentication authentication) {
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
