package jp.systemengineeya.bookreview.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.result.BookImageResult;
import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.api.entity.BookImage;
import jp.systemengineeya.bookreview.api.entity.BookImageExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookImageMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;
import jp.systemengineeya.bookreview.api.service.infrastructure.S3Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookImageService {
    private final BookMapper bookMapper;
    private final BookImageMapper bookImageMapper;
    private final S3Service s3Service;

    public BookImageResult upload(Long bookId, MultipartFile image) {

        Book book = bookMapper.selectByPrimaryKey(bookId);

        if (book == null) {
            throw new NotFoundException("Book", bookId);
        }
        String s3Key;
        try {
            s3Key = s3Service.upload(image);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image", e);
        }

        BookImage bookImage = new BookImage();
        bookImage.setBookId(bookId);
        bookImage.setS3Key(s3Key);

        bookImageMapper.insert(bookImage);

        return BookImageResult.builder()
                .id(bookImage.getId())
                .imageUrl(s3Service.generatePresignedUrl(s3Key))
                .build();
    }

    public void delete(Long bookId, Long imageId) {
        BookImage bookImage = bookImageMapper.selectByPrimaryKey(imageId);
        if (bookImage == null
                || !bookImage.getBookId().equals(bookId)) {
            throw new NotFoundException("BookImage", imageId);
        }
        s3Service.delete(bookImage.getS3Key());
        bookImageMapper.deleteByPrimaryKey(imageId);
    }

    public List<BookImageResult> findByBookId(
            Long bookId) {
        validateBookExists(bookId);
        BookImageExample example = new BookImageExample();
        example.createCriteria().andBookIdEqualTo(bookId);
        List<BookImage> images = bookImageMapper.selectByExample(example);
        return images.stream()
                .map(this::toResult)
                .toList();
    }

    private void validateBookExists(Long bookId) {
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            throw new NotFoundException(
                    "Book",
                    bookId);
        }
    }

    private BookImageResult toResult(
            BookImage image) {
        return BookImageResult.builder()
                .id(image.getId())
                .imageUrl(
                        s3Service.generatePresignedUrl(
                                image.getS3Key()))
                .build();
    }

    public void deleteAllByBookId(Long bookId) {
        BookImageExample example = new BookImageExample();
        example.createCriteria()
                .andBookIdEqualTo(bookId);
        List<BookImage> images = bookImageMapper.selectByExample(example);
        for (BookImage image : images) {
            delete(bookId, image.getId());
        }
    }
}
