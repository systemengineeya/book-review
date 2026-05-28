package jp.systemengineeya.bookreview.api.mapper.dto;

import org.mapstruct.Mapper;

import jp.systemengineeya.bookreview.api.dto.response.BookImageResponse;
import jp.systemengineeya.bookreview.api.dto.result.BookImageResult;

@Mapper(componentModel = "spring")
public interface BookImageResponseMapper {

    BookImageResponse toResponse(
            BookImageResult result);
}