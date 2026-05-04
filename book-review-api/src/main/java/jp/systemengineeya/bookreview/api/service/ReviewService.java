package jp.systemengineeya.bookreview.api.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import jp.systemengineeya.bookreview.api.dto.ReviewDto;
import jp.systemengineeya.bookreview.api.entity.Review;
import jp.systemengineeya.bookreview.api.entity.ReviewExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.dto.ReviewDtoMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.ReviewMapper;

@Service
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final BookMapper bookMapper;
    private final ReviewDtoMapper reviewDtoMapper;

    public ReviewService(ReviewMapper reviewMapper, BookMapper bookMapper, ReviewDtoMapper reviewDtoMapper) {
        this.reviewMapper = reviewMapper;
        this.bookMapper = bookMapper;
        this.reviewDtoMapper = reviewDtoMapper;
    }

    public ReviewDto createReview(Long bookId, ReviewDto review) {
        if (bookMapper.selectByPrimaryKey(bookId) == null) {
            throw new NotFoundException("Book", bookId);
        }

        Review entity = reviewDtoMapper.toEntity(review);
        entity.setBookId(bookId);
        reviewMapper.insertSelective(entity);
        return reviewDtoMapper.toDto(entity);
    }

    public ReviewDto getReviewById(Long bookId, Long reviewId) {
        Review entity = reviewMapper.selectByPrimaryKey(reviewId);
        if (entity == null || !entity.getBookId().equals(bookId)) {
            throw new NotFoundException("Review", reviewId);
        }
        return reviewDtoMapper.toDto(entity);
    }

    public List<ReviewDto> getReviewsByBookId(Long bookId) {
        if (bookMapper.selectByPrimaryKey(bookId) == null) {
            throw new NotFoundException("Book", bookId);
        }

        ReviewExample example = new ReviewExample();
        example.createCriteria().andBookIdEqualTo(bookId);
        return reviewMapper.selectByExample(example).stream()
                .map(reviewDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public ReviewDto updateReview(Long bookId, Long reviewId, ReviewDto updatedReview) {
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
