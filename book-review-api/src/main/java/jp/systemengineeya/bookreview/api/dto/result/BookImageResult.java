package jp.systemengineeya.bookreview.api.dto.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookImageResult {
    private Long id;
    private String imageUrl;
}
