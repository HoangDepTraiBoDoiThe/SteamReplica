package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.Purchases;
import com.example.steamreplica.repository.PurchaseTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseTransactionService {
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    
    public List<Purchases> getAllPurchaseTransactions() {
        return purchaseTransactionRepository.findAll();
    }
    
    public Purchases getPurchaseTransactionById(long id) {
        return purchaseTransactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found"));
    }
    
    public Purchases createPurchaseTransaction(Purchases newPurchases) {
        return purchaseTransactionRepository.save(newPurchases);
    }
    
    public Purchases updatePurchaseTransaction(long id, Purchases newPurchasesData) {
        Purchases existingPurchases = purchaseTransactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found"));
        
        if (existingPurchases != null) {
            existingPurchases.setPurchasedGames(newPurchasesData.getPurchasedGames());
            existingPurchases.setTransactionDate(newPurchasesData.getTransactionDate());
            existingPurchases.setBoughtLibrary(newPurchasesData.getBoughtLibrary());
            
            return purchaseTransactionRepository.save(existingPurchases);
        }
        
        return null;
    }
    
    public void deletePurchaseTransaction(long id) {
        Purchases existingPurchases = purchaseTransactionRepository.findById(id).orElse(null);
        
        if (existingPurchases != null) {
            purchaseTransactionRepository.deleteById(id);
        }
    }
}
