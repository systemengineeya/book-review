package jp.systemsengineeya.bookreview.api.controller;

import jp.systemsengineeya.bookreview.api.dto.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books/{bookId}/reviews")
public class ReviewController {

    // Mock data
    private List<Review> reviews = new ArrayList<>();
    private Long nextId = 1L;

    @PostMapping
    public ResponseEntity<Review> createReview(@PathVariable Long bookId, @RequestBody Review review) {
        review.setId(nextId++);
        review.setBookId(bookId);
        reviews.add(review);
        return ResponseEntity.ok(review);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long bookId) {
        List<Review> bookReviews = reviews.stream().filter(r -> r.getBookId().equals(bookId)).collect(Collectors.toList());
        return ResponseEntity.ok(bookReviews);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        Review review = reviews.stream().filter(r -> r.getId().equals(reviewId) && r.getBookId().equals(bookId)).findFirst().orElse(null);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(review);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable Long bookId, @PathVariable Long reviewId, @RequestBody Review updatedReview) {
        Review review = reviews.stream().filter(r -> r.getId().equals(reviewId) && r.getBookId().equals(bookId)).findFirst().orElse(null);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        review.setContent(updatedReview.getContent());
        review.setRating(updatedReview.getRating());
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long bookId, @PathVariable Long reviewId) {
        reviews.removeIf(r -> r.getId().equals(reviewId) && r.getBookId().equals(bookId));
        return ResponseEntity.noContent().build();
    }
}