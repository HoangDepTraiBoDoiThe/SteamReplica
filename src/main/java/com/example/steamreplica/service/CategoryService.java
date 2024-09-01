package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.CategoryRequest;
import com.example.steamreplica.dtos.response.CategoryResponse_Full;
import com.example.steamreplica.dtos.response.CategoryResponse_Minimal;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    private final String CATEGORY_LIST_CACHE = "categoryListCache";
    private final String CATEGORY_CACHE = "categoryCache";
    
    public EntityModel<CategoryResponse_Full> getCategoryById(long id, Authentication authentication) {
        CategoryResponse_Full responseFull = cacheHelper.getCache(CATEGORY_CACHE, CategoryResponse_Full.class, id, categoryRepository, repo -> {
            Category category = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
            return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, category);
        }, 15);
        return serviceHelper.makeCategoryResponse_EntityModel(responseFull, authentication);
    }

    @Transactional
    public Category getCategoryById_entity(long id, boolean bThrowIfNotFound, Authentication authentication) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        if (category == null && bThrowIfNotFound)
            throw new ResourceNotFoundException(String.format("Discount not found with id [%s]", id));
        return category;
    }

    @Transactional
    public Category getCategoryById_entityFull(long id) {
        return categoryRepository.findById_full(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }
    
    public CollectionModel<EntityModel<CategoryResponse_Minimal>> getAllCategories(Authentication authentication) {
        List<CategoryResponse_Minimal> categoryResponseMinimals  = cacheHelper.getListCache(CATEGORY_LIST_CACHE, categoryRepository, repo -> {
            List<Category> categories = repo.findAll();
            return serviceHelper.makeCategoryResponses(CategoryResponse_Minimal.class, categories);
        });

        return serviceHelper.makeCategoryResponse_CollectionModel(categoryResponseMinimals, authentication);
    }

    @Transactional
    public EntityModel<CategoryResponse_Full> createCategory(CategoryRequest categoryRequest, Authentication authentication) {
        Category newCategory = categoryRequest.toModel();
        CategoryResponse_Full responseFull = serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, newCategory);
        return serviceHelper.makeCategoryResponse_EntityModel(responseFull, authentication);
    }

    @Transactional
    public EntityModel<CategoryResponse_Full> updateCategory(long id, CategoryRequest categoryRequest, Authentication authentication) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        categoryToUpdate.setCategoryName(categoryRequest.getCategoryName());
        categoryToUpdate.setCategoryDescription(categoryRequest.getCategoryDescription());
        Category updateCategory = categoryRepository.save(categoryToUpdate);
        CategoryResponse_Full responseFull = serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, updateCategory);

        cacheHelper.updateCache(responseFull, CATEGORY_CACHE, CATEGORY_LIST_CACHE);
        return serviceHelper.makeCategoryResponse_EntityModel(responseFull, authentication);
    }

    @Transactional
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        cacheHelper.deleteCaches(CATEGORY_CACHE, category.getId(), CATEGORY_LIST_CACHE);
        categoryRepository.deleteById(id);
    }
}
