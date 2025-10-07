package ganadii.hanjum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FlowerCardDtos {

    public record CreateCardRequest(
            @Schema(description = "메인 꽃 ID", example = "5 // 1~9까지 가능")
            @NotNull Long mainFlowerId,

            @Schema(description = "카드 제목", example = "가나디 최고 :)")
            @Size(max = 100) String title,

            @Schema(description = "꽃말 (선택사항)", example = "당신을 사랑합니다")
            @Size(max = 500) String floriography,

            @Schema(description = "대상",
                    example = "연인",
                    allowableValues = {"TEACHER", "LOVER", "SELF", "FAMILY", "FRIEND", "COLLEAGUE", "스승", "연인", "본인", "가족", "친구", "동료"})
            @NotBlank String whoType,

            @Schema(description = "상황/시기",
                    example = "고백",
                    allowableValues = {"BANQUET", "CONFESSION", "BIRTHDAY", "ANNIVERSARY", "MEMORIAL", "OPENING", "연회", "고백", "생일", "기념일", "추모", "개업"})
            @NotBlank String whenType,

            @Schema(description = "감정 (1~5개 선택 가능)",
                    example = "[\"사랑\", \"설렘\"]",
                    allowableValues = {"EXCITEMENT", "LOVE", "GRATITUDE", "COURAGE", "ENCOURAGEMENT", "EXPECTATION", "CELEBRATION", "RESPECT", "FRIENDSHIP", "RESOLUTION", "APOLOGY", "MOURNING", "설렘", "사랑", "고마음", "용기", "격려", "기대", "축하", "존경", "우정", "다짐", "사과", "애도"})
            @NotNull @Size(min = 1, max = 5) List<@NotBlank String> emotionTypes,

            @Schema(description = "꽃다발 크기",
                    example = "M",
                    allowableValues = {"S", "M", "L"})
            String bouquetSize,

            @Schema(description = "포장 타입",
                    example = "컬러 종이",
                    allowableValues = {"KRAFT_PAPER", "COLOR_PAPER", "CLEAR_VINYL", "크래프트지", "컬러 종이", "투명 비닐"})
            String wrappingType,

            @Schema(description = "가격 (원)", example = "10000")
            Integer price
    ) {}

    public record CardResponse(
            Long cardId,
            String title,
            String imageUrl,
            String imageSource,
            @JsonInclude(JsonInclude.Include.NON_NULL)
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
