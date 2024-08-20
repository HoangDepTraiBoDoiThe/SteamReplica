package com.example.steamreplica.service;

import com.example.steamreplica.controller.assembler.CategoryAssembler;
import com.example.steamreplica.dtos.request.CategoryRequest;
import com.example.steamreplica.dtos.response.CategoryResponse;
import com.example.steamreplica.model.game.Category;
import com.example.steamreplica.repository.CategoryRepository;
import com.example.steamreplica.service.exception.ResourceNotFoundException;
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
    private final CategoryAssembler categoryAssembler;
    
    public EntityModel<CategoryResponse> getCategoryById(long id, Authentication authentication) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        CategoryResponse categoryResponse = new CategoryResponse(category);
        return categoryAssembler.toModel(categoryResponse, authentication);
    }
    
    
    public Category getCategoryById_entity(long id, Authentication authentication) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
    }
    
    public CollectionModel<EntityModel<CategoryResponse>> getAllCategories(Authentication authentication) {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = categories.stream().map(CategoryResponse::new).toList();
        return categoryAssembler.toCollectionModel(categoryResponses, authentication);
    }

    @Transactional
    public EntityModel<CategoryResponse> createCategory(CategoryRequest categoryRequest, Authentication authentication) {
        Category newCategory = categoryRequest.toModel();
        return categoryAssembler.toModel(new CategoryResponse(categoryRepository.save(newCategory)), authentication);
    }

    @Transactional
    public EntityModel<CategoryResponse> updateCategory(long id, CategoryRequest categoryRequest, Authentication authentication) {
        Category categoryToUpdate = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        categoryToUpdate.setCategoryName(categoryRequest.getCategoryName());
        categoryToUpdate.setCategoryDescription(categoryRequest.getCategoryDescription());
        Category updateCategory = categoryRepository.save(categoryToUpdate);
        return categoryAssembler.toModel(new CategoryResponse(updateCategory), authentication);
    }

    public void deleteCategoryById(long id) {
        categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format("Category with id %d not found", id)));
        categoryRepository.deleteById(id);
    }
}
