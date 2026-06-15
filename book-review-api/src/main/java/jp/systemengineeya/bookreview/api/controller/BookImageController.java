package jp.systemengineeya.bookreview.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jp.systemengineeya.bookreview.api.mapper.dto.BookImageResponseMapper;
import jp.systemengineeya.bookreview.api.service.BookImageService;
import jp.systemengineeya.bookreview.generated.api.BookImageControllerApi;
import jp.systemengineeya.bookreview.generated.model.BookImageResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BookImageController implements BookImageControllerApi {

    private final BookImageService bookImageService;
    private final BookImageResponseMapper bookImageResponseMapper;

    @Override
    public ResponseEntity<BookImageResponse> upload(
            @PathVariable Long bookId,
            @RequestParam MultipartFile image) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                bookImageResponseMapper.toResponse(bookImageService.upload(bookId, image)));
    }

    @Override
    public ResponseEntity<Void> delete(
            @PathVariable Long bookId,
            @PathVariable Long imageId) {

        bookImageService.delete(bookId, imageId);

        return ResponseEntity.noContent().build();
    }
}
