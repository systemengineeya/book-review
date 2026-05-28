package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;

import org.mapstruct.Mapper;

import jp.systemengineeya.bookreview.api.dto.response.BookResponse;
import jp.systemengineeya.bookreview.api.dto.result.BookResult;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {

    BookResponse toResponse(BookResult result);
    List<BookResponse> toResponseList(List<BookResult> results);
}