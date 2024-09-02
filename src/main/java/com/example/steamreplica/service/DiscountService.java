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
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.hateoas.CollectionModel;
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

    private final String DISCOUNT_LIST_CACHE = "discountListCache";
    private final String DISCOUNT_CACHE = "discountCache";
    private final String DISCOUNT_PAGINATION_CACHE_PREFIX = "discountPaginationCache";
    private final String SPECIAL_DISCOUNT_PAGINATION_CACHE_PREFIX = "Special";
    private final Integer PAGE_RANGE = 10;
    private final Integer PAGE_SIZE = 10;

    public EntityModel<DiscountResponse_Full> getDiscountByCode(String code, Authentication authentication) {
        return cacheHelper.getCache(DISCOUNT_CACHE, DiscountResponse_Full.class, code, discountRepository, repo -> {
            Discount discount = repo.findDiscountByDiscountCode(code).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with code [%s]", code)));
            return serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, discount, authentication);
        }, 30);
    }

    public Discount getDiscountById_entity(long id, Authentication authentication) {
        return discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Discount not found with id [%s]", id)));
    }

    public CollectionModel<EntityModel<DiscountResponse_Minimal>> getAllDiscounts(Authentication authentication) {
        return cacheHelper.getListCache(DISCOUNT_LIST_CACHE, DiscountResponse_Minimal.class, discountRepository, discountRepository1 -> {
            List<Discount> discounts = discountRepository1.findAll();
            return serviceHelper.makeDiscountResponse_CollectionModel(DiscountResponse_Minimal.class, discounts, authentication);
        });
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

        EntityModel<DiscountResponse_Full> entityModel = serviceHelper.makeDiscountResponse(DiscountResponse_Full.class, updatedDiscount, authentication);

        cacheHelper.updateCache(entityModel, DISCOUNT_CACHE);
        cacheHelper.deleteListCaches(DISCOUNT_LIST_CACHE);
        return entityModel;
    }

    @Transactional
    public void deleteDiscount(long id) {
        Discount discount = discountRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));

        cacheHelper.deleteListCaches(DISCOUNT_LIST_CACHE);
        cacheHelper.deleteCache(DISCOUNT_CACHE, discount.getId());
        cacheHelper.deleteCache(DISCOUNT_CACHE, discount.getDiscountCode());
        discountRepository.deleteById(id);
    }

    @Transactional
    public Discount getDiscountById_entityFull(long id) {
        return discountRepository.findById_full(id).orElseThrow(() -> new ResourceNotFoundException("Discount not found"));
    }
}
