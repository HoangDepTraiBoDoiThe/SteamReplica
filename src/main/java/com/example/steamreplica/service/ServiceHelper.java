package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.response.*;
import com.example.steamreplica.dtos.response.game.GameImageResponse_Full;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Minimal;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Basic;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Full;
import com.example.steamreplica.dtos.response.purchases.PurchaseDlcResponse;
import com.example.steamreplica.dtos.response.purchases.PurchaseGameResponse;
import com.example.steamreplica.dtos.response.purchases.PurchaseResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.example.steamreplica.model.userApplication.User;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceHelper {
    private final GameAssembler gameAssembler;
    private final UserAssembler userAssembler;
    private final DiscountAssembler discountAssembler;
    private final CategoryAssembler categoryAssembler;
    private final GameImageAssembler gameImageAssembler;
    private final DlcAssembler dlcAssembler;
    private final PurchaseAssembler purchaseAssembler;
    private final PurchaseGameAssembler purchaseGameAssembler;
    private final PurchaseDlcAssembler purchaseDlcAssembler;

    public <T extends BaseResponse> EntityModel<T> makeGameResponse(Class<T> responseType, Game game, Authentication authentication) {
        try {
            T response;

            List<DiscountResponse_Minimal> discountResponsesMinimal = game.getDiscounts().stream().map(DiscountResponse_Minimal::new).toList();

            List<EntityModel<CategoryResponse_Minimal>> categoryEntityModelList = game.getCategories().stream().map(category -> makeCategoryResponse(CategoryResponse_Minimal.class, category, authentication)).toList();
            if (GameResponse_Full.class.equals(responseType)) {
                List<EntityModel<UserResponse_Minimal>> usersAsPublisherResponses = game.getPublishers().stream().map(user -> makeUserResponse(UserResponse_Minimal.class, user, authentication)).toList();

                List<EntityModel<UserResponse_Minimal>> usersAsDevResponses = game.getPublishers().stream().map(user -> makeUserResponse(UserResponse_Minimal.class, user, authentication)).toList();

                List<EntityModel<GameResponse_Minimal>> gameImageResponses = game.getGameImages().stream().map(gameImage -> makeGameImageResponse(GameResponse_Minimal.class, gameImage, authentication)).toList();
                
                response = (T) new GameResponse_Full(game, usersAsPublisherResponses, usersAsDevResponses, discountResponsesMinimal, categoryEntityModelList, gameImageResponses);
            } else if (GameResponse_Basic.class.equals(responseType)) {
                response = (T) new GameResponse_Basic(game, discountResponsesMinimal, categoryEntityModelList);
            } else {
                response = responseType.getDeclaredConstructor(Long.class).newInstance(game.getId());
            }

            return gameAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeGameImageResponse(Class<T> responseType, GameImage gameImage, Authentication authentication) {
        try {
            T response;
            if (GameImageResponse_Full.class.equals(responseType)) {
                response = (T) new GameImageResponse_Full(gameImage, makeGameResponse(GameResponse_Minimal.class, gameImage.getGame(), authentication));
            } else {
                response = responseType.getDeclaredConstructor(GameImage.class).newInstance(gameImage);
            }

            return gameImageAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    } 

    public <T extends BaseResponse> EntityModel<T> makeCategoryResponse(Class<T> responseType, Category category, Authentication authentication) {
        try {
            T response;

            if (CategoryResponse_Full.class.equals(responseType)) {
                List<EntityModel<GameResponse_Basic>> gameResponses = category.getGames().stream().map(game -> makeGameResponse(GameResponse_Basic.class, game, authentication)).toList();
                response = (T) new CategoryResponse_Full(category, gameResponses);
            } else {
                response = responseType.getDeclaredConstructor(Category.class).newInstance(category);
            }

            return categoryAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeUserResponse(Class<T> responseType, User user, Authentication authentication) {
        try {
            T response;

            if (UserResponse_Full.class.equals(responseType)) {
                response = (T) new UserResponse_Full(user);
            } else {
                response = responseType.getDeclaredConstructor(User.class).newInstance(user);
            }

            return userAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeDlcResponse(Class<T> responseType, DLC dlc, Authentication authentication) {
        try {
            T response;
            if (DlcResponse_Full.class.equals(responseType)) {
                List<EntityModel<DiscountResponse_Minimal>> discountResponseBasics = dlc.getDiscounts().stream().map(discount -> makeDiscountResponse(DiscountResponse_Minimal.class, discount, authentication)).toList();
                EntityModel<GameResponse_Minimal> GameResponse_Basic = makeGameResponse(GameResponse_Minimal.class, dlc.getGame(), authentication);
                response = (T) new DlcResponse_Full(dlc, discountResponseBasics, null, GameResponse_Basic);
            } else {
                response = responseType.getDeclaredConstructor(DLC.class).newInstance(dlc);
            } 
            return dlcAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeDiscountResponse(Class<T> responseType, Discount discount, Authentication authentication) {
        T response;
        
        try {
            if (DiscountResponse_Full.class.equals(responseType)) {
                List<EntityModel<GameResponse_Minimal>> gameEntityModelList = discount.getDiscountedGames().stream().map(game -> makeGameResponse(GameResponse_Minimal.class, game, authentication)).toList();
                List<EntityModel<DlcResponse_Basic>> dlcEntityModelList = discount.getDiscountedDlc().stream().map(dlc -> makeDlcResponse(DlcResponse_Basic.class, dlc, authentication)).toList();
                response = (T) new DiscountResponse_Full(discount, gameEntityModelList, dlcEntityModelList);
            } else {
                response = responseType.getDeclaredConstructor(Discount.class).newInstance(discount);
            } 
            return discountAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makePurchaseResponse(Class<T> responseType, Purchase purchase, Authentication authentication) {
        try {
            T response;
            BigDecimal totalDlcPrice = purchase.getPurchasedDLCs().stream().map(purchasedDLC -> purchasedDLC.getPriceAtTheTime().multiply(BigDecimal.valueOf(100 - purchasedDLC.getDiscountPercent()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalGamePrice = purchase.getPurchasedGames().stream().map(purchasedGame -> purchasedGame.getGameBasePriceAtTheTime().multiply(BigDecimal.valueOf(purchasedGame.getDiscountPercent()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            double additionalDiscountPercent = purchase.getAdditionalDiscount().getDiscountPercent();

            BigDecimal purchasedTotalPrice = totalDlcPrice.add(totalGamePrice).multiply(BigDecimal.valueOf(100 - additionalDiscountPercent));
            if (PurchaseResponse_Full.class.equals(responseType)) {
                List<EntityModel<PurchaseDlcResponse>> purchasedDLCs = purchase.getPurchasedDLCs().stream().map(purchasedDLC -> makePurchaseDlcResponse(PurchaseDlcResponse.class, purchasedDLC, authentication)).toList();
                List<EntityModel<PurchaseGameResponse>> purchasedGames = purchase.getPurchasedGames().stream().map(purchasedGame -> makePurchaseGameResponse(PurchaseGameResponse.class, purchasedGame, authentication)).toList();
                response = (T) new PurchaseResponse_Full(purchase, purchasedTotalPrice, purchasedGames, purchasedDLCs, additionalDiscountPercent);
            } else {
                response = responseType.getDeclaredConstructor(Purchase.class, BigDecimal.class, double.class).newInstance(purchase, purchasedTotalPrice, additionalDiscountPercent);
            } 
            return purchaseAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makePurchaseGameResponse(Class<T> responseType, PurchasedGame purchasedGame, Authentication authentication) {
        try {
            T response;

            EntityModel<GameResponse_Minimal> gameResponseMinimalEntityModel = makeGameResponse(GameResponse_Minimal.class, purchasedGame.getGame(), authentication);
            if (PurchaseGameResponse.class.equals(responseType)) {
                double totalDiscount = purchasedGame.getGame().getDiscounts().stream().mapToDouble(Discount::getDiscountPercent).sum();
                totalDiscount = Math.max(0, Math.min(totalDiscount, 100));
                BigDecimal purchasedPrice = purchasedGame.getGameBasePriceAtTheTime().multiply(BigDecimal.valueOf(totalDiscount));
                response = (T) new PurchaseGameResponse(purchasedGame, gameResponseMinimalEntityModel, totalDiscount, purchasedPrice);
            } else {
                response = responseType.getDeclaredConstructor(PurchasedGame.class, EntityModel.class).newInstance(purchasedGame, gameResponseMinimalEntityModel);
            } 
            return purchaseGameAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makePurchaseDlcResponse(Class<T> responseType, PurchasedDLC purchasedDLC, Authentication authentication) {
        try {
            T response;

            EntityModel<DlcResponse_Basic> gameResponseMinimalEntityModel = makeDlcResponse(DlcResponse_Basic.class, purchasedDLC.getDlc(), authentication);
            if (PurchaseDlcResponse.class.equals(responseType)) {
                double totalDiscount = purchasedDLC.getDlc().getDiscounts().stream().mapToDouble(Discount::getDiscountPercent).sum();
                totalDiscount = Math.max(0, Math.min(totalDiscount, 100));
                BigDecimal purchasedPrice = purchasedDLC.getPriceAtTheTime().multiply(BigDecimal.valueOf(totalDiscount));
                response = (T) new PurchaseDlcResponse(purchasedDLC, gameResponseMinimalEntityModel, totalDiscount, purchasedPrice);
            } else {
                response = null;
//                response = responseType.getDeclaredConstructor(PurchasedDLC.class, EntityModel.class).newInstance(purchasedDLC, gameResponseMinimalEntityModel);
            } 
            return purchaseDlcAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }
}
