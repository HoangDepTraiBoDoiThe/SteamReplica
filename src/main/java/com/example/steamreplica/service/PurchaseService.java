package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.PurchaseRequest;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Basic;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Full;
import com.example.steamreplica.event.DlcImageUpdateEvent;
import com.example.steamreplica.event.GameUpdateEvent;
import com.example.steamreplica.event.UserUpdateEvent;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.BoughtLibrary;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.example.steamreplica.model.userApplication.User;
import com.example.steamreplica.repository.BoughtLibraryRepository;
import com.example.steamreplica.repository.PurchaseRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final BoughtLibraryRepository boughtLibraryRepository;
    private final GameService gameService;
    private final UserService userService;
    private final DlcService dlcService;
    private final DiscountService discountService;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    private final String PURCHASE_LIST_CACHE = "purchaseListCache";
    private final String PURCHASE_CACHE = "purchaseCache";

    private final Integer PAGE_RANGE = 10;
    private final Integer PAGE_SIZE = 10;

    @EventListener
    private void userUpdated(UserUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                PURCHASE_CACHE,
                List.of(),
                List.of(PURCHASE_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    User user = userService.findUsersWithById_entityFull((Long) id);
                    return user.getBoughtLibrary().getPurchases().stream().anyMatch(purchase -> Objects.equals(purchase.getId(), id));
                });
    }
    
    @EventListener
    private void gameUpdated(GameUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                PURCHASE_CACHE,
                List.of(),
                List.of(PURCHASE_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    Game game = gameService.findGameWithById_withPurchasedGame((Long) id);
                    return game.getPurchasedGame().stream().anyMatch(purchasedGame -> Objects.equals(purchasedGame.getTransaction().getId(), id));
                });
    }
    
    @EventListener
    private void dlcUpdated(DlcImageUpdateEvent updateEvent) {
        cacheHelper.refreshAllCachesSelectiveOnUpdatedEventReceived(
                PURCHASE_CACHE,
                List.of(),
                List.of(PURCHASE_LIST_CACHE),
                PAGE_RANGE,
                updateEvent.getId(),
                (entity, id) -> {
                    DLC dlc = dlcService.getDlcById_withPurchasedDLCs((Long) id);
                    return dlc.getPurchasedDLCs().stream().anyMatch(purchasedDLC -> Objects.equals(purchasedDLC.getTransaction().getId(), id));
                });
    }
    
    public CollectionModel<EntityModel<PurchaseResponse_Basic>> getAllPurchasesOfUser(long user_id, Authentication authentication) {
        List<Purchase> purchases = cacheHelper.getListCache(PURCHASE_LIST_CACHE, purchaseRepository, repo -> repo.findAllByBoughtLibrary_Id(user_id).stream().toList());
        return serviceHelper.makePurchaseResponse_CollectionModel(PurchaseResponse_Basic.class, purchases, authentication);
    }
    
    public EntityModel<PurchaseResponse_Full> getPurchaseById(long id, Authentication authentication) {
        Purchase purchase = cacheHelper.getCache(PURCHASE_CACHE, id, purchaseRepository, repo -> repo.findById(id).orElseThrow(() -> new RuntimeException("Purchase Transaction not found")));
        return serviceHelper.makePurchaseResponse(PurchaseResponse_Full.class, purchase, authentication);
    }
    
    @Transactional
    public EntityModel<PurchaseResponse_Full> createPurchase(PurchaseRequest request, Authentication authentication) {
        AuthUserDetail authUserDetail = (AuthUserDetail) authentication.getPrincipal();
        BoughtLibrary boughtLibrary = boughtLibraryRepository.findById(authUserDetail.getId()).orElseThrow(() -> new ResourceNotFoundException("Can not get Library"));
        
        Set<PurchasedGame> purchasedGames = request.getGameIds().stream().map(aLong -> {
            Game game = gameService.getGameById_entity(aLong);
            double gameTotalDiscount = game.getDiscounts().stream().mapToDouble(Discount::getDiscountPercent).sum();
            return new PurchasedGame(game, gameTotalDiscount);
        }).collect(Collectors.toSet());
        Set<PurchasedDLC> purchasedDLCSs = request.getDlcIds().stream().map(aLong -> {
            DLC dlc = dlcService.getDlcById_entity(aLong);
            double dlcTotalDiscount = dlc.getDiscounts().stream().mapToDouble(Discount::getDiscountPercent).sum();
            return new PurchasedDLC(dlcService.getDlcById_entity(aLong), dlcTotalDiscount);
        }).collect(Collectors.toSet());
        
        Discount additionalDiscount = discountService.getDiscountById_entity(request.getAdditionalDiscountId(), authentication);
        
        Purchase purchase = new Purchase(ZonedDateTime.now(), "None", boughtLibrary, purchasedGames, purchasedDLCSs, additionalDiscount);
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
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));
        cacheHelper.deleteCaches(PURCHASE_CACHE, purchase.getId(), PURCHASE_LIST_CACHE);
        purchaseRepository.deleteById(id);
    }
}
