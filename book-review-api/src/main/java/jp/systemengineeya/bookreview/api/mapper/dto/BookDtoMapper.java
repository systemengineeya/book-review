package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import jp.systemengineeya.bookreview.api.dto.BookDto;
import jp.systemengineeya.bookreview.api.entity.Book;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookDtoMapper {
    BookDto toDto(Book entity);
    Book toEntity(BookDto dto);
    List<BookDto> toDtoList(List<Book> entities);
}
