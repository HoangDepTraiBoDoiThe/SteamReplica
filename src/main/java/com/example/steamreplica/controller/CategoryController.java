package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.CategoryRequest;
import com.example.steamreplica.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable long id, Authentication authentication) {
        return ResponseEntity.ok(categoryService.getCategoryById(id, authentication));
    }

    @GetMapping()
    public ResponseEntity<?> getAllCategories(Authentication authentication) {
        return ResponseEntity.ok(categoryService.getAllCategories(authentication));
    }
    
    @GetMapping("/create")
    public ResponseEntity<?> createCategory(@RequestBody @Validated CategoryRequest categoryRequest, Authentication authentication) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest, authentication));
    }
    
    @GetMapping("/{id}/update")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @RequestBody @Validated CategoryRequest categoryRequest, Authentication authentication) {
        return ResponseEntity.ok(ResponseEntity.ok(categoryService.updateCategory(id, categoryRequest, authentication)));
    }
    
    @GetMapping("/{id}/delete")
    public ResponseEntity<?> deleteCategoryById(@PathVariable long id) {
        categoryService.deleteCategoryById(id);
        return ResponseEntity.noContent().build();
    } 
}
