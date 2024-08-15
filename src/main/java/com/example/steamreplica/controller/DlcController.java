package com.example.steamreplica.controller;

import com.example.steamreplica.service.DlcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games/{game_id}/dlcs")
public class DlcController {
    private final DlcService dlcService;
    
    @GetMapping("/{dlc_id}")
    public ResponseEntity<?> getDlcById(@PathVariable long dlc_id, Authentication authentication) {
        return ResponseEntity.ok(dlcService.getDlcById(dlc_id, authentication));
    }
}
