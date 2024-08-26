package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.service.DlcService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games/{game_id}/dlcs")
public class DlcController {
    private final DlcService dlcService;
    
    @GetMapping("/{dlc_id}")
    public ResponseEntity<?> getDlcById(@PathVariable long dlc_id, Authentication authentication) {
        return ResponseEntity.ok(dlcService.getDlcById(dlc_id, authentication));
    }   
    
    @GetMapping("/purchased-dlc")
    public ResponseEntity<?> getPurchasedDlcOfGame(@PathVariable long game_id, Authentication authentication) {
        return ResponseEntity.ok(dlcService.getPurchasedDlcOfGame(game_id, authentication));
    }
    
    @GetMapping("/purchased-dlc")
    public ResponseEntity<CollectionModel<EntityModel<DlcResponse_Basic>>> getAllDlcOfGame(@PathVariable long game_id, @RequestParam int page, Authentication authentication) {
        return ResponseEntity.ok(dlcService.getAllDlcOfGame(game_id, page, authentication));
    }
}
