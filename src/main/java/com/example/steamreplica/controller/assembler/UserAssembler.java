package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.controller.UserController;
import com.example.steamreplica.dtos.response.user.UserResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Component
public class UserAssembler {
    public <T extends UserResponse> EntityModel<T> toModel(T entity) {
        return EntityModel.of(entity, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserById(entity.getId())).withSelfRel().withType(HttpRequestTypes.GET.name()));
    }

    public <T extends UserResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities) {

        return StreamSupport.stream(entities.spliterator(), false) //
                .map(this::toModel) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
