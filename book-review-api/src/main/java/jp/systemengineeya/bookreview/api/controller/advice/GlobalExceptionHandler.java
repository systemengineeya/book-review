package jp.systemengineeya.bookreview.api.controller.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handle(NotFoundException e) {
        log.info(e.getMessage(), e);
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handle(RuntimeException e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(500)
                .build();
    }
}
