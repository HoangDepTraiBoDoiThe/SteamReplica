package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.GameController;
import com.example.steamreplica.dtos.response.CategoryResponse;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.dtos.response.GameImageResponse;
import com.example.steamreplica.dtos.response.GameResponse;
import com.example.steamreplica.dtos.response.user.UserResponse;
import com.example.steamreplica.model.game.Game;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class GameAssembler {
    private final UserAssembler userAssembler;
    private final DiscountAssembler discountAssembler;
    private final CategoryAssembler categoryAssembler;
    private final GameImageAssembler gameImageAssembler;
    
    public EntityModel<GameResponse> toModel(Game entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);

        List<UserResponse> usersAsPublisherResponses = entity.getPublishers().stream().map(UserResponse::new).toList();
        CollectionModel<?> publisherEntityModel = userAssembler.toCollectionModel(usersAsPublisherResponses, authentication);
        
        List<UserResponse> usersAsDevResponses = entity.getPublishers().stream().map(UserResponse::new).toList();
        CollectionModel<?> DeveloperEntityModel = userAssembler.toCollectionModel(usersAsDevResponses, authentication);
        
        List<DiscountResponse> discountResponses = entity.getDiscounts().stream().map(DiscountResponse::new).toList();
        CollectionModel<?> discountCollectionModel = discountAssembler.toCollectionModel(discountResponses, authentication);
        
        List<CategoryResponse> categoryResponses = entity.getCategories().stream().map(CategoryResponse::new).toList();
        CollectionModel<?> categoryCollectionModel = categoryAssembler.toCollectionModel(categoryResponses, authentication);        
        
        List<GameImageResponse> gameImageResponses = entity.getGameImages().stream().map(gameImage -> new GameImageResponse(entity.getId(), gameImage)).toList();
        CollectionModel<?> gameImageCollectionModel = gameImageAssembler.toCollectionModel(gameImageResponses, authentication);
        
        GameResponse gameResponse = new GameResponse(entity.getId(), entity.getGameName(), entity.getGameDescription(), entity.getReleaseDate(), entity.getGameBasePrice(), publisherEntityModel, DeveloperEntityModel, discountCollectionModel, categoryCollectionModel, gameImageCollectionModel, StaticHelper.convertBlobToString(entity.getGameThumbnail()));
        EntityModel<GameResponse> gameResponseEntityModel = EntityModel.of(gameResponse,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGame(entity.getId(), authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGames(authentication)).withRel("Get all Games").withType(HttpRequestTypes.GET.name())
        );

        if (roles.contains(SystemRole.ADMIN.name()) || roles.contains(SystemRole.GAME_DEVELOPER.name()) || roles.contains(SystemRole.PUBLISHER.name())) {
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).updateGame(null, entity.getId(), authentication)).withRel("Update game info").withType(HttpRequestTypes.PUT.name()));
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).deleteGame(entity.getId())).withRel("Delete game").withType(HttpRequestTypes.DELETE.name()));
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).createNewGame(null, authentication)).withRel("Create new game").withType(HttpRequestTypes.POST.name()));
        }
        return gameResponseEntityModel;
    }

    public CollectionModel<EntityModel<GameResponse>> toCollectionModel(Iterable<Game> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(game -> toModel(game, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
