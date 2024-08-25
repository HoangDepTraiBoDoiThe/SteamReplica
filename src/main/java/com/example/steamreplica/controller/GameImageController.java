package com.example.steamreplica.controller;

import com.example.steamreplica.service.GameImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games/{id}/images")
@RequiredArgsConstructor
public class GameImageController {
    private final GameImageService gameImageService;
    
    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    public ResponseEntity<?> x(@PathVariable long id, Authentication authentication) {
        // todo: add and update image to game
        return null;
    }
}
