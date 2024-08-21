package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.PurchaseTransactionRepository;
import com.example.steamreplica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoughtLibraryService {
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final PurchaseTransactionRepository purchaseTransactionRepository;
    private final UserRepository userRepository;

    public BoughtLibrary saveBoughtLibrary(BoughtLibrary boughtLibrary) {
        return boughtLibraryRepository.save(boughtLibrary);
    }

    public BoughtLibrary getBoughtLibraryById(Long id) {
        return boughtLibraryRepository.findById(id).orElse(null);
    }

    public List<BoughtLibrary> getAllBoughtLibraries() {
        return boughtLibraryRepository.findAll();
    }

    public void deleteBoughtLibrary(Long id) {
        boughtLibraryRepository.deleteById(id);
    }

    public BoughtLibrary updateBoughtLibrary(BoughtLibrary boughtLibrary) {
        return boughtLibraryRepository.save(boughtLibrary);
    }

    public void addPurchaseTransactionToBoughtLibrary(Long boughtLibraryId, Purchases purchases) {
        BoughtLibrary boughtLibrary = boughtLibraryRepository.findById(boughtLibraryId).orElse(null);
        if (boughtLibrary != null) {
            purchases.setBoughtLibrary(boughtLibrary);
            purchaseTransactionRepository.save(purchases);
        }
    }

    public void addUserToBoughtLibrary(Long boughtLibraryId, User user) {
        BoughtLibrary boughtLibrary = boughtLibraryRepository.findById(boughtLibraryId).orElse(null);
        if (boughtLibrary != null) {
            user.setBoughtLibrary(boughtLibrary);
            userRepository.save(user);
        }
    }
}
