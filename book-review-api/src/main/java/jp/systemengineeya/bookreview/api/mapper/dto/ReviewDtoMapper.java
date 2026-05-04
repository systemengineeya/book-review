package jp.systemengineeya.bookreview.api.mapper.dto;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import jp.systemengineeya.bookreview.api.dto.ReviewDto;
import jp.systemengineeya.bookreview.api.entity.Review;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewDtoMapper {
    ReviewDto toDto(Review entity);
    Review toEntity(ReviewDto dto);
    List<ReviewDto> toDtoList(List<Review> entities);
}
