package com.example.steamreplica.service;

import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.PurchaseRepository;
import com.example.steamreplica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoughtLibraryService {
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final PurchaseRepository purchaseRepository;
    private final UserRepository userRepository;

    public BoughtLibrary getBoughtLibraryById(Long id) {
        return boughtLibraryRepository.findById(id).orElse(null);
    }

    public List<BoughtLibrary> getAllBoughtLibraries() {
        return boughtLibraryRepository.findAll();
    }

    public BoughtLibrary updateBoughtLibrary(BoughtLibrary boughtLibrary) {
        return boughtLibraryRepository.save(boughtLibrary);
    }

    public void addPurchaseTransactionToBoughtLibrary(Long boughtLibraryId, Purchase purchase) {
        BoughtLibrary boughtLibrary = boughtLibraryRepository.findById(boughtLibraryId).orElse(null);
        if (boughtLibrary != null) {
            purchase.setBoughtLibrary(boughtLibrary);
            purchaseRepository.save(purchase);
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
