package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.response.*;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.game.GameResponse_Minimal;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Basic;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Minimal;
import com.example.steamreplica.dtos.response.game.dlc.DlcResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.userApplication.User;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

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

    public <T extends BaseResponse> EntityModel<T> makeGameResponse(Class<T> responseType, Game game, Authentication authentication) {
        try {
            T response;

            List<DiscountResponse_Full> discountResponsFulls = game.getDiscounts().stream().map(discount -> ).toList();

            List<EntityModel<CategoryResponse_Minimal>> categoryEntityModelList = game.getCategories().stream().map(category -> makeCategoryResponse(CategoryResponse_Minimal.class, category, authentication)).toList();
            if (GameResponse_Full.class.equals(responseType)) {
                List<EntityModel<UserResponse_Minimal>> usersAsPublisherResponses = game.getPublishers().stream().map(user -> makeUserResponse(UserResponse_Minimal.class, user, authentication)).toList();

                List<EntityModel<UserResponse_Minimal>> usersAsDevResponses = game.getPublishers().stream().map(user -> makeUserResponse(UserResponse_Minimal.class, user, authentication)).toList();

                List<GameImageResponse> gameImageResponses = game.getGameImages().stream().map(gameImage -> new GameImageResponse(game.getId(), gameImage)).toList();
                CollectionModel<?> gameImageCollectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);

                response = (T) new GameResponse_Full(game, usersAsPublisherResponses, usersAsDevResponses, discountCollectionModel, categoryEntityModelList, gameImageCollectionModel);
            } else if (GameResponse_Basic.class.equals(responseType)) {
                response = (T) new GameResponse_Basic(game, discountCollectionModel, categoryEntityModelList);
            } else {
                response = responseType.getDeclaredConstructor(Long.class).newInstance(game.getId());
            }

            return gameAssembler.toModel(response, authentication);
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
        try {
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error while creating response: ", e);
        }
    }
}
