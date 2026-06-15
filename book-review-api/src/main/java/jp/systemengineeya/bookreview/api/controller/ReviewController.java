package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jp.systemengineeya.bookreview.api.service.ReviewService;
import jp.systemengineeya.bookreview.generated.api.ReviewControllerApi;
import jp.systemengineeya.bookreview.generated.model.ReviewRequest;
import jp.systemengineeya.bookreview.generated.model.ReviewResponse;

import java.util.List;

@RestController
public class ReviewController implements ReviewControllerApi {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Override
    public ResponseEntity<ReviewResponse> createReview(@PathVariable Long bookId, @RequestBody ReviewRequest review) {
        ReviewResponse createdReview = reviewService.createReview(bookId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @Override
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        ReviewResponse review = reviewService.getReviewById(bookId, reviewId);
        return ResponseEntity.ok(review);
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId,
            @RequestBody ReviewRequest updatedReview) {
        ReviewResponse review = reviewService.updateReview(bookId, reviewId, updatedReview);
        return ResponseEntity.ok(review);
    }

    @Override
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        reviewService.deleteReview(bookId, reviewId);
        return ResponseEntity.noContent().build();
    }
}