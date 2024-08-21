package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.controller.PurchaseController;
import com.example.steamreplica.dtos.response.PurchaseTransactionResponse;
import com.example.steamreplica.model.purchasedLibrary.Purchases;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class PurchaseAssembler implements RepresentationModelAssembler<Purchases, EntityModel<PurchaseTransactionResponse>> {
    @Override
    public EntityModel<PurchaseTransactionResponse> toModel(Purchases entity) {
        EntityModel<PurchaseTransactionResponse> entityModel = EntityModel.of(new PurchaseTransactionResponse(entity),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseController.class).getTransactionById(entity.getId())).withSelfRel().withType("GET"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseController.class).getAllTransactions()).withRel("Get all transaction").withType("GET")
        );

        return entityModel;
    }
}
