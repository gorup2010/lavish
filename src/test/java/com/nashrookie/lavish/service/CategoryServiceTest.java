package com.nashrookie.lavish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.ZonedDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.request.CreateCategoryDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateCategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.DeletedImage;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.CategoryRepository;
import com.nashrookie.lavish.repository.DeletedImageRepository;
import com.nashrookie.lavish.util.CategoryMapper;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DeletedImageRepository deletedImageRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private CategoryService categoryService;

    private Category mockCategory;
    private List<Category> mockCategories;
    private MultipartFile mockFile;
    private Map<String, String> cloudinaryResponse;

    @BeforeEach
    void setUp() {
        mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Test Category");
        mockCategory.setDescription("Test Description");
        mockCategory.setThumbnailImg("http://test-url.com/image.jpg");
        mockCategory.setThumbnailId("test_public_id");
        mockCategory.setCreatedOn(ZonedDateTime.now());

        mockCategories = List.of(mockCategory);

        mockFile = mock(MultipartFile.class);

        cloudinaryResponse = new HashMap<>();
        cloudinaryResponse.put("url", "http://test-url.com/image.jpg");
        cloudinaryResponse.put("public_id", "test_public_id");
    }

    @Test
    void findAll_ShouldReturnAllCategories() {
        // Arrange
        when(categoryRepository.findAllBy(Category.class)).thenReturn(mockCategories);

        // Act
        List<Category> result = categoryService.findAll(Category.class);

        // Assert
        assertEquals(mockCategories, result);
        verify(categoryRepository).findAllBy(Category.class);
    }

    @Test
    void getCategory_WithValidId_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        // Act
        Category result = categoryService.getCategory(1L);

        // Assert
        assertEquals(mockCategory, result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategory_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategory(999L));
        verify(categoryRepository).findById(999L);
    }

    @Test
    void getCategoriesAdminView_ShouldReturnPaginationResponse() {
        // Arrange
        CategoryFilterDto filterDto = new CategoryFilterDto("Test", 0, 10, "name", "asc");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(mockCategories, pageable, 1);

        when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(categoryPage);

        // Act
        PaginationResponse<CategoryInAdminDto> result = categoryService.getCategoriesAdminView(filterDto, pageable);

        // Assert
        assertEquals(0, result.getPage());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getData().size());
        assertEquals(mockCategory.getId(), result.getData().get(0).id());
        assertEquals(mockCategory.getName(), result.getData().get(0).name());
        verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getCategories_ShouldReturnPageOfCategories() {
        // Arrange
        CategoryFilterDto filterDto = new CategoryFilterDto("Test", 0, 10, "name", "asc");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(mockCategories, pageable, 1);

        when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(categoryPage);

        // Act
        Page<Category> result = categoryService.getCategories(filterDto, pageable);

        // Assert
        assertEquals(categoryPage, result);
        verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void createCategory_ShouldSaveCategoryWithThumbnail() {
        // Arrange
        CreateCategoryDto createDto = new CreateCategoryDto("New Category", "New Description", mockFile);
        Category newCategory = new Category();
        newCategory.setName("New Category");
        newCategory.setDescription("New Description");

        when(categoryMapper.toCategory(createDto)).thenReturn(newCategory);
        when(cloudinaryService.uploadFile(mockFile)).thenReturn(cloudinaryResponse);

        // Act
        categoryService.createCategory(createDto);

        // Assert
        assertEquals("http://test-url.com/image.jpg", newCategory.getThumbnailImg());
        assertEquals("test_public_id", newCategory.getThumbnailId());
        verify(categoryMapper).toCategory(createDto);
        verify(cloudinaryService).uploadFile(mockFile);
        verify(categoryRepository).save(newCategory);
    }

    @Test
    void updateCategoryDetails_WithValidId_ShouldUpdateAndSaveCategory() {
        // Arrange
        UpdateCategoryDetailsDto updateDto = new UpdateCategoryDetailsDto("Updated Name", "Updated Description");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        // Act
        categoryService.updateCategoryDetails(1L, updateDto);

        // Assert
        verify(categoryMapper).updateCategoryDetails(mockCategory, updateDto);
        verify(categoryRepository).save(mockCategory);
    }

    @Test
    void updateCategoryDetails_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        UpdateCategoryDetailsDto updateDto = new UpdateCategoryDetailsDto("Updated Name", "Updated Description");
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryDetails(999L, updateDto));
        verify(categoryRepository).findById(999L);
        verify(categoryMapper, never()).updateCategoryDetails(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategoryThumbnail_WithValidId_ShouldUpdateThumbnailAndSaveCategory() {
        // Arrange
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(cloudinaryService.uploadFile(mockFile)).thenReturn(cloudinaryResponse);

        // Act
        categoryService.updateCategoryThumbnail(1L, fileImageDto);

        // Assert
        verify(deletedImageRepository).save(any(DeletedImage.class));
        verify(cloudinaryService).uploadFile(mockFile);
        assertEquals("http://test-url.com/image.jpg", mockCategory.getThumbnailImg());
        assertEquals("test_public_id", mockCategory.getThumbnailId());
        verify(categoryRepository).save(mockCategory);
    }

    @Test
    void updateCategoryThumbnail_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategoryThumbnail(999L, fileImageDto));
        verify(categoryRepository).findById(999L);
        verify(deletedImageRepository, never()).save(any());
        verify(cloudinaryService, never()).uploadFile(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(mockCategory);
    }

    @Test
    void deleteCategory_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(999L));
        verify(categoryRepository).findById(999L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
