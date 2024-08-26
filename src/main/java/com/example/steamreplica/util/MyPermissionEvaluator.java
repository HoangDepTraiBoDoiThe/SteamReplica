package com.example.steamreplica.util;

import com.example.steamreplica.model.BaseCacheableModel;
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
import java.util.function.Function;

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
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        
        // Check if the user id is the same as the auth
        if ("ownerRequest".equalsIgnoreCase(requirePermission)) {
            return Objects.equals(authUserDetail.getId(), targetId);
        }        
        
        // 
        if ("ownedData".equalsIgnoreCase(requirePermission)) {
            if ("Purchase".equalsIgnoreCase(targetType)) {
                Purchase purchase = purchaseRepository.findById((Long) targetId).orElseThrow(() -> new AuthenticationException("Purchase not found with id [" + targetId + "]"));
                return Objects.equals(purchase.getBoughtLibrary().getId(), authUserDetail.getId());
            }
        }
        
//        if ("Purchase".equals(targetType)) {
//            Purchase purchase = purchaseRepository.findAllByBoughtLibrary_Id((Long) targetId);
//            
//        }
        return false;
    }
}
