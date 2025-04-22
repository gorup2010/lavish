package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.ProductFilterDto;
import com.nashrookie.lavish.dto.request.ProductIdsDto;
import com.nashrookie.lavish.dto.request.UpdateProductDto;
import com.nashrookie.lavish.dto.response.ErrorResponse;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.dto.response.ProductInformationDto;
import com.nashrookie.lavish.service.ProductService;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<PaginationResponse<ProductCardDto>> getProducts(
            @Valid @ModelAttribute ProductFilterDto productFilter) {
        Sort sort = productFilter.orderBy().equalsIgnoreCase("desc")
                ? Sort.by(productFilter.sortBy()).descending()
                : Sort.by(productFilter.sortBy()).ascending();
        // Remember page starts from 0 in JPA
        Pageable pageable = PageRequest.of(productFilter.page(), productFilter.size(), sort);
        PaginationResponse<ProductCardDto> result = productService.getAllProducts(productFilter, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductInformationDto> getProductDetailInformation(@PathVariable Long id) {

        return ResponseEntity.ok(null);
    }

    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductInformationDto.class)))
    @ApiResponse(responseCode = "403", description = "Authorization failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @Secured("ADMIN")
    public ResponseEntity<ProductInformationDto> createProduct(@RequestBody CreateProductDto productCreation) {

        return ResponseEntity.ok(null);
    }

    @PatchMapping("/{id}")
    @Secured("ADMIN")
    public ResponseEntity<ProductInformationDto> updateProduct(@PathVariable String id,
            @RequestBody UpdateProductDto productUpdate) {

        return ResponseEntity.ok(null);
    }

    @DeleteMapping
    @Secured("ADMIN")
    public ResponseEntity<String> deleteProducts(@RequestBody ProductIdsDto productIds) {
        // Delete products

        return ResponseEntity.ok("Delete Successfully");
    }
}
