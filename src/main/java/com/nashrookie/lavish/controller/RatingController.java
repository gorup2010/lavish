package com.nashrookie.lavish.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nashrookie.lavish.dto.request.CreateRatingDto;
import com.nashrookie.lavish.dto.response.PaginationResponse;
import com.nashrookie.lavish.dto.response.RatingDto;
import com.nashrookie.lavish.entity.AppUserDetails;
import com.nashrookie.lavish.entity.Rating;
import com.nashrookie.lavish.service.RatingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    @GetMapping()
    public ResponseEntity<PaginationResponse<RatingDto>> getRatings(@RequestParam Long productId, @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        Sort sort = Sort.by("createdOn").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ratingService.getRatings(productId, pageable));
    }

    @PostMapping()
    public ResponseEntity<Rating> createRating(@Valid @RequestBody CreateRatingDto rating, Authentication auth) {
        return ResponseEntity.ok(ratingService.createRating(rating, (AppUserDetails) auth.getPrincipal()));
    }
}
