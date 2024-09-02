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
import org.springframework.data.repository.ListCrudRepository;
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
        return cacheHelper.getCache(CATEGORY_CACHE, CategoryResponse_Full.class, id, categoryRepository, repo -> {
            Category category = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
            return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, category, authentication);
        }, 30);
    }

    @Transactional
    public Category getCategoryById_entity(long id, Authentication authentication) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }

    @Transactional
    public Category getCategoryById_entityFull(long id) {
        return categoryRepository.findById_full(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }

    public CollectionModel<EntityModel<CategoryResponse_Minimal>> getAllCategories(Authentication authentication) {
        return cacheHelper.getListCache(CATEGORY_LIST_CACHE, CategoryResponse_Minimal.class, categoryRepository, categoryRepository1 -> {
            List<Category> categories = categoryRepository1.findAll();
            return serviceHelper.makeCategoryResponse_CollectionModel(CategoryResponse_Minimal.class, categories, authentication);
        });
    }

    @Transactional
    public EntityModel<CategoryResponse_Full> createCategory(CategoryRequest categoryRequest, Authentication authentication) {
        Category newCategory = categoryRequest.toModel();
        return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, newCategory, authentication);
    }

    @Transactional
    public EntityModel<CategoryResponse_Full> updateCategory(long id, CategoryRequest categoryRequest, Authentication authentication) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        categoryToUpdate.setCategoryName(categoryRequest.getCategoryName());
        categoryToUpdate.setCategoryDescription(categoryRequest.getCategoryDescription());
        Category updateCategory = categoryRepository.save(categoryToUpdate);

        EntityModel<CategoryResponse_Full> categoryResponseFullEntityModel = serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, updateCategory, authentication);
        cacheHelper.updateCache(categoryResponseFullEntityModel, CATEGORY_CACHE);
        cacheHelper.deleteListCaches(CATEGORY_LIST_CACHE);
        return categoryResponseFullEntityModel;
    }

    @Transactional
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        cacheHelper.deleteCaches(CATEGORY_CACHE, category.getId(), CATEGORY_LIST_CACHE);
        categoryRepository.deleteById(id);
    }
}
