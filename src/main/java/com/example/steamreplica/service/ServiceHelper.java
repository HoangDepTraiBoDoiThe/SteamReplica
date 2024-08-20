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
import com.example.steamreplica.dtos.response.user.UserResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse_Minimal;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.model.game.GameImage;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.model.userApplication.User;
import lombok.RequiredArgsConstructor;
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
}
