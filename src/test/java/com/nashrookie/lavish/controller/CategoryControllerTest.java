package com.nashrookie.lavish.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashrookie.lavish.LavishApplication;
import com.nashrookie.lavish.configuration.TestWebSecurityConfig;
import com.nashrookie.lavish.dto.request.CreateCategoryDto;
import com.nashrookie.lavish.dto.request.FileImageDto;
import com.nashrookie.lavish.dto.request.UpdateCategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryDetailsDto;
import com.nashrookie.lavish.dto.response.CategoryDto;
import com.nashrookie.lavish.entity.Category;
import com.nashrookie.lavish.service.CategoryService;
import com.nashrookie.lavish.service.JwtService;
import com.nashrookie.lavish.util.CategoryMapper;

@WebMvcTest(CategoryController.class)
@ContextConfiguration(classes = LavishApplication.class)
@Import(TestWebSecurityConfig.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @Test
    void getCategoriesUserView_shouldReturnListOfCategories() throws Exception {
        CategoryDto category = new CategoryDto(1L, "Books", "Book Category", "img.png");
        when(categoryService.findAll(CategoryDto.class)).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Books"));
    }

    @Test
    void getCategoryDetails_shouldReturnCategoryDetailsDto() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Books");
        category.setDescription("desc");
        category.setThumbnailImg("img.png");

        CategoryDetailsDto dto = new CategoryDetailsDto(1L, "Books", "desc", "img.png");
        when(categoryService.getCategory(1L)).thenReturn(category);
        when(categoryMapper.toCategoryDetailsDto(category)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Books"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void createCategory_shouldCreateCategory() throws Exception {
        MockMultipartFile image = new MockMultipartFile("thumbnailImg", "img.png", "image/png", "dummy".getBytes());

        mockMvc.perform(multipart("/api/v1/categories")
                .file(image)
                .param("name", "Books")
                .param("description", "Book Category"))
                .andExpect(status().isOk())
                .andExpect(content().string("Create Category Successfully"));

        verify(categoryService).createCategory(any(CreateCategoryDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void updateCategoryDetails_shouldUpdateCategory() throws Exception {
        UpdateCategoryDetailsDto updateDto = new UpdateCategoryDetailsDto("Updated Name", "Updated Description");

        mockMvc.perform(patch("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Update category details successfully"));

        verify(categoryService).updateCategoryDetails(eq(1L), any(UpdateCategoryDetailsDto.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ADMIN" })
    void updateCategoryThumbnail_shouldUpdateThumbnail() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "img.png", "image/png", "dummy".getBytes());

        mockMvc.perform(multipart("/api/v1/categories/1/thumbnail")
                .file(image)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().string("Update category thumbnail successfully"));

        verify(categoryService).updateCategoryThumbnail(eq(1L), any(FileImageDto.class));
    }
}