package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import jp.systemengineeya.bookreview.api.dto.result.BookImageResult;
import jp.systemengineeya.bookreview.api.dto.result.BookResult;
import jp.systemengineeya.bookreview.api.entity.Book;

@Mapper(componentModel = "spring")
public interface BookResultMapper {

    @Mapping(target = "images", source = "images")
    BookResult toResult(
            Book book,
            List<BookImageResult> images);
}