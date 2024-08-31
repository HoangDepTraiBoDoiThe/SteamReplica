package com.example.steamreplica.controller.assembler;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.GameController;
import com.example.steamreplica.controller.GameImageController;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.GameImageResponse;
import com.example.steamreplica.util.StaticHelper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GameImageAssembler {
    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);

        EntityModel<T> entityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameImageController.class).getGameImage(entity.getId(), authentication)).withSelfRel().withType(HttpMethod.GET.name())
        );

        if (roles.contains(SystemRole.ADMIN.name())) {
            entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).deleteGame(entity.getId())).withRel("Delete game image").withType(HttpMethod.DELETE.name()));
        }

        if (roles.contains(SystemRole.GAME_DEVELOPER.name()) || roles.contains(SystemRole.PUBLISHER.name())) {
            entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).createNewGame(null, authentication)).withRel("Create new game image").withType(HttpMethod.POST.name()));
            entityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameController.class).updateGame(null, entity.getId(), authentication)).withRel("Update game image").withType(HttpMethod.PUT.name()));
        }

        return entityModel;
    }
    
    public <T extends GameImageResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
