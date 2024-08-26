package com.example.steamreplica.util;

import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.repository.PurchaseRepository;
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
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        String requirePermission = (String) permission;
        AuthUserDetail authUserDetail = authentication.getPrincipal() != null ? (AuthUserDetail) authentication.getPrincipal() : null;

        // Check if the user id is the same as the auth
        if ("ownerRequest".equalsIgnoreCase(requirePermission)) {
            return checkOwnerRequest(authUserDetail.getId(), targetId);
        }
        
        // Check if the user is the owner of the data
        if ("ownedData".equalsIgnoreCase(requirePermission) && "Purchase".equalsIgnoreCase(targetType)) {
            return checkOwnedData(authUserDetail, targetId);
        }

        return false;
    }

    private boolean checkOwnerRequest(long userId, Serializable targetId) {
        return Objects.equals(userId, targetId);
    }

    private boolean checkOwnedData(AuthUserDetail authUserDetail, Serializable targetId) {
        Purchase purchase = purchaseRepository.findById((Long) targetId)
                .orElseThrow(() -> new AuthenticationException("Purchase not found with id [" + targetId + "]"));
        return Objects.equals(purchase.getBoughtLibrary().getId(), authUserDetail.getId());
    }
}
