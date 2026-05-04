package jp.systemengineeya.bookreview.api.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import jp.systemengineeya.bookreview.api.dto.BookDto;
import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.api.entity.BookExample;
import jp.systemengineeya.bookreview.api.exception.NotFoundException;
import jp.systemengineeya.bookreview.api.mapper.dto.BookDtoMapper;
import jp.systemengineeya.bookreview.api.mapper.mybatis.BookMapper;

@Service
public class BookService {

    private final BookMapper bookMapper;
    private final BookDtoMapper bookDtoMapper;

    public BookService(BookMapper bookMapper, BookDtoMapper bookDtoMapper) {
        this.bookMapper = bookMapper;
        this.bookDtoMapper = bookDtoMapper;
    }

    public BookDto createBook(BookDto book) {
        Book entity = bookDtoMapper.toEntity(book);
        bookMapper.insertSelective(entity);
        return bookDtoMapper.toDto(entity);
    }

    public BookDto getBookById(Long bookId) {
        Book entity = bookMapper.selectByPrimaryKey(bookId);
        if (entity == null) {
            throw new NotFoundException("Book", bookId);
        }
        return bookDtoMapper.toDto(entity);
    }

    public List<BookDto> getAllBooks() {
        BookExample example = new BookExample();
        return bookMapper.selectByExample(example).stream()
                .map(bookDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public BookDto updateBook(Long bookId, BookDto updatedBook) {
        Book book = bookMapper.selectByPrimaryKey(bookId);
        if (book == null) {
            throw new NotFoundException("Book", bookId);
        }

        if (updatedBook.getTitle() != null) {
            book.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getAuthor() != null) {
            book.setAuthor(updatedBook.getAuthor());
        }

        bookMapper.updateByPrimaryKeySelective(book);
        return bookDtoMapper.toDto(book);
    }

    public void deleteBook(Long bookId) {
        int count = bookMapper.deleteByPrimaryKey(bookId);
        if (count == 0) {
            throw new NotFoundException("Book", bookId);
        }
    }
}
