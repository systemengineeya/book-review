package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
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
        ReviewDto createdReview = reviewService.createReview(bookId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews(@PathVariable Long bookId) {
        List<ReviewDto> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        ReviewDto review = reviewService.getReviewById(bookId, reviewId);
        return ResponseEntity.ok(review);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId,
            @RequestBody ReviewDto updatedReview) {
        ReviewDto review = reviewService.updateReview(bookId, reviewId, updatedReview);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        reviewService.deleteReview(bookId, reviewId);
        return ResponseEntity.noContent().build();
    }
}