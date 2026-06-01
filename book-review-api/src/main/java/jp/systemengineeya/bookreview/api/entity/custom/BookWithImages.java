package jp.systemengineeya.bookreview.api.entity.custom;

import java.util.List;

import jp.systemengineeya.bookreview.api.entity.Book;
import jp.systemengineeya.bookreview.api.entity.BookImage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookWithImages extends Book {
    private List<BookImage> images;
}