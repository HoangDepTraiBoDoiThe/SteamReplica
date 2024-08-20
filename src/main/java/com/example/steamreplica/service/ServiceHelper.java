package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.*;
import com.example.steamreplica.dtos.response.*;
import com.example.steamreplica.dtos.response.game.GameResponse_Basic;
import com.example.steamreplica.dtos.response.game.GameResponse_Full;
import com.example.steamreplica.dtos.response.user.UserResponse;
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

            List<CategoryResponse> categoryResponses = game.getCategories().stream().map(CategoryResponse::new).toList();
            CollectionModel<?> categoryCollectionModel = categoryAssembler.toCollectionModel(categoryResponses, authentication);
            if (GameResponse_Full.class.equals(responseType)) {
                List<UserResponse> usersAsPublisherResponses = game.getPublishers().stream().map(UserResponse::new).toList();
                CollectionModel<?> publisherEntityModel = userAssembler.toCollectionModel(usersAsPublisherResponses, authentication);

                List<UserResponse> usersAsDevResponses = game.getPublishers().stream().map(UserResponse::new).toList();
                CollectionModel<?> DeveloperEntityModel = userAssembler.toCollectionModel(usersAsDevResponses, authentication);

                List<GameImageResponse> gameImageResponses = game.getGameImages().stream().map(gameImage -> new GameImageResponse(game.getId(), gameImage)).toList();
                CollectionModel<?> gameImageCollectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);

                response = (T) new GameResponse_Full(game, publisherEntityModel, DeveloperEntityModel, discountCollectionModel, categoryCollectionModel, gameImageCollectionModel);
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
}
