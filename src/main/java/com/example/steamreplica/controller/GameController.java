package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.GameRequest;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
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
        EntityModel<GameResponse_Full> gameResponseEntityModel = gameService.getGameById(id, authentication);
        return ResponseEntity.ok(gameResponseEntityModel);
    }

    @GetMapping("/{user_id}/purchased-games")
    @PreAuthorize("hasPermission(#user_id, 'Game', 'ownerRequest')")
    public ResponseEntity<?> getGamesPurchased(@PathVariable long user_id, Authentication authentication) {
        return ResponseEntity.ok(gameService.getGamesPurchased(user_id, authentication));
    }

//    @GetMapping("/{id}/reviews")
//    public ResponseEntity<?> getGameReviews(Authentication authentication) {
//        return ResponseEntity.ok(gameService.getGamesPurchased(authentication));
//    }

    @GetMapping("/new-and-trending")
    public ResponseEntity<?> getNewAndTrendingGames(@RequestParam int page, Authentication authentication) {
        return ResponseEntity.ok(gameService.getNewAndTrendingGames(page, authentication));
    }

    @GetMapping("/top-seller")
    public ResponseEntity<?> getTopSellerGames(@RequestParam int page, Authentication authentication) {
        return ResponseEntity.ok(gameService.getTopSellerGames(page, authentication));
    }

    @GetMapping("/special")
    public ResponseEntity<?> getSpecialGames(@RequestParam int page, Authentication authentication) {
        return ResponseEntity.ok(gameService.getSpecialGames(page, authentication));
    }

    @GetMapping("/category/{category_id}")
    public ResponseEntity<CollectionModel<EntityModel<GameResponse_Basic>>> getGamesOfCategory(@RequestParam int page, @PathVariable long category_id, Authentication authentication) {
        CollectionModel<EntityModel<GameResponse_Basic>> entityModelList = gameService.getGamesOfCategory(page, category_id, authentication);
        return ResponseEntity.ok(entityModelList);
    }

    @GetMapping("/dev-owned-games/{dev_id}")
    public ResponseEntity<?> getGamesBelongToDev(@RequestParam int page, @PathVariable long dev_id, Authentication authentication) {
        return ResponseEntity.ok(gameService.getDevOwningGames(page, dev_id, authentication));
    }

    @GetMapping("/publisher-owned-games/{publisher_id}")
    public ResponseEntity<?> getGamesBelongToPublisher(@RequestParam int page, @PathVariable long publisher_id, Authentication authentication) {
        return ResponseEntity.ok(gameService.getPublisherOwningGames(page, publisher_id, authentication));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER')")
    @PostMapping("/create")
    public ResponseEntity<?> createNewGame(@RequestBody @Validated GameRequest gameRequest, Authentication authentication) {
        return ResponseEntity.ok(gameService.addGame(gameRequest, authentication));
    }

    @PreAuthorize("hasPermission(#id, 'Game_Pub', 'ownedData')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateGame(@RequestBody GameRequest gameRequest, @PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(gameService.updateGame(id, gameRequest, authentication));
    }

    @PreAuthorize("hasPermission(#id, 'Game_Pub', 'ownedData')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteGame(@PathVariable long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }
}
