package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.controller.*;
import com.example.steamreplica.dtos.response.HomeResponse;
import com.example.steamreplica.model.auth.AuthUserDetail;
import com.example.steamreplica.util.StaticHelper;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestControllerAdvice
public class HomeAssembler {
    public EntityModel<HomeResponse> toModel(HomeResponse entity, Authentication authentication) {
        AuthUserDetail authUserDetail = StaticHelper.extractAuthUserDetail(authentication).orElse(null);
        EntityModel<HomeResponse> entityModel = EntityModel.of(entity, 
                linkTo(methodOn(HomeController.class).initialGet(authentication)).withSelfRel().withType(HttpMethod.GET.name()), 
                linkTo(methodOn(GameController.class).getNewAndTrendingGames(0, authentication)).withRel("Get new and trending games").withType(HttpMethod.GET.name()), 
                linkTo(methodOn(GameController.class).getTopSellerGames(0, authentication)).withRel("Get top seller games").withType(HttpMethod.GET.name()), 
                linkTo(methodOn(GameController.class).getSpecialGames(0, authentication)).withRel("Get special games").withType(HttpMethod.GET.name()), 
                linkTo(methodOn(CategoryController.class).getAllCategories(authentication)).withRel("Get all categories").withType(HttpMethod.GET.name())
        );

        if (authUserDetail == null || !authentication.isAuthenticated()) {
            entityModel.add(linkTo(methodOn(AuthController.class).login(null, null)).withRel("Login").withType(HttpMethod.POST.name()));
            entityModel.add(linkTo(methodOn(AuthController.class).register(null, null)).withRel("Register").withType(HttpMethod.POST.name()));
        } else {
            entityModel.add(linkTo(methodOn(UserController.class).getUserById(authUserDetail.getId(), authentication)).withRel("Get user detail").withType(HttpMethod.GET.name()));
        } 

        return entityModel;
    }
}
