package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.PurchaseRequest;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Basic;
import com.example.steamreplica.service.PurchaseService;
import com.example.steamreplica.util.MyPermissionEvaluator;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final MyPermissionEvaluator myPermissionEvaluator;

    @PreAuthorize("hasPermission(#id, 'PUrchase', 'ownedData')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(purchaseService.getPurchaseById(id, authentication));
    }

    @PreAuthorize("hasPermission(#user_id, 'Purchase', 'ownerRequest')")
    @GetMapping("/buyer/{user_id}")
    public ResponseEntity<CollectionModel<EntityModel<PurchaseResponse_Basic>>> getAllTransactionsOfBuyer(@PathVariable long user_id, Authentication authentication) {
        return ResponseEntity.ok(purchaseService.getAllPurchasesOfUser(user_id, authentication));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody @Validated PurchaseRequest purchaseRequest, BindingResult result, Authentication authentication) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        
        return ResponseEntity.ok(purchaseService.createPurchase(purchaseRequest, authentication));
    }
    
//    @PutMapping("/{id}/update")
//    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody @Validated PurchaseRequest purchaseRequest, BindingResult result) {
//        var errors = StaticHelper.extractBindingErrorMessages(result);
//        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
//       
//        Purchase updatedPurchase = purchaseService.updatePurchase(id, purchaseRequest.toModel());
//        return ResponseEntity.ok(updatedPurchase);
//    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }
}
