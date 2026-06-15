package jp.systemengineeya.bookreview.api.mapper.dto;

import org.mapstruct.Mapper;

import jp.systemengineeya.bookreview.api.dto.result.BookImageResult;
import jp.systemengineeya.bookreview.generated.model.BookImageResponse;

@Mapper(componentModel = "spring")
public interface BookImageResponseMapper {

    BookImageResponse toResponse(
            BookImageResult result);
}