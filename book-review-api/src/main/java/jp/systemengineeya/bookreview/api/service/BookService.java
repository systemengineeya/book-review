package jp.systemengineeya.bookreview.api.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.request.BookRequest;
import jp.systemengineeya.bookreview.api.dto.result.BookImageResult;
import jp.systemengineeya.bookreview.api.dto.result.BookResult;
import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.api.entity.BookExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.dto.BookDtoMapper;
import jp.systemengineeya.bookreview.api.mapper.dto.BookResultMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookImageMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookMapper bookMapper;
    private final BookDtoMapper bookDtoMapper;
    private final BookImageService bookImageService;
    private final BookResultMapper bookResultMapper;

    public BookResult createBook(BookRequest book, List<MultipartFile> images) {
        Book entity = bookDtoMapper.toEntity(book);
        bookMapper.insertSelective(entity);
        
        List<BookImageResult> bookImageResults = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                BookImageResult bookImageResult = bookImageService.upload(entity.getId(), image);
                bookImageResults.add(bookImageResult);
            }
        }
        return bookResultMapper.toResult(entity, bookImageResults);
    }

    public BookResult getBookById(Long bookId) {
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            throw new NotFoundException("Book", bookId);
        }
        List<BookImageResult> bookImageResults = bookImageService.findByBookId(bookId);
        return bookResultMapper.toResult(book, bookImageResults);
    }

    public List<BookResult> getAllBooks() {
        List<Book> books = bookMapper.selectByExample(new BookExample());
        List<BookResult> bookResults = new ArrayList<>();
        for (Book book : books) {
            List<BookImageResult> bookImageResults = bookImageService.findByBookId(book.getId());
            bookResults.add(bookResultMapper.toResult(book, bookImageResults));
        }
        return bookResults;
    }

    public BookResult updateBook(Long bookId, BookRequest updatedBook) {
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
        List<BookImageResult> bookImageResults = bookImageService.findByBookId(bookId);
        return bookResultMapper.toResult(
                book,
                bookImageResults);
    }

    public void deleteBook(Long bookId) {
        bookImageService.deleteAllByBookId(bookId);
        int count = bookMapper.deleteByPrimaryKey(bookId);
        if (count == 0) {
            throw new NotFoundException("Book", bookId);
        }
    }
}
