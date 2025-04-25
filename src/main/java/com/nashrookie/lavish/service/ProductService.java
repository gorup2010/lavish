package com.nashrookie.lavish.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.request.ProductFilterDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.dto.response.ProductCardInAdminDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PaginationResponse<ProductCardDto> getProductsUserView(ProductFilterDto productFilter, Pageable pageable) {
        Page<Product> products = this.getProducts(productFilter, pageable);

        return PaginationResponse.<ProductCardDto>builder()
                .page(pageable.getPageNumber())
                .total(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .data(products.getContent().stream().map(ProductCardDto::fromModel).toList())
                .build();
    }

    public PaginationResponse<ProductCardInAdminDto> getProductsAdminView(ProductFilterDto productFilter,
            Pageable pageable) {
        Page<Product> products = this.getProducts(productFilter, pageable);

        return PaginationResponse.<ProductCardInAdminDto>builder()
                .page(pageable.getPageNumber())
                .total(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .data(products.getContent().stream().map(ProductCardInAdminDto::fromModel).toList())
                .build();
    }

    public Page<Product> getProducts(ProductFilterDto productFilter, Pageable pageable) {
        List<Category> categories = null;
        if (productFilter.categorieIds() != null && !productFilter.categorieIds().isEmpty()) {
            categories = categoryRepository.findAllById(productFilter.categorieIds());
        }

        return productRepository.findAll(
                Specification.where(ProductSpecification.hasName(productFilter.name()))
                        .and(ProductSpecification.hasIsFeatured(productFilter.isFeatured()))
                        .and(ProductSpecification.hasCategory(categories))
                        .and(ProductSpecification.hasPrice(productFilter.minPrice(), productFilter.maxPrice())),
                pageable);
    }
}
