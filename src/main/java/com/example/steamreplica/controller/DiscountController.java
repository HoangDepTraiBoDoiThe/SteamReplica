package com.example.steamreplica.controller;

import com.example.steamreplica.dtos.request.DiscountRequest;
import com.example.steamreplica.dtos.response.DiscountResponse;
import com.example.steamreplica.service.DiscountService;
import com.example.steamreplica.util.StaticHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discount")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiscount(@PathVariable long id, Authentication authentication) {
        EntityModel<DiscountResponse> responseEntityModel = discountService.getDiscountById(id, authentication);
        responseEntityModel.add(
);
        return ResponseEntity.ok(responseEntityModel);
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getDiscountByCode(@PathVariable String code, Authentication authentication) {
        EntityModel<DiscountResponse> responseEntityModel = discountService.getDiscountByCode(code, authentication);
        return ResponseEntity.ok(responseEntityModel);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDiscounts(Authentication authentication) {
        return ResponseEntity.ok(discountService.getAllDiscounts(authentication));
    }
    
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    public ResponseEntity<?> addDiscount(@RequestBody @Validated DiscountRequest discountRequest, Authentication authentication, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        return ResponseEntity.ok(discountService.addDiscount(discountRequest, authentication));
    }
    
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PUBLISHER', 'GAME_DEVELOPER')")
    public ResponseEntity<?> updateDiscount(@PathVariable long id, @RequestBody @Validated DiscountRequest discountRequest, Authentication authentication, BindingResult result) {
        var errors = StaticHelper.extractBindingErrorMessages(result);
        if (!errors.isEmpty()) return ResponseEntity.badRequest().body(errors);
        return ResponseEntity.ok(discountService.updateDiscount(id, discountRequest, authentication));
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok("Discount deleted successfully");
    }
}
