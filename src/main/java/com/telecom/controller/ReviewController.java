package com.telecom.controller;

import com.telecom.model.dto.ApiResponse;
import com.telecom.model.dto.ReviewDto;
import com.telecom.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Plan review and rating APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/plans/{planId}/reviews")
    @Operation(summary = "Get all reviews for a plan")
    public ResponseEntity<ApiResponse<Page<ReviewDto.Response>>> getReviewsByPlan(
            @PathVariable Long planId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getReviewsByPlan(planId, page, size)));
    }

    @PostMapping("/plans/{planId}/reviews")
    @Operation(summary = "Add a review for a plan (authenticated users)")
    public ResponseEntity<ApiResponse<ReviewDto.Response>> addReview(
            @PathVariable Long planId,
            @Valid @RequestBody ReviewDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ReviewDto.Response review = reviewService.addReview(planId, userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review added successfully", review));
    }

    @GetMapping("/users/{userId}/reviews")
    @Operation(summary = "Get all reviews by a specific user")
    public ResponseEntity<ApiResponse<Page<ReviewDto.Response>>> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                reviewService.getReviewsByUser(userId, page, size)));
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "Delete a review (owner or admin)")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        reviewService.deleteReview(reviewId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }
}
