package com.nashrookie.lavish.service;

import java.util.Map;

import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.request.CreateCategoryDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateCategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.DeletedImage;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.repository.DeletedImageRepository;
import com.nashrookie.lavish.specification.CategorySpecification;
import com.nashrookie.lavish.util.CategoryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final DeletedImageRepository deletedImageRepository;
    private final CloudinaryService cloudinaryService;

    public PaginationResponse<CategoryInAdminDto> getCategoriesAdminView(CategoryFilterDto categoryFilter,
            Pageable pageable) {
        Page<Category> products = this.getCategories(categoryFilter, pageable);

        return PaginationResponse.<CategoryInAdminDto>builder()
                .page(pageable.getPageNumber())
                .total(products.getTotalElements())
                .totalPages(products.getTotalPages())
                .data(products.getContent().stream().map(CategoryInAdminDto::fromModel).toList())
                .build();
    }

    public Page<Category> getCategories(CategoryFilterDto categoryFilter, Pageable pageable) {
        return categoryRepository.findAll(Specification.where(CategorySpecification.hasName(categoryFilter.name())),
                pageable);
    }

    @Transactional
    public void createCategory(CreateCategoryDto createCategoryDto) {
        Category category = categoryMapper.toCategory(createCategoryDto);

        Map<String, String> res = cloudinaryService.uploadFile(createCategoryDto.thumbnailImg());
        category.setThumbnailImg(res.get("url"));
        category.setThumbnailId(res.get("public_id"));

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryDetails(Long id, UpdateCategoryDetailsDto updateCategoryDetailsDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Not found category with id {} in updateCategoryDetails", id);
            throw new ResourceNotFoundException();
        });
        categoryMapper.updateCategoryDetails(category, updateCategoryDetailsDto);
        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategoryThumbnail(Long id, FileImageDto fileImageDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Not found category with id {} in updateCategoryDetails", id);
            throw new ResourceNotFoundException();
        });

        DeletedImage deletedImage = DeletedImage.builder()
                .url(category.getThumbnailImg())
                .publicId(category.getThumbnailId())
                .build();
        deletedImageRepository.save(deletedImage);

        Map<String, String> res = cloudinaryService.uploadFile(fileImageDto.image());
        category.setThumbnailImg(res.get("url"));
        category.setThumbnailId(res.get("public_id"));

        categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory() {

    }
}
