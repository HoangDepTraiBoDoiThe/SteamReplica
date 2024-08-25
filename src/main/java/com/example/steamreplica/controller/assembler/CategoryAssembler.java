package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.constants.HttpRequestTypes;
import com.example.steamreplica.constants.SystemRole;
import com.example.steamreplica.controller.CategoryController;
import com.example.steamreplica.dtos.response.BaseResponse;
import com.example.steamreplica.dtos.response.CategoryResponse_Full;
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
public class CategoryAssembler {

    public <T extends BaseResponse> EntityModel<T> toModel(T entity, Authentication authentication) {
        Collection<String> roles = StaticHelper.extractGrantedAuthority(authentication);
        EntityModel<T> entityModel = EntityModel.of(entity,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CategoryController.class).getCategoryById(entity.getId(), authentication)).withSelfRel().withType(HttpRequestTypes.GET.name()),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CategoryController.class).getAllCategories(authentication)).withRel("Get all categories").withType(HttpRequestTypes.GET.name())
        );

        if (roles.contains(SystemRole.ADMIN.name())) {
            entityModel.add(
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CategoryController.class).updateCategory(entity.getId(), null, authentication, null)).withRel("Update category").withType(HttpRequestTypes.PUT.name()),
                    WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CategoryController.class).deleteCategoryById(entity.getId())).withRel("Delete category").withType(HttpRequestTypes.DELETE.name())
                    );
        }
        return entityModel;
    }

    public <T extends BaseResponse> CollectionModel<EntityModel<T>> toCollectionModel(Iterable<T> entities, Authentication authentication) {
        return StreamSupport.stream(entities.spliterator(), false) //
                .map(t -> toModel(t, authentication)) //
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }
    
}
