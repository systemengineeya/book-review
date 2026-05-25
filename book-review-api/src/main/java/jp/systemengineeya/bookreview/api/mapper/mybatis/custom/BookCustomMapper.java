package jp.systemengineeya.bookreview.api.mapper.mybatis.custom;

import java.util.List;

import jp.systemengineeya.bookreview.api.entity.custom.BookWithImages;

public interface BookCustomMapper {
    List<BookWithImages> selectBookWithImages(Long id);
}
