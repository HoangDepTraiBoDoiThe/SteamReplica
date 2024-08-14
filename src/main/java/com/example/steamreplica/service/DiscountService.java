package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.DiscountAssembler;
import com.example.steamreplica.dtos.request.DiscountRequest;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountAssembler discountAssembler;

    public EntityModel<DiscountResponse> getDiscountById(long id, Authentication authentication) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
        DiscountResponse discountResponse = new DiscountResponse(discount);
        return discountAssembler.toModel(discountResponse, authentication);
    }
    
    public EntityModel<DiscountResponse> getDiscountByCode(String code, Authentication authentication) {
        Discount discount = discountRepository.findDiscountByDiscountCode(code).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with code [%s]", code)));
        DiscountResponse discountResponse = new DiscountResponse(discount);
        return discountAssembler.toModel(discountResponse, authentication);
    }
    
    public CollectionModel<EntityModel<DiscountResponse>> getAllDiscounts(Authentication authentication) {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountResponse> discountResponses = discounts.stream().map(DiscountResponse::new).toList();
        return discountAssembler.toCollectionModel(discountResponses, authentication);
    }
    
    public EntityModel<DiscountResponse> addDiscount(DiscountRequest discountRequest, Authentication authentication) {
        Discount newDiscount = new Discount(discountRequest.getDiscountName(), discountRequest.getDiscountCode(), discountRequest.getDiscountDescription(), discountRequest.getDiscountPercent());
        Discount newCreatedDiscount = discountRepository.save(newDiscount);
        DiscountResponse discountResponse = new DiscountResponse(newCreatedDiscount);
        return discountAssembler.toModel(discountResponse, authentication);
    }
    
    public EntityModel<DiscountResponse> updateDiscount(long id, DiscountRequest discountRequest, Authentication authentication) {
        Discount discountToUpdate = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discountToUpdate.setDiscountName(discountRequest.getDiscountName());
        discountToUpdate.setDiscountCode(discountRequest.getDiscountCode());
        discountToUpdate.setDiscountDescription(discountRequest.getDiscountDescription());
        discountToUpdate.setDiscountPercent(discountRequest.getDiscountPercent());
        Discount updatedDiscount = discountRepository.save(discountToUpdate);
        DiscountResponse discountResponse = new DiscountResponse(updatedDiscount);
        return discountAssembler.toModel(discountResponse, authentication);
    }
    
    public void deleteDiscount(long id) {
        discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discountRepository.deleteById(id);
    }
}
