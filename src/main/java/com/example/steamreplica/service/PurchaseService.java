package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.PurchaseAssembler;
import com.example.steamreplica.dtos.request.PurchaseRequest;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Basic;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Full;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.example.steamreplica.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final BoughtLibraryService boughtLibraryService;
    private final GameService gameService;
    private final DlcService dlcService;
    private final ServiceHelper serviceHelper;
    
    public List<EntityModel<PurchaseResponse_Basic>> getAllPurchases(Authentication authentication) {
        return purchaseRepository.findAll().stream().map(purchase -> serviceHelper.makePurchaseResponse(PurchaseResponse_Basic.class, purchase, authentication)).toList();
    }
    
    public EntityModel<PurchaseResponse_Full> getPurchaseById(long id, Authentication authentication) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found"));
        return serviceHelper.makePurchaseResponse(PurchaseResponse_Full.class, purchase, authentication);
    }
    
    @Transactional
    public EntityModel<PurchaseResponse_Full> createPurchase(PurchaseRequest request, Authentication authentication) {
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        BoughtLibrary boughtLibrary = boughtLibraryService.getBoughtLibraryById(authUserDetail.getId());
        Set<PurchasedGame> purchasedGames = request.getGameIds().stream().map(aLong -> new PurchasedGame(gameService.getGameById_entity(aLong))).collect(Collectors.toSet());
        Set<PurchasedDLC> purchasedDLCSs = request.getDlcIds().stream().map(aLong -> new PurchasedDLC(dlcService.getDlcById_entity(aLong))).collect(Collectors.toSet());
        
        Purchase purchase = new Purchase(ZonedDateTime.now(), "None", boughtLibrary, purchasedGames, purchasedDLCSs);
        Purchase newCreatedPurchase = purchaseRepository.save(purchase);
        return serviceHelper.makePurchaseResponse(PurchaseResponse_Full.class, newCreatedPurchase, authentication);
    }
    
//    public EntityModel<PurchaseResponse_Full> updatePurchase(long id, PurchaseRequest request, Authentication authentication) {
//        Purchase existingPurchase = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));
//
//        existingPurchase.setPurchasedGames(request.getPurchasedGames());
//        existingPurchase.setTransactionDate(request.getTransactionDate());
//        existingPurchase.setBoughtLibrary(request.getBoughtLibrary());
//
//        return purchaseRepository.save(existingPurchase);
//    }
    
    public void deletePurchase(long id) {
        purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));
        purchaseRepository.deleteById(id);
    }
}
