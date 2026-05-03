package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jp.systemengineeya.bookreview.api.dto.ReviewDto;
import jp.systemengineeya.bookreview.api.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@PathVariable Long bookId, @RequestBody ReviewDto review) {
        try {
            ReviewDto createdReview = reviewService.createReview(bookId, review);
            return ResponseEntity.ok(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long bookId) {
        try {
            List<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId);
            return ResponseEntity.ok(reviews);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        try {
            ReviewDto review = reviewService.getReviewById(bookId, reviewId);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId, @RequestBody ReviewDto updatedReview) {
        try {
            ReviewDto review = reviewService.updateReview(bookId, reviewId, updatedReview);
            return ResponseEntity.ok(review);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        try {
            reviewService.deleteReview(bookId, reviewId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}