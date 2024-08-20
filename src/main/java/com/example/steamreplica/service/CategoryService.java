package com.example.steamreplica.service;

import com.example.steamreplica.dtos.request.CategoryRequest;
import com.example.steamreplica.dtos.response.CategoryResponse_Full;
import com.example.steamreplica.dtos.response.CategoryResponse_Minimal;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ServiceHelper serviceHelper;
    
    public EntityModel<CategoryResponse_Full> getCategoryById(long id, Authentication authentication) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, category, authentication);
    }

    public Category getCategoryById_entity(long id, Authentication authentication) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }
    
    public List<EntityModel<CategoryResponse_Minimal>> getAllCategories(Authentication authentication) {
        List<Category> categories = categoryRepository.findAll();
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
        return serviceHelper.makeCategoryResponse(CategoryResponse_Full.class, updateCategory, authentication);
    }

    public void deleteCategoryById(long id) {
        categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        categoryRepository.deleteById(id);
    }
}
