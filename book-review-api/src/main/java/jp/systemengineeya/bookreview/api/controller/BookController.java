package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.request.BookRequest;
import jp.systemengineeya.bookreview.api.dto.response.BookResponse;
import jp.systemengineeya.bookreview.api.service.BookService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam(required = false) List<MultipartFile> images) throws IOException {

        BookRequest book = BookRequest.builder()
                .title(title)
                .author(author)
                .build();
        BookResponse createdBook = bookService.createBook(book, images);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long bookId) {
        BookResponse book = bookService.getBookById(bookId);
        return ResponseEntity.ok(book);
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long bookId, @RequestBody BookRequest updatedBook) {
        BookResponse book = bookService.updateBook(bookId, updatedBook);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
}