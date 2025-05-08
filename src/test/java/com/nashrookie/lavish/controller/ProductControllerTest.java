package com.nashrookie.lavish.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashrookie.lavish.LavishApplication;
import com.nashrookie.lavish.configuration.TestWebSecurityConfig;
import com.nashrookie.lavish.dto.filter.ProductFilterDto;
import com.nashrookie.lavish.dto.request.CreateProductDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateProductDetailsDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.exception.ResourceNotFoundException;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.service.JwtService;
import com.nashrookie.lavish.service.ProductService;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(controllers = ProductController.class)
@ContextConfiguration(classes = LavishApplication.class)
@Import(TestWebSecurityConfig.class)
public class ProductControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ProductService productService;

        @MockitoBean
        private ProductRepository productRepository;

        @MockitoBean
        private JwtService jwtService;

        private Category testCategory;
        private Product testProduct;
        private PaginationResponse<ProductCardDto> paginationResponse;
        private ProductCardDto productCardDto;

        @BeforeEach
        void setUp() {
                when(jwtService.getStringRoleList(anyString())).thenReturn(Arrays.asList("ROLE_ADMIN"));

                testCategory = Category.builder().id(1L).name("Test Category").description("Test Description")
                                .thumbnailImg("thumbnailUrl").thumbnailId("thumbnailId").build();
                testProduct = Product.builder().id(1L).name("Test Product").price(100L).rating(4.5)
                                .thumbnailImg("thumbnailUrl")
                                .thumbnailId("thumbnailId").description("Test Description").quantity(10)
                                .isFeatured(true).build();
                testProduct.addCategory(testCategory);

                productCardDto = ProductCardDto.fromModel(testProduct);

                List<ProductCardDto> content = Arrays.asList(productCardDto);
                paginationResponse = PaginationResponse.<ProductCardDto>builder().page(1).total(1L).totalPages(1)
                                .data(content)
                                .build();
        }

        @Test
        void testGetProductsUserView_withInvalidFilter_returnBadRequestError() throws Exception {

                when(productService.getProductsUserView(any(ProductFilterDto.class), any(Pageable.class)))
                                .thenReturn(paginationResponse);

                mockMvc.perform(get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("sortOrder", "asc")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message")
                                                .value("{sortBy=[Invalid sortBy field in product filter]}"));
        }

        @Test
        void testGetProductsUserView_withValidFilter_returnOk() throws Exception {

                when(productService.getProductsUserView(any(ProductFilterDto.class), any(Pageable.class)))
                                .thenReturn(paginationResponse);

                mockMvc.perform(get("/api/v1/products")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "price")
                                .param("sortOrder", "asc")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.page").value(1))
                                .andExpect(jsonPath("$.total").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1))
                                .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        void testGetProductDetails_found_returnOk() throws Exception {

                when(productService.getProductDetails(anyLong())).thenReturn(testProduct);

                mockMvc.perform(get("/api/v1/products/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("Test Product"))
                                .andExpect(jsonPath("$.price").value(100))
                                .andExpect(jsonPath("$.rating").value(4.5))
                                .andExpect(jsonPath("$.thumbnailImg").value("thumbnailUrl"))
                                .andExpect(jsonPath("$.description").value("Test Description"))
                                .andExpect(jsonPath("$.quantity").value(10))
                                .andExpect(jsonPath("$.isFeatured").value(true))
                                .andExpect(jsonPath("$.categories").isArray());
        }

        @Test
        void testGetProductDetails_notFound_returnNotFound() throws Exception {
                when(productService.getProductDetails(anyLong())).thenThrow(new ResourceNotFoundException());
                mockMvc.perform(get("/api/v1/products/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message")
                                                .value("Can not found resource"));
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testCreateProduct_withValidDtoAndAdmin_returnSuccessMsg() throws Exception {
                MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailImg", "thumbnail.jpg", "image/jpeg",
                                "thumbnail content".getBytes());
                when(productService.createProduct(any())).thenReturn(null);

                mockMvc.perform(multipart("/api/v1/products")
                                .file(thumbnailFile)
                                .param("name", "Test Product")
                                .param("price", "100")
                                .param("description", "Test Description")
                                .param("isFeatured", "true")
                                .param("quantity", "10")
                                .param("categoryId", "1")
                                .with(request -> {
                                        request.setMethod("POST");
                                        return request;
                                }))
                                .andExpect(status().isOk());
                verify(productService, times(1)).createProduct(any(CreateProductDto.class));
        }

        @Test
        @WithMockUser(username = "user", authorities = { "USER" })
        void testCreateProduct_withUser_returnForbidden() throws Exception {
                MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailImg", "thumbnail.jpg", "image/jpeg",
                                "thumbnail content".getBytes());
                when(productService.createProduct(any())).thenReturn(null);

                mockMvc.perform(multipart("/api/v1/products")
                                .file(thumbnailFile)
                                .param("name", "Test Product")
                                .param("price", "100")
                                .param("description", "Test Description")
                                .param("isFeatured", "true")
                                .param("quantity", "10")
                                .param("categoryId", "1")
                                .with(request -> {
                                        request.setMethod("POST");
                                        return request;
                                }))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.message")
                                                .value("Access Denied"));
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testUpdateProductDetails_returnSuccessMsg() throws Exception {
                UpdateProductDetailsDto updateDto = new UpdateProductDetailsDto("Test Product", 100L,
                                "Test Description", true, 10, 1L);
                doNothing().when(productService).updateProductDetails(anyLong(), any());

                mockMvc.perform(patch("/api/v1/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Update Successfully"));
                verify(productService, times(1)).updateProductDetails(anyLong(), any());
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testUpdateProductThumbnail_returnSuccessMsg() throws Exception {
                MockMultipartFile thumbnailFile = new MockMultipartFile("thumbnailImg", "thumbnail.jpg", "image/jpeg",
                                "thumbnail content".getBytes());
                doNothing().when(productService).updateProductThumbnail(anyLong(), any());

                mockMvc.perform(multipart("/api/v1/products/1/thumbnail")
                                .file(thumbnailFile)
                                .with(request -> {
                                        request.setMethod("PATCH");
                                        return request;
                                }))
                                .andExpect(status().isOk());
                verify(productService, times(1)).updateProductThumbnail(anyLong(), any());
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testAddImage() throws Exception {
                MockMultipartFile imageFile = new MockMultipartFile(
                                "image",
                                "product-image.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "image content".getBytes());

                doNothing().when(productService).addProductImage(eq(1L), any(FileImageDto.class));

                mockMvc.perform(multipart("/api/v1/products/1/images")
                                .file(imageFile))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Add Image Successfully"));

                verify(productService, times(1)).addProductImage(eq(1L), any(FileImageDto.class));
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testDeleteProductImages() throws Exception {
                doNothing().when(productService).deleteProductImages(1L, 2L);

                mockMvc.perform(delete("/api/v1/products/1/images/2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Delete Image Successfully"));

                verify(productService, times(1)).deleteProductImages(1L, 2L);
        }

        @Test
        @WithMockUser(username = "admin", authorities = { "ADMIN" })
        void testDeleteProducts() throws Exception {
                doNothing().when(productService).deleteProduct(1L);

                mockMvc.perform(delete("/api/v1/products/1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Delete Product Successfully"));

                verify(productService, times(1)).deleteProduct(1L);
        }
}
