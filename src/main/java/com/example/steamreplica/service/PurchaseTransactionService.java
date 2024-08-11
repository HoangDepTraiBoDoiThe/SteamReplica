package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import com.example.steamreplica.repository.PurchaseTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseTransactionService {
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    
    public List<PurchaseTransaction> getAllPurchaseTransactions() {
        return purchaseTransactionRepository.findAll();
    }
    
    public PurchaseTransaction getPurchaseTransactionById(long id) {
        return purchaseTransactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found"));
    }
    
    public PurchaseTransaction createPurchaseTransaction(PurchaseTransaction newPurchaseTransaction) {
        return purchaseTransactionRepository.save(newPurchaseTransaction);
    }
    
    public PurchaseTransaction updatePurchaseTransaction(long id, PurchaseTransaction newPurchaseTransactionData) {
        PurchaseTransaction existingPurchaseTransaction = purchaseTransactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found"));
        
        if (existingPurchaseTransaction != null) {
            existingPurchaseTransaction.setPurchasedGames(newPurchaseTransactionData.getPurchasedGames());
            existingPurchaseTransaction.setTransactionDate(newPurchaseTransactionData.getTransactionDate());
            existingPurchaseTransaction.setBoughtLibrary(newPurchaseTransactionData.getBoughtLibrary());
            
            return purchaseTransactionRepository.save(existingPurchaseTransaction);
        }
        
        return null;
    }
    
    public void deletePurchaseTransaction(long id) {
        PurchaseTransaction existingPurchaseTransaction = purchaseTransactionRepository.findById(id).orElse(null);
        
        if (existingPurchaseTransaction != null) {
            purchaseTransactionRepository.deleteById(id);
        }
    }
}
