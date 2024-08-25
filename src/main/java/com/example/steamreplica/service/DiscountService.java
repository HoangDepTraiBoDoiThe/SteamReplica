package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.DiscountRequest;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Full;
import com.example.steamreplica.dtos.response.game.discount.DiscountResponse_Minimal;
import com.example.steamreplica.event.GameUpdateEvent;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.model.game.discount.Discount;
import com.example.steamreplica.repository.DiscountRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    private final String DISCOUNT_LIST_CACHE = "discountListCache";
    private final String DISCOUNT_CACHE = "discountCache";
    private final String DISCOUNT_PAGINATION_CACHE_PREFIX = "discountPaginationCache";
    private final Integer PAGE_RANGE = 10;
    private final Integer PAGE_SIZE = 10;
    
    public EntityModel<DiscountResponse_Full> getDiscountById(long id, Authentication authentication) {
        Discount discount = cacheHelper.getCache(DISCOUNT_CACHE, id, discountRepository, repo -> repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id))));
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, discount, authentication);
    }

    public Discount getDiscountById_entity(long id, Authentication authentication) {
        return discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
    }

    public EntityModel<DiscountResponse_Full> getDiscountByCode(String code, Authentication authentication) {
        Discount discount = cacheHelper.getCache(DISCOUNT_CACHE, code, discountRepository, repo -> repo.findDiscountByDiscountCode(code).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with code [%s]", code))));
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, discount, authentication);
    }

    public List<EntityModel<DiscountResponse_Minimal>> getAllDiscounts(Authentication authentication) {
        List<Discount> discounts = cacheHelper.getListCache(DISCOUNT_LIST_CACHE, discountRepository, discountRepository -> discountRepository.findAll().stream().toList());
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

        cacheHelper.updateCache(updatedDiscount, DISCOUNT_CACHE, DISCOUNT_LIST_CACHE);
        return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, updatedDiscount, authentication);
    }

    @Transactional
    public void deleteDiscount(long id) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
        cacheHelper.deleteCaches(DISCOUNT_CACHE, discount.getId(), DISCOUNT_LIST_CACHE);
        cacheHelper.deleteCaches(DISCOUNT_CACHE, discount.getDiscountCode(), "");
        discountRepository.deleteById(id);
    }
}
