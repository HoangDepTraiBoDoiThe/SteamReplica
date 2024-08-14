package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.controller.GameImageController;
import com.example.steamreplica.dtos.response.GameImageResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GameImageAssembler {

    public <T extends GameImageResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        return EntityModel.of(entity, 
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GameImageController.class).getAllImagesByGameId(entity.getGameId(), authentication)).withRel("Get all images of this game").withType(HttpRequestTypes.GET.name())
        );
    }
    
    public <T extends GameImageResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
    
}
