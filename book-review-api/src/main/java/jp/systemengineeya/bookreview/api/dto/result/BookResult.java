package jp.systemengineeya.bookreview.api.dto.result;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResult {

    private Long id;

    private String title;

    private String author;

    private List<BookImageResult> images;
}