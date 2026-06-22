package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import jp.systemengineeya.bookreview.api.entity.Review;
import jp.systemengineeya.bookreview.generated.model.ReviewRequest;
import jp.systemengineeya.bookreview.generated.model.ReviewResponse;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewDtoMapper {
    ReviewResponse toDto(Review entity);
    Review toEntity(ReviewRequest dto);
    List<ReviewResponse> toDtoList(List<Review> entities);
}
