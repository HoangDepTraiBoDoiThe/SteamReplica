package com.example.steamreplica.controller;

import com.example.steamreplica.controller.assembler.HomeAssembler;
import com.example.steamreplica.dtos.response.HomeResponse;
import com.example.steamreplica.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final HomeAssembler homeAssembler;
    private final ServiceHelper serviceHelper;
    
    @GetMapping("/")
    public ResponseEntity<?> initialGet(Authentication authentication) {
        return ResponseEntity.ok(homeAssembler.toModel(new HomeResponse("Halola amigosu!!!"), authentication));
    }
}
