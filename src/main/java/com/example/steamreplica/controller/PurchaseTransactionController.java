package com.example.steamreplica.controller;

import com.example.steamreplica.controller.assembler.PurchaseTransactionAssembler;
import com.example.steamreplica.dtos.transaction.PurchaseTransactionRequest;
import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import com.example.steamreplica.service.PurchaseTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
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

        PurchaseTransaction purchaseTransaction = purchaseTransactionService.getPurchaseTransactionById(id);
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(purchaseTransaction));
    }

    @GetMapping
    public ResponseEntity<?> getAllTransactions() {
        var entityModels = purchaseTransactionAssembler.toCollectionModel(purchaseTransactionService.getAllPurchaseTransactions());
        entityModels.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseTransactionController.class)).withSelfRel().withType("GET"));
        return ResponseEntity.ok(entityModels);
    }

    @PostMapping()
    public ResponseEntity<?> createTransaction(@RequestBody PurchaseTransactionRequest purchaseTransactionRequest) {
        PurchaseTransaction newPurchaseTransaction = purchaseTransactionService.createPurchaseTransaction(purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(newPurchaseTransaction));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable long id, @RequestBody PurchaseTransactionRequest purchaseTransactionRequest) {
        PurchaseTransaction updatedPurchaseTransaction = purchaseTransactionService.updatePurchaseTransaction(id, purchaseTransactionRequest.toPurchaseTransaction());
        return ResponseEntity.ok(purchaseTransactionAssembler.toModel(updatedPurchaseTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable long id) {
        purchaseTransactionService.deletePurchaseTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
