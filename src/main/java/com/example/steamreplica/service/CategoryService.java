package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.CategoryRequest;
import com.example.steamreplica.dtos.response.CategoryResponse_Full;
import com.example.steamreplica.dtos.response.CategoryResponse_Minimal;
import com.example.steamreplica.event.GameUpdateEvent;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.model.game.DLC.DLC;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import com.example.steamreplica.util.CacheHelper;
import com.example.steamreplica.util.ServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ServiceHelper serviceHelper;
    private final CacheHelper cacheHelper;

    private final String CATEGORY_LIST_CACHE = "categoryListCache";
    private final String CATEGORY_CACHE = "categoryCache";
    
    public EntityModel<CategoryResponse_Full> getCategoryById(long id, Authentication authentication) {
        Category category = cacheHelper.getCache(CATEGORY_CACHE, id, categoryRepository, repo -> repo.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id))));
        return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, category, authentication);
    }

    @Transactional
    public Category getCategoryById_entity(long id, Authentication authentication) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }

    @Transactional
    public Category getCategoryById_entityFull(long id) {
        return categoryRepository.findById_full(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }
    
    public List<EntityModel<CategoryResponse_Minimal>> getAllCategories(Authentication authentication) {
        List<Category> categories = cacheHelper.getListCache(CATEGORY_LIST_CACHE, categoryRepository, ListCrudRepository::findAll);
        return categories.stream().map(category -> serviceHelper.makeCategoryResponse(CategoryResponse_Minimal.class, category, authentication)).toList();
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

        cacheHelper.updateCache(updateCategory, CATEGORY_CACHE, CATEGORY_LIST_CACHE);
        return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, updateCategory, authentication);
    }

    @Transactional
    public void deleteCategoryById(long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        cacheHelper.deleteCaches(CATEGORY_CACHE, category.getId(), CATEGORY_LIST_CACHE);
        categoryRepository.deleteById(id);
    }
}
