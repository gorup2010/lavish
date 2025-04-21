package com.nashrookie.lavish.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.nashrookie.lavish.dto.request.ProductFilterDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PaginationResponse<ProductCardDto> getAllProducts(ProductFilterDto productFilter, Pageable pageable) {
        List<Category> categories = null;
        if (productFilter.categorieIds() != null && !productFilter.categorieIds().isEmpty()) {
            categories = categoryRepository.findAllById(productFilter.categorieIds());
        }

        Page<Product> products = productRepository.findAll(
                Specification.where(ProductSpecification.hasName(productFilter.name()))
                        .and(ProductSpecification.hasIsFeatured(productFilter.isFeatured()))
                        .and(ProductSpecification.hasCategory(categories))
                        .and(ProductSpecification.hasPrice(productFilter.minPrice(), productFilter.maxPrice())),
                pageable);
                
        PaginationResponse<ProductCardDto> paginationResponse = new PaginationResponse<>();
        paginationResponse.setPage(pageable.getPageNumber() + 1);
        paginationResponse.setTotal((int) products.getTotalElements());
        paginationResponse.setTotalPages(products.getTotalPages());
        paginationResponse.setData(products.getContent().stream().map(ProductCardDto::fromModel).toList());

        return paginationResponse;
    }
}
