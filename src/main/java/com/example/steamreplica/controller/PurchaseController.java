package com.example.steamreplica.controller;

import com.example.steamreplica.controller.assembler.PurchaseAssembler;
import com.example.steamreplica.dtos.request.PurchaseTransactionRequest;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import com.example.steamreplica.service.PurchaseService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final PurchaseAssembler purchaseAssembler;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable long id) {
//        if (result.hasErrors()) return ResponseEntity.badRequest().body(StaticHelper.extractBindingErrorMessages(result));

        Purchases purchases = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchaseAssembler.toModel(purchases));
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        var entityModels = purchaseAssembler.toCollectionModel(purchaseService.getAllPurchases());
        entityModels.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseController.class)).withSelfRel().withType("GET"));
        return ResponseEntity.ok(entityModels);
    }

    @PostMapping()
    public ResponseEntity<?> createTransaction(@RequestBody @Validated PurchaseTransactionRequest purchaseTransactionRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        Purchases newPurchases = purchaseService.createPurchase(purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseAssembler.toModel(newPurchases));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody @Validated PurchaseTransactionRequest purchaseTransactionRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        Purchases updatedPurchases = purchaseService.updatePurchas(id, purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseAssembler.toModel(updatedPurchases));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
        purchaseService.deletePurchase(id);
        return ResponseEntity.noContent().build();
    }
}
