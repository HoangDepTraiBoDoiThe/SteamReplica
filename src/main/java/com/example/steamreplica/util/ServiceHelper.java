package com.example.steamreplica.util;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.response.*;
import com.example.steamreplica.dtos.response.game.*;
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
import com.example.steamreplica.model.game.DLC.DLCImage;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.purchasedLibrary.DLC.PurchasedDLC;
import com.example.steamreplica.model.purchasedLibrary.Purchase;
import com.example.steamreplica.model.purchasedLibrary.game.PurchasedGame;
import com.example.steamreplica.model.userApplication.User;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> addLinksToPaginationResponse(CollectionModel<EntityModel<T>> entityModels, int page, IMyHelper helperInterface) {

        entityModels.add(
                linkTo(helperInterface.getPaginationMethodToLink(page)).withSelfRel().withType(HttpMethod.GET.name()),
                linkTo(helperInterface.getPaginationMethodToLink(page + 1)).withRel("Next").withType(HttpMethod.GET.name())
        );
        if (page > 0)
            entityModels.add(linkTo((helperInterface.getPaginationMethodToLink(page - 1))).withRel("Prev").withType(HttpMethod.GET.name()));

        return entityModels;
    }

    public BaseResponse makeBaseResponse(long id, String message) {
        return new BaseResponse(id, message);
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> makeGameResponse_CollectionModel(List<T> gameResponse, Authentication authentication) {
        return gameAssembler.toCollectionModel(gameResponse, authentication);
    }
    
    public <T extends BaseResponse> List<T> makeGameResponses(Class<T> responseType, List<Game> games, Authentication authentication) {
        return games.stream().map(game -> makeGameResponse(responseType, game, authentication)).toList();
    }

    public <T extends BaseResponse> EntityModel<T> makeGameResponse_EntityModel(T game, Authentication authentication) {
        return gameAssembler.toModel(game, authentication);
    }

    public <T extends BaseResponse> T makeGameResponse(Class<T> responseType, Game game, Authentication authentication) {
        try {
            T response;

            List<DiscountResponse_Minimal> discountResponsesMinimal = game.getDiscounts().stream().map(DiscountResponse_Minimal::new).toList();

            List<EntityModel<CategoryResponse_Minimal>> categoryEntityModelList = game.getCategories().stream().map(category -> makeCategoryResponse_EntityModel(makeCategoryResponse(CategoryResponse_Minimal.class, category), authentication)).toList();
            if (GameResponse_Full.class.equals(responseType)) {
                List<EntityModel<UserResponse_Minimal>> usersAsPublisherResponses = game.getPublisherOwners().stream().map(library -> makeUserResponse(UserResponse_Minimal.class, library.getUser(), authentication, "")).toList();
                List<EntityModel<UserResponse_Minimal>> usersAsDevResponses = game.getDevOwners().stream().map(library -> makeUserResponse(UserResponse_Minimal.class, library.getUser(), authentication, "")).toList();
                List<EntityModel<ImageResponse>> gameImageResponses = game.getGameImages().stream().map(gameImage -> makeGameImageResponse(ImageResponse.class, gameImage, authentication)).toList();
                
                response = (T) new GameResponse_Full(game, usersAsPublisherResponses, usersAsDevResponses, discountResponsesMinimal, categoryEntityModelList, gameImageResponses);
            } else if (GameResponse_Basic.class.equals(responseType)) {
                response = (T) new GameResponse_Basic(game, discountResponsesMinimal, categoryEntityModelList);
            } else {
                response = responseType.getDeclaredConstructor(Long.class).newInstance(game.getId());
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeGameImageResponse(Class<T> responseType, GameImage gameImage, Authentication authentication) {
        try {
            T response;
            if (GameImageResponse.class.equals(responseType)) {
                response = (T) new GameImageResponse(gameImage, makeGameResponse_EntityModel(makeGameResponse(GameResponse_Minimal.class, gameImage.getGame(), authentication), authentication));
            } else if (ImageResponse.class.equals(responseType)) {
                response = (T) new ImageResponse(gameImage.getId(), gameImage.getImageName(), gameImage.getImage());
            } else response = (T) new BaseResponse(gameImage.getId());

            return gameImageAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    } 

    public <T extends BaseResponse> EntityModel<T> makeDlcImageResponse(Class<T> responseType, DLCImage dlcImage, Authentication authentication) {
        try {
            T response;
            if (DlcImageResponse.class.equals(responseType)) {
                response = (T) new DlcImageResponse(dlcImage, makeDlcResponse_EntityModel(makeDlcResponse(DlcResponse_Basic.class, dlcImage.getDlc(), authentication), authentication));
            } else {
                response = responseType.getDeclaredConstructor(Long.class, String.class, Blob.class).newInstance(dlcImage.getId(), dlcImage.getImageName(), dlcImage.getImage());
            }

            return gameImageAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeCategoryResponse_EntityModel(T categoryResponse, Authentication authentication) {
        return categoryAssembler.toModel(categoryResponse, authentication);
    }
    public <T extends BaseResponse> List<T> makeCategoryResponses(Class<T> responseType, List<Category> categories) {
        return categories.stream().map(category -> makeCategoryResponse(responseType, category)).toList();
    }
    public <T extends BaseResponse> CollectionModel<EntityModel<T>> makeCategoryResponse_CollectionModel(List<T> categorieResponses, Authentication authentication) {
        return categoryAssembler.toCollectionModel(categorieResponses, authentication);
    }
    public <T extends BaseResponse> T makeCategoryResponse(Class<T> responseType, Category category) {
        try {
            T response;

            if (CategoryResponse_Full.class.equals(responseType)) {
                response = (T) new CategoryResponse_Full(category);
            } else {
                response = responseType.getDeclaredConstructor(Category.class).newInstance(category);
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makeUserResponse(Class<T> responseType, User user, Authentication authentication, String message) {
        try {
            T response;

            if (UserResponse_Full.class.equals(responseType)) {
                response = (T) new UserResponse_Full(user);
            } else {
                response = responseType.getDeclaredConstructor(User.class).newInstance(user);
            }
            response.setMessage(message);
            return userAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> makeDlcResponse_CollectionModel(List<T> dlcResponses, Authentication authentication) {
        return dlcAssembler.toCollectionModel(dlcResponses, authentication);
    } 
    public <T extends BaseResponse> List<T> makeDlcResponses(Class<T> responseType, List<DLC> dlcs, Authentication authentication) {
        return dlcs.stream().map(dlc -> makeDlcResponse(responseType, dlc, authentication)).toList();
    } 
    public <T extends BaseResponse> T makeDlcResponse(Class<T> responseType, DLC dlc, Authentication authentication) {
        try {
            T response;
            if (DlcResponse_Full.class.equals(responseType)) {
                List<EntityModel<DiscountResponse_Minimal>> discountResponseMinimal = dlc.getDiscounts().stream().map(discount -> makeDiscountResponse_EntityModel(makeDiscountResponse(DiscountResponse_Minimal.class, discount), authentication)).toList();
                EntityModel<GameResponse_Minimal> gameResponse_minimal = makeGameResponse_EntityModel(makeGameResponse(GameResponse_Minimal.class, dlc.getGame(), authentication), authentication);
                List<EntityModel<ImageResponse>> gameImageResponses = dlc.getDlcImages().stream().map(dlcImage -> makeDlcImageResponse(ImageResponse.class, dlcImage, authentication)).toList();
                
                response = (T) new DlcResponse_Full(dlc, discountResponseMinimal, gameImageResponses, gameResponse_minimal);
            } else {
                response = responseType.getDeclaredConstructor(DLC.class).newInstance(dlc);
            } 
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }
    public <T extends BaseResponse> EntityModel<T> makeDlcResponse_EntityModel(T dlc, Authentication authentication) {
        return dlcAssembler.toModel(dlc, authentication);
    }
    public <T extends BaseResponse> List<T> makeDiscountResponses(Class<T> responseType, List<Discount> discounts) {
        return discounts.stream().map(t -> makeDiscountResponse(responseType, t)).toList();
    } 
    public <T extends BaseResponse> T makeDiscountResponse(Class<T> responseType, Discount discount) {
        T response;

        try {
            if (DiscountResponse_Full.class.equals(responseType)) {
                response = (T) new DiscountResponse_Full(discount);
            } else {
                response = responseType.getDeclaredConstructor(Discount.class).newInstance(discount);
            }
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }
    public <T extends BaseResponse> CollectionModel<EntityModel<T>> makeDiscountResponse_CollectionModel(List<T> discountResponse, Authentication authentication) {
        return discountAssembler.toCollectionModel(discountResponse, authentication);
    } 
    public <T extends BaseResponse> EntityModel<T> makeDiscountResponse_EntityModel(T discountResponse, Authentication authentication) {
        return discountAssembler.toModel(discountResponse, authentication);
    }

    public <T extends BaseResponse> List<T> makePurchaseResponses(Class<T> responseType, List<Purchase> purchases) {
        return purchases.stream().map(purchase -> makePurchaseResponse(responseType, purchase)).toList();
    }
    public <T extends BaseResponse> CollectionModel<EntityModel<T>> makePurchaseResponse_CollectionModel(List<T> purchasesResponses, Authentication authentication) {
        return purchaseAssembler.toCollectionModel(purchasesResponses, authentication);
    }

    public <T extends BaseResponse> EntityModel<T> makePurchaseResponse_EntityModel(T purchaseResponses, Authentication authentication) {
        return purchaseAssembler.toModel(purchaseResponses, authentication);
    } 
    public <T extends BaseResponse> T makePurchaseResponse(Class<T> responseType, Purchase purchase) {
        try {
            T response;
            BigDecimal totalDlcPrice = purchase.getPurchasedDLCs().stream().map(purchasedDLC -> purchasedDLC.getPriceAtTheTime().multiply(BigDecimal.valueOf(100 - purchasedDLC.getDiscountPercent()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalGamePrice = purchase.getPurchasedGames().stream().map(purchasedGame -> purchasedGame.getGameBasePriceAtTheTime().multiply(BigDecimal.valueOf(purchasedGame.getDiscountPercent()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            double additionalDiscountPercent = purchase.getAdditionalDiscount().getDiscountPercent();

            BigDecimal purchasedTotalPrice = totalDlcPrice.add(totalGamePrice).multiply(BigDecimal.valueOf(100 - additionalDiscountPercent));
            if (PurchaseResponse_Full.class.equals(responseType)) {
                List<EntityModel<PurchaseDlcResponse>> purchasedDLCs = purchase.getPurchasedDLCs().stream().map(purchasedDLC -> makePurchaseDlcResponse(PurchaseDlcResponse.class, purchasedDLC, null)).toList();
                List<EntityModel<PurchaseGameResponse>> purchasedGames = purchase.getPurchasedGames().stream().map(purchasedGame -> makePurchaseGameResponse(PurchaseGameResponse.class, purchasedGame, null)).toList();
                EntityModel<UserResponse_Minimal> buyer = makeUserResponse(UserResponse_Minimal.class, purchase.getBoughtLibrary().getUser(), null, "");
                response = (T) new PurchaseResponse_Full(purchase, purchasedTotalPrice, buyer, purchasedGames, purchasedDLCs, additionalDiscountPercent);
            } else {
                response = responseType.getDeclaredConstructor(Purchase.class, BigDecimal.class, double.class).newInstance(purchase, purchasedTotalPrice, additionalDiscountPercent);
            } 
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

    public <T extends BaseResponse> EntityModel<T> makePurchaseGameResponse(Class<T> responseType, PurchasedGame purchasedGame, Authentication authentication) {
        try {
            T response;

            EntityModel<GameResponse_Minimal> gameResponseMinimalEntityModel = makeGameResponse_EntityModel(makeGameResponse(GameResponse_Minimal.class, purchasedGame.getGame(), authentication), authentication);
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

            EntityModel<DlcResponse_Basic> gameResponseMinimalEntityModel = makeGameResponse_EntityModel(makeDlcResponse(DlcResponse_Basic.class, purchasedDLC.getDlc(), authentication), authentication);
            if (PurchaseDlcResponse.class.equals(responseType)) {
                double totalDiscount = purchasedDLC.getDlc().getDiscounts().stream().mapToDouble(Discount::getDiscountPercent).sum();
                totalDiscount = Math.max(0, Math.min(totalDiscount, 100));
                BigDecimal purchasedPrice = purchasedDLC.getPriceAtTheTime().multiply(BigDecimal.valueOf(totalDiscount));
                response = (T) new PurchaseDlcResponse(purchasedDLC, gameResponseMinimalEntityModel, totalDiscount, purchasedPrice);
            } else {
                response = responseType.getDeclaredConstructor(Long.class).newInstance(purchasedDLC.getId());
            } 
            return purchaseDlcAssembler.toModel(response, authentication);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }

}
