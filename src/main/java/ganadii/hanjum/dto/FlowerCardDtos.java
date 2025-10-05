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
            @NotNull @Size(min = 1, max = 9) List<@NotBlank String> whenTypes,
            @NotBlank String emotionType,
            String bouquetSize,
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
            List<String> whenTypes,
            List<String> whenLabels,
            String emotionType,
            String emotionLabel,
            String bouquetSize,
            String bouquetLabel,
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
