package jp.systemengineeya.bookreview.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookImageResponse {
    private Long id;
    private String imageUrl;
}
