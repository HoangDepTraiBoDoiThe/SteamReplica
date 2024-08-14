package com.example.steamreplica.controller.assembler;

import com.example.steamreplica.controller.PurchaseTransactionController;
import com.example.steamreplica.dtos.response.PurchaseTransactionResponse;
import com.example.steamreplica.model.purchasedLibrary.PurchaseTransaction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class PurchaseTransactionAssembler implements RepresentationModelAssembler<PurchaseTransaction, EntityModel<PurchaseTransactionResponse>> {
    @Override
    public EntityModel<PurchaseTransactionResponse> toModel(PurchaseTransaction entity) {
        EntityModel<PurchaseTransactionResponse> entityModel = EntityModel.of(new PurchaseTransactionResponse(entity),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseTransactionController.class).getTransactionById(entity.getId())).withSelfRel().withType("GET"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PurchaseTransactionController.class).getAllTransactions()).withRel("Get all transaction").withType("GET")
        );

        return entityModel;
    }
}
