package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.nashrookie.lavish.dto.filter.ProductFilterDto;
import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.ProductIdsDto;
import com.nashrookie.lavish.dto.request.UpdateProductDetailsDto;
import com.nashrookie.lavish.dto.response.ErrorResponse;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.dto.response.ProductDetailsDto;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.service.CloudinaryService;
import com.nashrookie.lavish.service.ProductService;
import com.nashrookie.lavish.util.PaginationUtils;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    @GetMapping()
    public ResponseEntity<PaginationResponse<ProductCardDto>> getProductsUserView(
            @Valid @ModelAttribute ProductFilterDto productFilter) {
        Pageable pageable = PaginationUtils.createPageable(productFilter.page(),
                productFilter.size(),
                productFilter.sortBy(),
                productFilter.sortOrder());

        PaginationResponse<ProductCardDto> result = productService.getProductsUserView(productFilter, pageable);
        return ResponseEntity.ok(result);
    }

    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailsDto.class)))
    @ApiResponse(responseCode = "404", description = "Can not found product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailsDto> getProductDetailInformation(@PathVariable Long id) {
        Product res = productRepository.findWithImagesAndCategoriesById(id).orElseThrow(() -> {
            log.error("Not found product with id {}", id);
            throw new ResourceNotFoundException();
        });
        return ResponseEntity.ok(ProductDetailsDto.fromModel(res));
    }

    @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductDetailsDto.class)))
    @ApiResponse(responseCode = "403", description = "Authorization failed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    // @Secured("ADMIN")
    public ResponseEntity<Long> createProduct(@Valid @ModelAttribute CreateProductDto productCreation) {
        Long res = productService.createProduct(productCreation).getId();
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{id}")
    // @Secured("ADMIN")
    public ResponseEntity<String> updateProductDetails(@PathVariable Long id,
            @RequestBody UpdateProductDetailsDto updateProduct) {
        productService.updateProductDetails(id, updateProduct);
        return ResponseEntity.ok("Update Successfully");
    }

    @PatchMapping(value = "/{id}/thumbnail", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> updateProductThumbnail(@PathVariable Long id,
            @ModelAttribute FileImageDto fileImageDto) {
        productService.updateProductThumbnail(id, fileImageDto);
        return ResponseEntity.ok("Update Thumbnail Successfully");
    }

    @PostMapping(value = "/{id}/images", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> addImage(@PathVariable Long id,
            @ModelAttribute FileImageDto fileImageDto) {
        productService.addProductImage(id, fileImageDto);
        return ResponseEntity.ok("Update Images Successfully");
    }

    @DeleteMapping("/{product-id}/images/{img-id}")
    public ResponseEntity<String> deleteProductImages(@PathVariable(value = "product-id") Long productId, @PathVariable(value = "img-id") Long imgId) {
        productService.deleteProductImages(productId, imgId);
        return ResponseEntity.ok("Delete Images Successfully");
    }

    @DeleteMapping
    @Secured("ADMIN")
    public ResponseEntity<String> deleteProducts(@RequestBody ProductIdsDto productIds) {
        // Delete products

        return ResponseEntity.ok("Delete Successfully");
    }
}
