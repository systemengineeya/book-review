package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.request.BookRequest;
import jp.systemengineeya.bookreview.api.dto.response.BookResponse;
import jp.systemengineeya.bookreview.api.dto.result.BookResult;
import jp.systemengineeya.bookreview.api.mapper.dto.BookResponseMapper;
import jp.systemengineeya.bookreview.api.service.BookService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final BookResponseMapper bookResponseMapper;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(required = false) List<MultipartFile> images) throws IOException {

        BookRequest book = BookRequest.builder()
                .title(title)
                .author(author)
                .build();
        BookResult createdBookResult = bookService.createBook(book, images);

        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponseMapper.toResponse(createdBookResult));
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooks() {
        List<BookResult> bookResults = bookService.getAllBooks();
        return ResponseEntity.ok(bookResponseMapper.toResponseList(bookResults));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookResult bookResult = bookService.getBookById(bookId);
        return ResponseEntity.ok(bookResponseMapper.toResponse(bookResult));
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId, @RequestBody BookRequest updatedBook) {
        BookResult bookResult = bookService.updateBook(bookId, updatedBook);
        return ResponseEntity.ok(bookResponseMapper.toResponse(bookResult));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}