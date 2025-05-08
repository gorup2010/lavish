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

import com.nashrookie.lavish.dto.request.CreateRatingDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.RatingDto;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.Product;
import com.nashrookie.lavish.entity.Rating;
import com.nashrookie.lavish.entity.User;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.repository.RatingRepository;
import com.nashrookie.lavish.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RatingService ratingService;

    private AppUserDetails mockUserDetails;
    private User mockUser;
    private Product mockProduct;
    private Rating mockRating;
    private RatingDto mockRatingDto;
    private List<RatingDto> ratingDtoList;
    private CreateRatingDto createRatingDto;

    @BeforeEach
    void setUp() {
        // Create mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstname("Test");
        mockUser.setLastname("User");
        mockUser.setUsername("test@example.com");

        // Create mock product
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");

        // Create mock rating
        mockRating = Rating.builder()
                .id(1L)
                .comment("Great product")
                .star(5)
                .build();
        mockRating.setUser(mockUser);
        mockRating.setProduct(mockProduct);
        mockRating.setCreatedOn(ZonedDateTime.now());

        // Create mock user details
        mockUserDetails = AppUserDetails.builder().id(1L).username("test@example.com").build();


        // Create mock rating DTO
        mockRatingDto = new RatingDto(
                1L,
                "Great product", 
                5, 
                ZonedDateTime.now(),
                1L, 
                "Test", 
                "User"
        );

        // Create list of rating DTOs
        ratingDtoList = List.of(mockRatingDto);

        // Create mock create rating DTO
        createRatingDto = new CreateRatingDto(1L, 5, "Great product");
    }

    @Test
    void getRatings_ShouldReturnPaginationResponseWithRatingDtos() {
        // Arrange
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RatingDto> ratingPage = new PageImpl<>(ratingDtoList, pageable, 1);

        when(ratingRepository.findAllByProductId(eq(productId), eq(RatingDto.class), eq(pageable)))
                .thenReturn(ratingPage);

        // Act
        PaginationResponse<RatingDto> result = ratingService.getRatings(productId, pageable);

        // Assert
        assertEquals(0, result.getPage());
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getData().size());
        
        RatingDto returnedDto = result.getData().get(0);
        assertEquals(mockRatingDto.getId(), returnedDto.getId());
        assertEquals(mockRatingDto.getComment(), returnedDto.getComment());
        assertEquals(mockRatingDto.getStar(), returnedDto.getStar());
        assertEquals(mockRatingDto.getUser().getId(), returnedDto.getUser().getId());
        assertEquals(mockRatingDto.getUser().getFirstname(), returnedDto.getUser().getFirstname());
        assertEquals(mockRatingDto.getUser().getLastname(), returnedDto.getUser().getLastname());
        
        verify(ratingRepository).findAllByProductId(productId, RatingDto.class, pageable);
    }

    @Test
    void getRatings_WithEmptyResult_ShouldReturnEmptyPaginationResponse() {
        // Arrange
        Long productId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<RatingDto> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(ratingRepository.findAllByProductId(eq(productId), eq(RatingDto.class), eq(pageable)))
                .thenReturn(emptyPage);

        // Act
        PaginationResponse<RatingDto> result = ratingService.getRatings(productId, pageable);

        // Assert
        assertEquals(0, result.getPage());
        assertEquals(0L, result.getTotal());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getData().isEmpty());
        
        verify(ratingRepository).findAllByProductId(productId, RatingDto.class, pageable);
    }

    @Test
    void createRating_ShouldSaveAndReturnNewRating() {
        // Arrange
        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        when(productRepository.getReferenceById(1L)).thenReturn(mockProduct);
        
        Rating expectedRating = Rating.builder()
                .comment(createRatingDto.comment())
                .star(createRatingDto.star())
                .build();
        expectedRating.setUser(mockUser);
        expectedRating.setProduct(mockProduct);
        
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating savedRating = invocation.getArgument(0);
            savedRating.setId(1L);
            savedRating.setCreatedOn(ZonedDateTime.now());
            return savedRating;
        });

        // Act
        Rating result = ratingService.createRating(createRatingDto, mockUserDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(createRatingDto.comment(), result.getComment());
        assertEquals(createRatingDto.star(), result.getStar());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockProduct, result.getProduct());
        assertNotNull(result.getCreatedOn());
        
        verify(userRepository).getReferenceById(1L);
        verify(productRepository).getReferenceById(1L);
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void createRating_WithNullComment_ShouldSaveRatingWithNullComment() {
        // Arrange
        CreateRatingDto ratingDtoWithNullComment = new CreateRatingDto(1L, 4, null);
        
        when(userRepository.getReferenceById(1L)).thenReturn(mockUser);
        when(productRepository.getReferenceById(1L)).thenReturn(mockProduct);
        
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating savedRating = invocation.getArgument(0);
            savedRating.setId(1L);
            savedRating.setCreatedOn(ZonedDateTime.now());
            return savedRating;
        });

        // Act
        Rating result = ratingService.createRating(ratingDtoWithNullComment, mockUserDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertNull(result.getComment());
        assertEquals(ratingDtoWithNullComment.star(), result.getStar());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockProduct, result.getProduct());
        
        verify(userRepository).getReferenceById(1L);
        verify(productRepository).getReferenceById(1L);
        verify(ratingRepository).save(any(Rating.class));
    }
}
