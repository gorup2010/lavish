package com.nashrookie.lavish.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nashrookie.lavish.LavishApplication;
import com.nashrookie.lavish.configuration.TestWebSecurityConfig;
import com.nashrookie.lavish.dto.filter.CategoryFilterDto;
import com.nashrookie.lavish.dto.filter.ProductFilterDto;
import com.nashrookie.lavish.dto.filter.UserFilterDto;
import com.nashrookie.lavish.dto.response.CategoryInAdminDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.ProductCardInAdminDto;
import com.nashrookie.lavish.dto.response.UserInAdminDto;
import com.nashrookie.lavish.service.CategoryService;
import com.nashrookie.lavish.service.JwtService;
import com.nashrookie.lavish.service.ProductService;
import com.nashrookie.lavish.service.UserService;
import com.nashrookie.lavish.util.PaginationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = LavishApplication.class)
@Import(TestWebSecurityConfig.class)
@WithMockUser(username = "admin", authorities = { "ADMIN" })
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ProductService productService;

    @Nested
    @DisplayName("GET /categories tests")
    class GetCategoriesTests {

        private CategoryInAdminDto categoryDto1;
        private CategoryInAdminDto categoryDto2;
        private PaginationResponse<CategoryInAdminDto> paginationResponse;

        @BeforeEach
        void setUp() {
            categoryDto1 = new CategoryInAdminDto(1L, "Rolex", "rolex.jpg", "description", ZonedDateTime.now());
            categoryDto2 = new CategoryInAdminDto(2L, "Omega", "omega.jpg", "description", ZonedDateTime.now());

            List<CategoryInAdminDto> categories = Arrays.asList(categoryDto1, categoryDto2);
            paginationResponse = PaginationResponse.<CategoryInAdminDto>builder()
                    .page(0)
                    .total(2L)
                    .totalPages(1)
                    .data(categories)
                    .build();
        }

        @Test
        @DisplayName("Should return categories with default pagination")
        void shouldReturnCategoriesWithDefaultPagination() throws Exception {
            // Given
            when(categoryService.getCategoriesAdminView(any(CategoryFilterDto.class), any(Pageable.class)))
                    .thenReturn(paginationResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.total").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Rolex"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].name").value("Omega"));
        }

        @Test
        void shouldFilterCategoriesByName() throws Exception {
            // Given
            PaginationResponse<CategoryInAdminDto> filteredResponse = PaginationResponse.<CategoryInAdminDto>builder()
                    .page(0)
                    .total(1L)
                    .totalPages(1)
                    .data(Collections.singletonList(categoryDto1))
                    .build();

            when(categoryService.getCategoriesAdminView(any(CategoryFilterDto.class), any(Pageable.class)))
                    .thenReturn(filteredResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("name", "Rolex")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Rolex"));
        }
    }

    @Nested
    @DisplayName("GET /users tests")
    class GetUsersTests {

        private UserInAdminDto userDto1;
        private UserInAdminDto userDto2;
        private PaginationResponse<UserInAdminDto> paginationResponse;

        @BeforeEach
        void setUp() {
            userDto1 = new UserInAdminDto(1L, "johnsmith", "John", "Smith", ZonedDateTime.now());
            userDto2 = new UserInAdminDto(2L, "janesmith", "Jane", "Smith", ZonedDateTime.now());

            List<UserInAdminDto> users = Arrays.asList(userDto1, userDto2);
            paginationResponse = PaginationResponse.<UserInAdminDto>builder()
                    .page(0)
                    .total(2L)
                    .totalPages(1)
                    .data(users)
                    .build();
        }

        @Test
        void shouldReturnUsersWithDefaultPagination() throws Exception {
            // Given
            when(userService.getUsersAdminView(any(UserFilterDto.class), any(Pageable.class)))
                    .thenReturn(paginationResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.total").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].username").value("johnsmith"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].username").value("janesmith"));
        }

        @Test
        @DisplayName("Should filter users by username")
        void shouldFilterUsersByUsername() throws Exception {
            // Given
            PaginationResponse<UserInAdminDto> filteredResponse = PaginationResponse.<UserInAdminDto>builder()
                    .page(0)
                    .total(1L)
                    .totalPages(1)
                    .data(Collections.singletonList(userDto1))
                    .build();

            when(userService.getUsersAdminView(any(UserFilterDto.class), any(Pageable.class)))
                    .thenReturn(filteredResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "john")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].username").value("johnsmith"));
        }
    }

    @Nested
    @DisplayName("GET /products tests")
    class GetProductsTests {

        private ProductCardInAdminDto productDto1;
        private ProductCardInAdminDto productDto2;
        private PaginationResponse<ProductCardInAdminDto> paginationResponse;

        @BeforeEach
        void setUp() {
            productDto1 = new ProductCardInAdminDto(1L, "Rolex", "rolex.jpg", 999L, 10, ZonedDateTime.now());
            productDto2 = new ProductCardInAdminDto(2L, "Omega", "omega.jpg", 1999L, 5, ZonedDateTime.now());

            List<ProductCardInAdminDto> products = Arrays.asList(productDto1, productDto2);
            paginationResponse = PaginationResponse.<ProductCardInAdminDto>builder()
                    .page(0)
                    .total(2L)
                    .totalPages(1)
                    .data(products)
                    .build();
        }

        @Test
        void shouldReturnProductsWithDefaultPagination() throws Exception {
            // Given
            when(productService.getProductsAdminView(any(ProductFilterDto.class), any(Pageable.class))).thenReturn(paginationResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/products")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.total").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Rolex"))
                    .andExpect(jsonPath("$.data[1].id").value(2))
                    .andExpect(jsonPath("$.data[1].name").value("Omega"));
        }

        @Test
        @DisplayName("Should filter products by name")
        void shouldFilterProductsByName() throws Exception {
            // Given
            ProductFilterDto filterDto = new ProductFilterDto("Rolex", 0, 10, null, null, null, "createdOn", "desc", null);
            Pageable pageable = PaginationUtils.createPageable(0, 10, "createdOn", "desc");

            PaginationResponse<ProductCardInAdminDto> filteredResponse = PaginationResponse
                    .<ProductCardInAdminDto>builder()
                    .page(0)
                    .total(1L)
                    .totalPages(1)
                    .data(Collections.singletonList(productDto1))
                    .build();

            when(productService.getProductsAdminView(eq(filterDto), eq(pageable))).thenReturn(filteredResponse);

            // When & Then
            mockMvc.perform(get("/api/v1/admin/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("name", "Rolex")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "createdOn")
                    .param("sortOrder", "desc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Rolex"));
        }
    }
}
