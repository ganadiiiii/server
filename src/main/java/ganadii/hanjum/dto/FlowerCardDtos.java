package ganadii.hanjum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FlowerCardDtos {

    public record CreateCardRequest(
            @NotNull Long mainFlowerId,
            @Size(max = 100) String title,
            @Size(max = 500) String floriography,
            @NotBlank String whoType,
            @NotBlank String whenType,
            @NotNull @Size(min = 1, max = 5) List<@NotBlank String> emotionTypes,
            String bouquetSize,
            String wrappingType,
            Integer price
    ) {}

    public record CardResponse(
            Long cardId,
            String title,
            String imageUrl,
            String imageSource,
            String floriography,
            String whoType,
            String whoLabel,
            String whenType,
            String whenLabel,
            List<String> emotionTypes,
            List<String> emotionLabels,
            String bouquetSize,
            String bouquetLabel,
            String wrappingType,
            String wrappingLabel,
            Integer price,
            Long designAssetId,
            FlowerSummary mainFlower
    ) {}

    public record CardPageResponse(
            List<CardResponse> cards,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}

    public record FlowerSummary(
            Long flowerId,
            String koreanName,
            String englishName,
            String imageUrl
    ) {}
}
