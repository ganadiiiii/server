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
            @NotBlank String emotionType,
            String bouquetSize,
            Integer price,
            List<@Size(max = 20) String> backgroundColors
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
            String emotionType,
            String emotionLabel,
            String bouquetSize,
            String bouquetLabel,
            Integer price,
            List<String> backgroundColors,
            Long designAssetId,
            FlowerSummary mainFlower
    ) {}

    public record FlowerSummary(
            Long flowerId,
            String koreanName,
            String englishName,
            String imageUrl
    ) {}
}
