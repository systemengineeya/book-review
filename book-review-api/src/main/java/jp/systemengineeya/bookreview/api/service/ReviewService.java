package jp.systemengineeya.bookreview.api.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.systemengineeya.bookreview.api.entity.Review;
import jp.systemengineeya.bookreview.api.entity.ReviewExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.dto.ReviewDtoMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.ReviewMapper;
import jp.systemengineeya.bookreview.generated.model.ReviewRequest;
import jp.systemengineeya.bookreview.generated.model.ReviewResponse;

@Service
@Transactional
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookMapper bookMapper;
    private final ReviewDtoMapper reviewDtoMapper;

    public ReviewService(ReviewMapper reviewMapper, BookMapper bookMapper, ReviewDtoMapper reviewDtoMapper) {
        this.reviewMapper = reviewMapper;
        this.bookMapper = bookMapper;
        this.reviewDtoMapper = reviewDtoMapper;
    }

    public ReviewResponse createReview(Long bookId, ReviewRequest review) {
        if (bookMapper.selectByPrimaryKey(bookId) == null) {
            throw new NotFoundException("Book", bookId);
        }

        Review entity = reviewDtoMapper.toEntity(review);
        entity.setBookId(bookId);
        reviewMapper.insertSelective(entity);
        return reviewDtoMapper.toDto(entity);
    }

    public ReviewResponse getReviewById(Long bookId, Long reviewId) {
        Review entity = reviewMapper.selectByPrimaryKey(reviewId);
        if (entity == null || !entity.getBookId().equals(bookId)) {
            throw new NotFoundException("Review", reviewId);
        }
        return reviewDtoMapper.toDto(entity);
    }

    public List<ReviewResponse> getReviewsByBookId(Long bookId) {
        if (bookMapper.selectByPrimaryKey(bookId) == null) {
            throw new NotFoundException("Book", bookId);
        }

        ReviewExample example = new ReviewExample();
        example.createCriteria().andBookIdEqualTo(bookId);
        return reviewMapper.selectByExample(example).stream()
                .map(reviewDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReviewResponse updateReview(Long bookId, Long reviewId, ReviewRequest updatedReview) {
        Review entity = reviewMapper.selectByPrimaryKey(reviewId);
        if (entity == null || !entity.getBookId().equals(bookId)) {
            throw new NotFoundException("Review", reviewId);
        }

        if (updatedReview.getContent() != null) {
            entity.setContent(updatedReview.getContent());
        }
        if (updatedReview.getRating() != null) {
            entity.setRating(updatedReview.getRating());
        }

        reviewMapper.updateByPrimaryKeySelective(entity);
        return reviewDtoMapper.toDto(entity);
    }

    public void deleteReview(Long bookId, Long reviewId) {
        Review entity = reviewMapper.selectByPrimaryKey(reviewId);
        if (entity == null || !entity.getBookId().equals(bookId)) {
            throw new NotFoundException("Review", reviewId);
        }
        reviewMapper.deleteByPrimaryKey(reviewId);
    }
}
