package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.generated.model.BookRequest;
import jp.systemengineeya.bookreview.generated.model.BookResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookDtoMapper {
    BookResponse toDto(Book entity);
    Book toEntity(BookRequest dto);
    List<BookResponse> toDtoList(List<Book> entities);
}
