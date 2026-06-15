package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.result.BookResult;
import jp.systemengineeya.bookreview.api.mapper.dto.BookResponseMapper;
import jp.systemengineeya.bookreview.api.service.BookService;
import jp.systemengineeya.bookreview.generated.api.BookControllerApi;
import jp.systemengineeya.bookreview.generated.model.BookRequest;
import jp.systemengineeya.bookreview.generated.model.BookResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController implements BookControllerApi{

    private final BookService bookService;
    private final BookResponseMapper bookResponseMapper;

    @Override
    public ResponseEntity<BookResponse> createBook(
            String title,
            String author,
            List<MultipartFile> images) {
        BookRequest book = new BookRequest();
        book.setAuthor(author);
        book.setTitle(title);
        BookResult createdBookResult = bookService.createBook(book, images);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseMapper.toResponse(createdBookResult));
    }

    @Override
    public ResponseEntity<List<BookResponse>> getBooks() {
        List<BookResult> bookResults = bookService.getAllBooks();
        return ResponseEntity.ok(bookResponseMapper.toResponseList(bookResults));
    }

    @Override
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookResult bookResult = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookResponseMapper.toResponse(bookResult));
    }

    @Override
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId, @RequestBody BookRequest updatedBook) {
        BookResult bookResult = bookService.updateBook(bookId, updatedBook);
        return ResponseEntity.ok(bookResponseMapper.toResponse(bookResult));
    }

    @Override
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}