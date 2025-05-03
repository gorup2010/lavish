package com.nashrookie.lavish.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nashrookie.lavish.dto.filter.ProductFilterDto;
import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateProductDetailsDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.dto.response.ProductCardInAdminDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.DeletedImage;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.entity.ProductImage;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.repository.DeletedImageRepository;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.specification.ProductSpecification;
import com.nashrookie.lavish.util.PaginationUtils;
import com.nashrookie.lavish.util.ProductMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final DeletedImageRepository deletedImageRepository;
    private final ProductMapper productMapper;

    public PaginationResponse<ProductCardDto> getProductsUserView(ProductFilterDto productFilter, Pageable pageable) {
        Page<Product> products = this.getProducts(productFilter, pageable);
        return PaginationUtils.createPaginationResponse(products, ProductCardDto::fromModel);
    }

    public PaginationResponse<ProductCardInAdminDto> getProductsAdminView(ProductFilterDto productFilter,
            Pageable pageable) {
        Page<Product> products = this.getProducts(productFilter, pageable);
        return PaginationUtils.createPaginationResponse(products, ProductCardInAdminDto::fromModel);
    }

    public Page<Product> getProducts(ProductFilterDto productFilter, Pageable pageable) {
        List<Category> categories = null;
        if (productFilter.categoryIds() != null && !productFilter.categoryIds().isEmpty()) {
            categories = categoryRepository.findAllById(productFilter.categoryIds());
        }

        return productRepository.findAll(
                Specification.where(ProductSpecification.hasName(productFilter.name()))
                        .and(ProductSpecification.hasIsFeatured(productFilter.isFeatured()))
                        .and(ProductSpecification.hasCategory(categories))
                        .and(ProductSpecification.hasPrice(productFilter.minPrice(), productFilter.maxPrice())),
                pageable);
    }

    @Transactional
    public Product createProduct(CreateProductDto productCreation) {
        Product product = productMapper.toProduct(productCreation);
        
        Category category = categoryRepository.findById(productCreation.categoryId()).orElseThrow(() -> {
            log.error("Not found category with id {} in createProduct", productCreation.categoryId());
            throw new ResourceNotFoundException();
        });

        product.addCategory(category);

        Map<String, String> res = cloudinaryService.uploadFile(productCreation.thumbnailImg());
        product.setThumbnailImg(res.get("url"));
        product.setThumbnailId(res.get("public_id"));

        for (MultipartFile img : productCreation.images()) {
            Map<String, String> resImg = cloudinaryService.uploadFile(img);
            ProductImage productImage = ProductImage.builder()
                    .url(resImg.get("url"))
                    .publicId(resImg.get("public_id"))
                    .type("image")
                    .build();
            product.addImage(productImage);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void updateProductDetails(Long id, UpdateProductDetailsDto updateProduct) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.error("Not found product with id {} in updateProductDetails", id);
            throw new ResourceNotFoundException();
        });
        productMapper.updateProduct(product, updateProduct);

        Category updateCategory = categoryRepository.findById(updateProduct.categoryId()).orElseThrow(() -> {
            log.error("Not found category with id {} in updateProductDetails", updateProduct.categoryId());
            throw new ResourceNotFoundException();
        });
        Category category = product.getCategories().stream().findFirst().orElseThrow(() -> {
            log.error("In updateProductDetails, product with id {} has no category", product.getId());
            throw new RuntimeException();
        });
        
        if (!category.equals(updateCategory)) {
            product.getCategories().remove(category);
            product.addCategory(updateCategory);
        }

        productRepository.save(product);
    }

    @Transactional
    public void updateProductThumbnail (Long id, FileImageDto fileImageDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.error("Not found product with id {} in updateProductThumbnail", id);
            throw new ResourceNotFoundException();
        });

        DeletedImage deletedImage = DeletedImage.builder()
                .url(product.getThumbnailImg())
                .publicId(product.getThumbnailId())
                .build();
        deletedImageRepository.save(deletedImage);

        Map<String, String> res = cloudinaryService.uploadFile(fileImageDto.image());
        product.setThumbnailImg(res.get("url"));
        product.setThumbnailId(res.get("public_id"));

        productRepository.save(product);
    }

    @Transactional
    public void addProductImage (Long id, FileImageDto fileImageDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> {
            log.error("Not found product with id {} in addProductImage", id);
            throw new ResourceNotFoundException();
        });

        Map<String, String> res = cloudinaryService.uploadFile(fileImageDto.image());
        ProductImage productImage = ProductImage.builder()
                .url(res.get("url"))
                .publicId(res.get("public_id"))
                .type("image")
                .build();
        product.addImage(productImage);

        productRepository.save(product);
    }

    @Transactional
    public void deleteProductImages(Long productId, Long imgId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            log.error("Not found product with id {} in deleteProductImages", productId);
            throw new ResourceNotFoundException();
        });
        ProductImage productImage = product.getImages().stream().filter(image -> image.getId().equals(imgId)).findFirst().orElseThrow(() -> {
            log.error("Product with id {} has no image with id {}", productId, imgId);
            throw new ResourceNotFoundException();
        });

        DeletedImage deletedImage = DeletedImage.builder()
                .url(productImage.getUrl())
                .publicId(productImage.getPublicId())
                .build();
        deletedImageRepository.save(deletedImage);

        product.removeImage(productImage);
        productRepository.save(product);
    }
}
