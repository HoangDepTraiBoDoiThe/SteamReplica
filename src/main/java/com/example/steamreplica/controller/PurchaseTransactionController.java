package com.example.steamreplica.controller;

import com.example.steamreplica.controller.assembler.PurchaseTransactionAssembler;
import com.example.steamreplica.dtos.request.PurchaseTransactionRequest;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import com.example.steamreplica.service.PurchaseTransactionService;
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
public class PurchaseTransactionController {
    private final PurchaseTransactionService purchaseTransactionService;
    private final PurchaseTransactionAssembler purchaseTransactionAssembler;

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable long id) {
//        if (result.hasErrors()) return ResponseEntity.badRequest().body(StaticHelper.extractBindingErrorMessages(result));

        Purchases purchases = purchaseTransactionService.getPurchaseTransactionById(id);
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(purchases));
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        var entityModels = purchaseTransactionAssembler.toCollectionModel(purchaseTransactionService.getAllPurchaseTransactions());
        entityModels.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseTransactionController.class)).withSelfRel().withType("GET"));
        return ResponseEntity.ok(entityModels);
    }

    @PostMapping()
    public ResponseEntity<?> createTransaction(@RequestBody @Validated PurchaseTransactionRequest purchaseTransactionRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        Purchases newPurchases = purchaseTransactionService.createPurchaseTransaction(purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(newPurchases));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody @Validated PurchaseTransactionRequest purchaseTransactionRequest, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        Purchases updatedPurchases = purchaseTransactionService.updatePurchaseTransaction(id, purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(updatedPurchases));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
        purchaseTransactionService.deletePurchaseTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
