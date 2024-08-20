package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.response.*;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.Game;
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

    public <T extends BaseResponse> EntityModel<T> makeGameResponse(Class<T> responseType, Game game, Authentication authentication) {
        try {
            T response;

            List<DiscountResponse> discountResponses = game.getDiscounts().stream().map(DiscountResponse::new).toList();
            CollectionModel<?> discountCollectionModel = discountAssembler.toCollectionModel(discountResponses, authentication);

            List<EntityModel<CategoryResponse_Minimal>> categoryEntityModelList = game.getCategories().stream().map(category -> makeCategoryResponse(CategoryResponse_Minimal.class, category, authentication)).toList();
            if (GameResponse_Full.class.equals(responseType)) {
                List<UserResponse> usersAsPublisherResponses = game.getPublishers().stream().map(UserResponse::new).toList();
                CollectionModel<?> publisherEntityModel = userAssembler.toCollectionModel(usersAsPublisherResponses, authentication);

                List<UserResponse> usersAsDevResponses = game.getPublishers().stream().map(UserResponse::new).toList();
                CollectionModel<?> DeveloperEntityModel = userAssembler.toCollectionModel(usersAsDevResponses, authentication);

                List<GameImageResponse> gameImageResponses = game.getGameImages().stream().map(gameImage -> new GameImageResponse(game.getId(), gameImage)).toList();
                CollectionModel<?> gameImageCollectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);

                response = (T) new GameResponse_Full(game, publisherEntityModel, DeveloperEntityModel, discountCollectionModel, categoryEntityModelList, gameImageCollectionModel);
            } else if (GameResponse_Basic.class.equals(responseType)) {
                response = (T) new GameResponse_Basic(game, discountCollectionModel, categoryCollectionModel);
            } else {
                response = responseType.getDeclaredConstructor(Long.class).newInstance(game.getId());
            }

            return EntityModel.of(response);
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

}
