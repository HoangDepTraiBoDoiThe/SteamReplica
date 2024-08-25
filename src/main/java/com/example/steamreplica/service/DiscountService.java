package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.DiscountRequest;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Minimal;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    public EntityModel<DiscountResponse_Full> getDiscountById(long id, Authentication authentication) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, discount, authentication);
    }

    public Discount getDiscountById_entity(long id, Authentication authentication) {
        return discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
    }

    public EntityModel<DiscountResponse_Full> getDiscountByCode(String code, Authentication authentication) {
        Discount discount = discountRepository.findDiscountByDiscountCode(code).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with code [%s]", code)));
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, discount, authentication);
    }

    public List<EntityModel<DiscountResponse_Minimal>> getAllDiscounts(Authentication authentication) {
        List<Discount> discounts = discountRepository.findAll();
        return discounts.stream().map(discount -> serviceHelper.makeDiscountResponse(DiscountResponse_Minimal.class, discount, authentication)).toList();
    }

    @Transactional
    public EntityModel<DiscountResponse_Full> addDiscount(DiscountRequest discountRequest, Authentication authentication) {
        Discount newDiscount = new Discount(discountRequest.getDiscountName(), discountRequest.getDiscountCode(), discountRequest.getDiscountDescription(), discountRequest.getDiscountPercent());
        Discount newCreatedDiscount = discountRepository.save(newDiscount);
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, newCreatedDiscount, authentication);
    }

    @Transactional
    public EntityModel<DiscountResponse_Full> updateDiscount(long id, DiscountRequest discountRequest, Authentication authentication) {
        Discount discountToUpdate = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        discountToUpdate.setDiscountName(discountRequest.getDiscountName());
        discountToUpdate.setDiscountCode(discountRequest.getDiscountCode());
        discountToUpdate.setDiscountDescription(discountRequest.getDiscountDescription());
        discountToUpdate.setDiscountPercent(discountRequest.getDiscountPercent());
        Discount updatedDiscount = discountRepository.save(discountToUpdate);

        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, updatedDiscount, authentication);
    }

    @Transactional
    public void deleteDiscount(long id) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));

        discountRepository.deleteById(id);
    }
}
