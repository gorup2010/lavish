package com.nashrookie.lavish.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
import com.nashrookie.lavish.util.ProductMapper;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private DeletedImageRepository deletedImageRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private ProductImage testImage;
    private List<Product> productList;
    private Page<Product> productPage;
    private Pageable pageable;
    private ProductFilterDto productFilterDto;
    private ProductFilterDto productFilterDtoWithoutCategory;
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Test Category")
                .build();

        testImage = ProductImage.builder()
                .id(1L)
                .url("http://example.com/image.jpg")
                .publicId("image_public_id")
                .type("image")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(1L)
                .quantity(10)
                .isFeatured(true)
                .thumbnailImg("http://example.com/thumbnail.jpg")
                .thumbnailId("thumbnail_public_id")
                .build();
        testProduct.addCategory(testCategory);
        testProduct.addImage(testImage);

        productList = Arrays.asList(testProduct);
        pageable = PageRequest.of(0, 10);
        productPage = new PageImpl<>(productList, pageable, 1);

        productFilterDto = new ProductFilterDto(
                "Test",
                null,
                null,
                10L,
                100L,
                true,
                null,
                null,
                List.of(1L));

        productFilterDtoWithoutCategory = new ProductFilterDto(
                "Test",
                null,
                null,
                10L,
                100L,
                true,
                null,
                null,
                null);

        mockFile = mock(MultipartFile.class);
    }

    @Test
    void testGetProductDetails_shouldReturnProduct_whenProductExists() {
        when(productRepository.findWithImagesAndCategoriesById(1L)).thenReturn(Optional.of(testProduct));
        Product result = productService.getProductDetails(1L);

        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
        verify(productRepository).findWithImagesAndCategoriesById(1L);
    }

    @Test
    void testGetProductDetails_shouldThrowResourceNotFoundException_whenProductNotFound() {

        when(productRepository.findWithImagesAndCategoriesById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductDetails(999L);
        });
        verify(productRepository).findWithImagesAndCategoriesById(999L);
    }

    @Test
    void getProductsUserView_shouldReturnPaginationResponse() {

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        PaginationResponse<ProductCardDto> result = productService.getProductsUserView(productFilterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalPages());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProductsAdminView_shouldReturnPaginationResponse() {

        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        PaginationResponse<ProductCardInAdminDto> result = productService.getProductsAdminView(productFilterDto,
                pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalPages());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withCategoryIds_shouldCallRepositoryWithCorrectParams() {

        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(testCategory));
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(productPage);

        Page<Product> result = productService.getProducts(productFilterDto, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository).findAllById(productFilterDto.categoryIds());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProducts_withoutCategoryIds_shouldCallRepositoryWithNullCategory() {

        when(productRepository.findAll(any(Specification.class),
                eq(pageable))).thenReturn(productPage);

        Page<Product> result = productService.getProducts(productFilterDtoWithoutCategory,
                pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository, never()).findAllById(anyList());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void createProduct_shouldCreateAndSaveProduct() {
        Product newProduct = Product.builder().build();
        CreateProductDto createDto = new CreateProductDto(
                "New Product",
                1000L,
                "A new product description",
                mockFile,
                true,
                10,
                1L,
                List.of(mockFile));

        Map<String, String> uploadResponse = new HashMap<>();
        uploadResponse.put("url", "http://example.com/new-image.jpg");
        uploadResponse.put("public_id", "new_image_public_id");

        when(productMapper.toProduct(any(CreateProductDto.class))).thenReturn(newProduct);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(cloudinaryService.uploadFile(any(MultipartFile.class))).thenReturn(uploadResponse);
        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        Product result = productService.createProduct(createDto);

        assertNotNull(result);
        verify(productMapper).toProduct(createDto);
        verify(categoryRepository).findById(1L);
        verify(cloudinaryService, times(2)).uploadFile(any(MultipartFile.class));
        verify(productRepository).save(newProduct);
    }

    @Test
    void createProduct_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        CreateProductDto createDto = new CreateProductDto(
                "New Product",
                1000L,
                "A new product description",
                mockFile,
                true,
                10,
                999L,
                null
        );

        when(productMapper.toProduct(any(CreateProductDto.class))).thenReturn(new Product());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(createDto);
        });
        verify(categoryRepository).findById(999L);
        verify(cloudinaryService, never()).uploadFile(any(MultipartFile.class));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProductDetails_shouldUpdateProduct() {
        UpdateProductDetailsDto updateDto = new UpdateProductDetailsDto(
                "Updated Product",
                1500L,
                "An updated product description",
                true,
                15,
                1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        productService.updateProductDetails(1L, updateDto);

        verify(productMapper).updateProduct(testProduct, updateDto);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProductDetails_shouldThrowResourceNotFoundException_whenProductNotFound() {
        UpdateProductDetailsDto updateDto = new UpdateProductDetailsDto(null, null, null, null, null, 1L);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductDetails(999L, updateDto);
        });
        verify(productRepository).findById(999L);
        verify(productMapper, never()).updateProduct(any(), any());
    }

    @Test
    void updateProductDetails_shouldThrowResourceNotFoundException_whenCategoryNotFound() {
        UpdateProductDetailsDto updateDto = new UpdateProductDetailsDto(null, null, null, null, null, 999L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductDetails(1L, updateDto);
        });
        verify(productRepository).findById(1L);
        verify(categoryRepository).findById(999L);
    }

    @Test
    void updateProductDetails_shouldUpdateCategory_whenNewCategoryProvided() {
        Category newCategory = Category.builder().id(2L).name("New Category").build();
        UpdateProductDetailsDto updateDto = new UpdateProductDetailsDto(null, null, null, null, null, 2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));

        productService.updateProductDetails(1L, updateDto);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();

        assertTrue(savedProduct.getCategories().contains(newCategory));
        assertFalse(savedProduct.getCategories().contains(testCategory));
    }

    @Test
    void updateProductThumbnail_shouldUpdateThumbnail() {
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        Map<String, String> uploadResponse = new HashMap<>();
        uploadResponse.put("url", "http://example.com/new-thumbnail.jpg");
        uploadResponse.put("public_id", "new_thumbnail_public_id");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cloudinaryService.uploadFile(mockFile)).thenReturn(uploadResponse);

        productService.updateProductThumbnail(1L, fileImageDto);

        ArgumentCaptor<DeletedImage> deletedImageCaptor = ArgumentCaptor.forClass(DeletedImage.class);
        verify(deletedImageRepository).save(deletedImageCaptor.capture());
        DeletedImage deletedImage = deletedImageCaptor.getValue();

        assertEquals("http://example.com/thumbnail.jpg", deletedImage.getUrl());
        assertEquals("thumbnail_public_id", deletedImage.getPublicId());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();

        assertEquals(uploadResponse.get("url"), updatedProduct.getThumbnailImg());
        assertEquals(uploadResponse.get("public_id"), updatedProduct.getThumbnailId());
    }

    @Test
    void updateProductThumbnail_shouldThrowResourceNotFoundException_whenProductNotFound() {
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.updateProductThumbnail(999L, fileImageDto);
        });
        verify(cloudinaryService, never()).uploadFile(any(MultipartFile.class));
    }

    @Test
    void addProductImage_shouldAddImageToProduct() {
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        Map<String, String> uploadResponse = new HashMap<>();
        uploadResponse.put("url", "http://example.com/new-image.jpg");
        uploadResponse.put("public_id", "new_image_public_id");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cloudinaryService.uploadFile(mockFile)).thenReturn(uploadResponse);

        productService.addProductImage(1L, fileImageDto);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();

        assertEquals(2, updatedProduct.getImages().size());
        assertTrue(updatedProduct.getImages().stream()
                .anyMatch(img -> img.getUrl().equals(uploadResponse.get("url"))
                        && img.getPublicId().equals(uploadResponse.get("public_id"))));
    }

    @Test
    void addProductImage_shouldThrowResourceNotFoundException_whenProductNotFound() {
        FileImageDto fileImageDto = new FileImageDto(mockFile);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.addProductImage(999L, fileImageDto);
        });
        verify(cloudinaryService, never()).uploadFile(any(MultipartFile.class));
    }

    @Test
    void deleteProductImages_shouldRemoveImageFromProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProductImages(1L, 1L);

        ArgumentCaptor<DeletedImage> deletedImageCaptor = ArgumentCaptor.forClass(DeletedImage.class);
        verify(deletedImageRepository).save(deletedImageCaptor.capture());
        DeletedImage deletedImage = deletedImageCaptor.getValue();

        assertEquals(testImage.getUrl(), deletedImage.getUrl());
        assertEquals(testImage.getPublicId(), deletedImage.getPublicId());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product updatedProduct = productCaptor.getValue();

        assertEquals(0, updatedProduct.getImages().size());
    }

    @Test
    void deleteProductImages_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProductImages(999L, 1L);
        });
    }

    @Test
    void deleteProductImages_shouldThrowResourceNotFoundException_whenImageNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProductImages(1L, 999L);
        });
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        productService.deleteProduct(1L);

        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProduct_shouldThrowResourceNotFoundException_whenProductNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });
    }
}
