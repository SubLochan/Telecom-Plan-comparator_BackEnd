package com.telecom.service;

import com.telecom.model.dto.ReviewDto;
import com.telecom.model.entity.Plan;
import com.telecom.model.entity.Review;
import com.telecom.model.entity.User;
import com.telecom.repository.PlanRepository;
import com.telecom.repository.ReviewRepository;
import com.telecom.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewDto.Response addReview(Long planId, String username, ReviewDto.CreateRequest request) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found: " + planId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        if (reviewRepository.existsByPlanIdAndUserId(planId, user.getId())) {
            throw new IllegalStateException("You have already reviewed this plan");
        }

        Review review = Review.builder()
                .plan(plan)
                .user(user)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    public Page<ReviewDto.Response> getReviewsByPlan(Long planId, int page, int size) {
        if (!planRepository.existsById(planId)) {
            throw new EntityNotFoundException("Plan not found: " + planId);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByPlanId(planId, pageable).map(this::toResponse);
    }

    public Page<ReviewDto.Response> getReviewsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Transactional
    public void deleteReview(Long reviewId, String username) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found: " + reviewId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r == User.Role.ROLE_ADMIN);

        if (!review.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new AccessDeniedException("Not authorized to delete this review");
        }

        reviewRepository.delete(review);
    }

    private ReviewDto.Response toResponse(Review review) {
        return ReviewDto.Response.builder()
                .id(review.getId())
                .planId(review.getPlan().getId())
                .planName(review.getPlan().getName())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
