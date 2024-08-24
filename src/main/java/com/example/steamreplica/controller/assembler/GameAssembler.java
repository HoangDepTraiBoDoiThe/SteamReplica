package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.GameController;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class GameAssembler {
    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        
        EntityModel<T> gameResponseEntityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getGame(entity.getId(), authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).getNewAndTrendingGames(1, authentication)).withRel("Get all Games").withType(HttpRequestTypes.GET.name())
        );

        if (roles.contains(SystemRole.ADMIN.name()) || roles.contains(SystemRole.GAME_DEVELOPER.name()) || roles.contains(SystemRole.PUBLISHER.name())) {
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).updateGame(null, entity.getId(), authentication)).withRel("Update game info").withType(HttpRequestTypes.PUT.name()));
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).deleteGame(entity.getId())).withRel("Delete game").withType(HttpRequestTypes.DELETE.name()));
            gameResponseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).createNewGame(null, authentication)).withRel("Create new game").withType(HttpRequestTypes.POST.name()));
        }
        return gameResponseEntityModel;
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(game -> toModel(game, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
