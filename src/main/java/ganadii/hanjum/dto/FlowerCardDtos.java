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

            @Schema(description = "꽃말", example = "당신을 사랑합니다 (option)")
            @Size(max = 500) String floriography,

            @Schema(description = "대상",
                    example = "TEACHER | LOVER | SELF | FAMILY | FRIEND | COLLEAGUE | 스승 | 연인 | 본인 | 가족 | 친구 | 동료")
            @NotBlank String whoType,

            @Schema(description = "상황/시기",
                    example = "BANQUET | CONFESSION | BIRTHDAY | ANNIVERSARY | MEMORIAL | OPENING | 연회 | 고백 | 생일 | 기념일 | 추모 | 개업")
            @NotBlank String whenType,

            @Schema(description = "감정 (1~9개 선택 가능)",
                    example = "EXCITEMENT | LOVE | GRATITUDE | COURAGE | ENCOURAGEMENT | EXPECTATION | CELEBRATION | RESPECT | FRIENDSHIP | RESOLUTION | APOLOGY | MOURNING | 설렘 | 사랑 | 고마음 | 용기 | 격려 | 기대 | 축하 | 존경 | 우정 | 다짐 | 사과 | 애도")
            @NotNull @Size(min = 1, max = 9) List<@NotBlank String> emotionTypes,

            @Schema(description = "꽃다발 크기",
                    example = "S | M | L")
            String bouquetSize,

            @Schema(description = "포장 타입",
                    example = "KRAFT_PAPER | COLOR_PAPER | CLEAR_VINYL | 크래프트지 | 컬러 종이 | 투명 비닐")
            String wrappingType,

            @Schema(description = "가격 (원)", example = "10000")
            Integer price
    ) {}

    public record CardResponse(
            @Schema(description = "카드 ID")
            Long cardId,

            @Schema(description = "카드 제목")
            String title,

            @Schema(description = "카드 이미지 URL")
            String imageUrl,

            @Schema(description = "이미지 소스 (PRESET | GENERATED)")
            String imageSource,

            @Schema(description = "꽃말")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String floriography,

            @Schema(description = "대상 (TEACHER | LOVER | SELF | FAMILY | FRIEND | COLLEAGUE)")
            String whoType,

            @Schema(description = "대상 한글명")
            String whoLabel,

            @Schema(description = "상황/시기 (BANQUET | CONFESSION | BIRTHDAY | ANNIVERSARY | MEMORIAL | OPENING)")
            String whenType,

            @Schema(description = "상황/시기 한글명")
            String whenLabel,

            @Schema(description = "감정 목록 (EXCITEMENT | LOVE | GRATITUDE | COURAGE | ENCOURAGEMENT | EXPECTATION | CELEBRATION | RESPECT | FRIENDSHIP | RESOLUTION | APOLOGY | MOURNING)")
            List<String> emotionTypes,

            @Schema(description = "감정 한글명 목록")
            List<String> emotionLabels,

            @Schema(description = "꽃다발 크기 (S | M | L)")
            String bouquetSize,

            @Schema(description = "꽃다발 크기 한글명")
            String bouquetLabel,

            @Schema(description = "포장 타입 (KRAFT_PAPER | COLOR_PAPER | CLEAR_VINYL)")
            String wrappingType,

            @Schema(description = "포장 타입 한글명")
            String wrappingLabel,

            @Schema(description = "가격 (원)")
            Integer price,

            @Schema(description = "디자인 에셋 ID")
            Long designAssetId,

            @Schema(description = "배경 그라데이션 색상 (Hex color codes)", example = "[\"#FFE5E5\", \"#FFF0F0\"]")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            List<String> backgroundColors,

            @Schema(description = "메인 꽃 정보")
            FlowerSummary mainFlower,

            @Schema(description = "서브 꽃 정보")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            FlowerSummary subFlower
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
