package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ReviewResponse> createReview(Long bookId, ReviewRequest review) {
        ReviewResponse createdReview = reviewService.createReview(bookId, review);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @Override
    public ResponseEntity<List<ReviewResponse>> getReviews(Long bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @Override
    public ResponseEntity<ReviewResponse> getReview(Long bookId, Long reviewId) {
        ReviewResponse review = reviewService.getReviewById(bookId, reviewId);
        return ResponseEntity.ok(review);
    }

    @Override
    public ResponseEntity<ReviewResponse> updateReview(Long bookId, Long reviewId,
            @RequestBody ReviewRequest updatedReview) {
        ReviewResponse review = reviewService.updateReview(bookId, reviewId, updatedReview);
        return ResponseEntity.ok(review);
    }

    @Override
    public ResponseEntity<Void> deleteReview(Long bookId, Long reviewId) {
        reviewService.deleteReview(bookId, reviewId);
        return ResponseEntity.noContent().build();
    }
}