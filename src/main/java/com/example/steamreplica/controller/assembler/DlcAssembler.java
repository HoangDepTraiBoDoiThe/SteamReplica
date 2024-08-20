package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.controller.DlcController;
import com.example.steamreplica.dtos.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class DlcAssembler{
    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        return EntityModel.of(entity, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DlcController.class).getDlcById(entity.getId(), authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()));
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
