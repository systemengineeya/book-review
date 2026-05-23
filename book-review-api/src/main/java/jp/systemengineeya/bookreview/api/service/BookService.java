package jp.systemengineeya.bookreview.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.request.BookRequest;
import jp.systemengineeya.bookreview.api.dto.response.BookResponse;
import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.api.entity.BookExample;
import jp.systemengineeya.bookreview.api.entity.BookImage;
import jp.systemengineeya.bookreview.api.entity.BookImageExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.dto.BookDtoMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookImageMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;
import jp.systemengineeya.bookreview.api.service.infrastructure.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookMapper bookMapper;
    private final BookDtoMapper bookDtoMapper;
    private final S3Service s3Service;
    private final BookImageMapper bookImageMapper;

    public BookResponse createBook(BookRequest book, List<MultipartFile> images) {
        List<String> keys = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                try {
                    String key = s3Service.upload(image);
                    keys.add(key);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
        }
        Book entity = bookDtoMapper.toEntity(book);
        bookMapper.insertSelective(entity);
        for (String key : keys) {
            BookImage bookImage = new BookImage();
            bookImage.setBookId(entity.getId());
            bookImage.setS3Key(key);
            bookImageMapper.insert(bookImage);
        }
        // TODO: 画像をbookに含める
        return bookDtoMapper.toDto(entity);
    }

    public BookResponse getBookById(Long bookId) {
        Book entity = bookMapper.selectByPrimaryKey(bookId);
        if (entity == null) {
            throw new NotFoundException("Book", bookId);
        }
        return bookDtoMapper.toDto(entity);
    }

    public List<BookResponse> getAllBooks() {
        BookExample example = new BookExample();
        return bookMapper.selectByExample(example).stream()
                .map(bookDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookResponse updateBook(Long bookId, BookRequest updatedBook) {
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            throw new NotFoundException("Book", bookId);
        }

        if (updatedBook.getTitle() != null) {
            book.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            book.setAuthor(updatedBook.getAuthor());
        }

        bookMapper.updateByPrimaryKeySelective(book);
        return bookDtoMapper.toDto(book);
    }

    public void deleteBook(Long bookId) {
        BookImageExample bookImageExample = new BookImageExample();
        bookImageExample.createCriteria().andBookIdEqualTo(bookId);
        List<BookImage> images = bookImageMapper.selectByExample(bookImageExample);

        for (BookImage image : images) {
            try {
                s3Service.delete(image.getS3Key());
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete image from S3: " + image.getS3Key(), e);
            }
        }
        int count = bookMapper.deleteByPrimaryKey(bookId);
        if (count == 0) {
            throw new NotFoundException("Book", bookId);
        }
    }
}
