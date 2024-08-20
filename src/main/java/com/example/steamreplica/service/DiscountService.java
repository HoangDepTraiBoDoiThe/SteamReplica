package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.DiscountAssembler;
import com.example.steamreplica.dtos.request.DiscountRequest;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
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

    public EntityModel<DiscountResponse_Full> getDiscountById(long id, Authentication authentication) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
        DiscountResponse_Full discountResponseFull = new DiscountResponse_Full(discount);
        return discountAssembler.toModel(discountResponseFull, authentication);
    }

    public Discount getDiscountById_entity(long id, Authentication authentication) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
        DiscountResponse_Full discountResponseFull = new DiscountResponse_Full(discount);
        return discount;
    }
    
    public EntityModel<DiscountResponse_Full> getDiscountByCode(String code, Authentication authentication) {
        Discount discount = discountRepository.findDiscountByDiscountCode(code).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with code [%s]", code)));
        DiscountResponse_Full discountResponseFull = new DiscountResponse_Full(discount);
        return discountAssembler.toModel(discountResponseFull, authentication);
    }
    
    public CollectionModel<EntityModel<DiscountResponse_Full>> getAllDiscounts(Authentication authentication) {
        List<Discount> discounts = discountRepository.findAll();
        List<DiscountResponse_Full> discountResponsFulls = discounts.stream().map(DiscountResponse_Full::new).toList();
        return discountAssembler.toCollectionModel(discountResponsFulls, authentication);
    }
    
    public EntityModel<DiscountResponse_Full> addDiscount(DiscountRequest discountRequest, Authentication authentication) {
        Discount newDiscount = new Discount(discountRequest.getDiscountName(), discountRequest.getDiscountCode(), discountRequest.getDiscountDescription(), discountRequest.getDiscountPercent());
        Discount newCreatedDiscount = discountRepository.save(newDiscount);
        DiscountResponse_Full discountResponseFull = new DiscountResponse_Full(newCreatedDiscount);
        return discountAssembler.toModel(discountResponseFull, authentication);
    }
    
    public EntityModel<DiscountResponse_Full> updateDiscount(long id, DiscountRequest discountRequest, Authentication authentication) {
        Discount discountToUpdate = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discountToUpdate.setDiscountName(discountRequest.getDiscountName());
        discountToUpdate.setDiscountCode(discountRequest.getDiscountCode());
        discountToUpdate.setDiscountDescription(discountRequest.getDiscountDescription());
        discountToUpdate.setDiscountPercent(discountRequest.getDiscountPercent());
        Discount updatedDiscount = discountRepository.save(discountToUpdate);
        DiscountResponse_Full discountResponseFull = new DiscountResponse_Full(updatedDiscount);
        return discountAssembler.toModel(discountResponseFull, authentication);
    }
    
    public void deleteDiscount(long id) {
        discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discountRepository.deleteById(id);
    }
}
