package com.example.steamreplica.util;

import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.purchasedLibrary.DevOwnedLibrary;
import com.example.steamreplica.model.purchasedLibrary.PublisherOwnedLibrary;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.GameImageRepository;
import com.example.steamreplica.repository.GameRepository;
import com.example.steamreplica.repository.PurchaseRepository;
import com.example.steamreplica.service.GameService;
import com.example.steamreplica.service.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MyPermissionEvaluator implements PermissionEvaluator {
    private final PurchaseRepository purchaseRepository;
    private final GameRepository gameRepository;
    private final GameImageRepository gameImageRepository;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String requirePermission = (String) permission;
        AuthUserDetail authUserDetail = Objects.equals("anonymousUser", authentication.getPrincipal()) ? null : (AuthUserDetail) authentication.getPrincipal();

        // Check if the user id is the same as the auth
        if ("ownerRequest".equalsIgnoreCase(requirePermission)) {
            return checkOwnerRequest(authentication, targetId);
        }
        
        // Check if the user is the owner of the data
        if ("ownedData".equalsIgnoreCase(requirePermission)) {
            if (authUserDetail == null) return false;
            else if (authUserDetail.getRoles().contains("ADMIN")) return true;
            else if ("Purchase".equalsIgnoreCase(targetType)) {
                Purchase purchase = purchaseRepository.findById((Long) targetId).orElseThrow(() -> new AuthenticationException("Purchase not found with id [" + targetId + "]"));
                return checkOwnerRequest(authentication, purchase.getBoughtLibrary().getId());
            } else if ("Game_Dev".equalsIgnoreCase(targetType)) {
                Game game = gameRepository.findById((Long) targetId).orElseThrow(() -> new AuthenticationException("Game not found with id [" + targetId + "]"));
                DevOwnedLibrary devOwnedLibrary = game.getDevOwners().stream().filter(library -> Objects.equals(library.getId(), (Long)targetId)).findFirst().orElse(null);
                if (devOwnedLibrary == null) return false;
                return checkOwnerRequest(authentication, devOwnedLibrary.getId());
            } else if ("Game_Pub".equalsIgnoreCase(targetType)) {
                Game game = gameRepository.findGamesByIdWithPublisherOwners((Long) targetId).orElseThrow(() -> new AuthenticationException("Game not found with id [" + targetId + "]"));
                PublisherOwnedLibrary gamePublisherOwnedLibrary = game.getPublisherOwners().stream().filter(library -> Objects.equals(library.getId(), (Long)targetId)).findFirst().orElse(null);
                if (gamePublisherOwnedLibrary == null) return false;
                return checkOwnerRequest(authentication, gamePublisherOwnedLibrary.getId());
            } else if ("Game_Image".equalsIgnoreCase(targetType)) {
                GameImage gameImage = gameImageRepository.findGameImageWithOwner((Long) targetId).orElseThrow(() -> new AuthenticationException("Game image not found with id [" + targetId + "]"));
                return gameImage.getGame().getPublisherOwners().stream().anyMatch(library -> checkOwnerRequest(authentication, library.getId()));
            }
        }

        return false;
    }

    public boolean checkOwnerRequest(Authentication authentication, Serializable targetId) {
        AuthUserDetail authUserDetail = authentication.getPrincipal() != null ? (AuthUserDetail) authentication.getPrincipal() : null;
        return Objects.equals(Objects.requireNonNull(authUserDetail).getId(), targetId);
    }
}
