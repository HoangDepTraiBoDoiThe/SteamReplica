package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.DiscountController;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.util.StaticHelper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class DiscountAssembler {
    public <T extends DiscountResponse_Full> EntityModel<T> toModel(T entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        
        EntityModel<T> responseEntityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).getDiscount(entity.getId(), authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).getDiscountByCode(entity.getDiscountCode(), authentication)).withRel("Get discount by code").withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).getAllDiscounts(authentication)).withRel("Get all discounts").withType(HttpRequestTypes.GET.name())
                );

        if (roles.contains(SystemRole.ADMIN.name()) || roles.contains(SystemRole.PUBLISHER.name()) || roles.contains(SystemRole.GAME_DEVELOPER.name())) {
            responseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).addDiscount(null, authentication, null)).withRel("Create discount").withType(HttpRequestTypes.POST.name()),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).updateDiscount(entity.getId(), null, authentication, null)).withRel("Update discount").withType(HttpRequestTypes.PUT.name()),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).deleteDiscount(entity.getId())).withRel("Delete discount").withType(HttpRequestTypes.DELETE.name()));
        }
        
        return responseEntityModel;
    }

    public <T extends DiscountResponse_Full> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {

        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
