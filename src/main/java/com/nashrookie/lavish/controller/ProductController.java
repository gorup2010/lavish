package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.ProductFilterDto;
import com.nashrookie.lavish.dto.request.ProductIdsDto;
import com.nashrookie.lavish.dto.request.UpdateProductDto;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.dto.response.ProductInformationDto;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    


    @GetMapping()
    public ResponseEntity<List<ProductCardDto>> getProducts(@ModelAttribute ProductFilterDto productFilter) {
        // Get all products

        return ResponseEntity.ok(null);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInformationDto> getProductDetailInformation(@PathVariable Long id) {
        
        return ResponseEntity.ok(null);
    }

    @PostMapping
    @Secured("ADMIN")
    public ResponseEntity<ProductInformationDto> createProduct(@RequestBody CreateProductDto productCreation) {
        
        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}")
    @Secured("ADMIN")
    public ResponseEntity<ProductInformationDto> updateProduct(@PathVariable String id, @RequestBody UpdateProductDto productUpdate) {
        
        return ResponseEntity.ok(null);
    }

    @DeleteMapping
    @Secured("ADMIN")
    public ResponseEntity<String> deleteProducts(@RequestBody ProductIdsDto productIds) {
        // Delete products

        return ResponseEntity.ok("Delete Successfully");
    }
}
