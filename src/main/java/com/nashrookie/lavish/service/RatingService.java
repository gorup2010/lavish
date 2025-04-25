package com.nashrookie.lavish.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nashrookie.lavish.dto.request.CreateRatingDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.RatingDto;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.Rating;
import com.nashrookie.lavish.repository.ProductRepository;
import com.nashrookie.lavish.repository.RatingRepository;
import com.nashrookie.lavish.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public PaginationResponse<RatingDto> getRatings(Long productId, Pageable pageable) {
        Page<RatingDto> products = ratingRepository.findAllByProductId(productId, RatingDto.class, pageable);

        return PaginationResponse.<RatingDto>builder()
                    .page(pageable.getPageNumber())
                    .total(products.getTotalElements())
                    .totalPages(products.getTotalPages())
                    .data(products.getContent()).build();
    }

    @Transactional
    public Rating createRating(CreateRatingDto rating, AppUserDetails user) {
        Rating newRating = Rating.builder()
                .comment(rating.comment())
                .star(rating.star())
                .build();
        newRating.setUser(userRepository.getReferenceById(user.getId()));
        newRating.setProduct(productRepository.getReferenceById(rating.productId()));
        return ratingRepository.save(newRating);
    }
}
