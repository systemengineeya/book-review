package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.dto.response.BookImageResponse;
import jp.systemengineeya.bookreview.api.mapper.dto.BookImageResponseMapper;
import jp.systemengineeya.bookreview.api.service.BookImageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books/{bookId}/images")
@RequiredArgsConstructor
public class BookImageController {

    private final BookImageService bookImageService;
    private final BookImageResponseMapper bookImageResponseMapper;
    @PostMapping
    public ResponseEntity<BookImageResponse> upload(
            @PathVariable Long bookId,
            @RequestParam MultipartFile image) {

        return ResponseEntity.ok(
                bookImageResponseMapper.toResponse(bookImageService.upload(bookId, image)));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long bookId,
            @PathVariable Long imageId) {

        bookImageService.delete(bookId, imageId);

        return ResponseEntity.noContent().build();
    }
}
