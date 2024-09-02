package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.DiscountController;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Minimal;
import com.example.steamreplica.model.game.discount.Discount;
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
public class DiscountAssembler {
    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        
        EntityModel<T> responseEntityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).getAllDiscounts(authentication)).withRel("Get all discounts").withType(HttpMethod.GET.name())
                );

        if (roles.contains(SystemRole.ADMIN.name()) || roles.contains(SystemRole.PUBLISHER.name()) || roles.contains(SystemRole.GAME_DEVELOPER.name())) {
            responseEntityModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).addDiscount(null, authentication, null)).withRel("Create discount").withType(HttpMethod.POST.name()),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).updateDiscount(entity.getId(), null, authentication, null)).withRel("Update discount").withType(HttpMethod.PUT.name()),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(DiscountController.class).deleteDiscount(entity.getId())).withRel("Delete discount").withType(HttpMethod.DELETE.name()));
        }
        
        return responseEntityModel;
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {

        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
}
