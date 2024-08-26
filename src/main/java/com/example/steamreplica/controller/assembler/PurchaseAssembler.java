package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.controller.PurchaseController;
import com.example.steamreplica.dtos.response.BaseResponse;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class PurchaseAssembler {
    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        EntityModel<T> entityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseController.class).getTransactionById(entity.getId(), authentication)).withSelfRel().withType(HttpMethod.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseController.class).getAllTransactions(entity.getId(), authentication)).withRel("Get all transactions of user").withType(HttpMethod.GET.name())
        );

        return entityModel;
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
