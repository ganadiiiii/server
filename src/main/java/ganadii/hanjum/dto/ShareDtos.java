package ganadii.hanjum.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ShareDtos {

    public record SendShareRequest(
            @Schema(description = "받는 사람 유저 ID", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID receiverId,
            @Schema(description = "받는 사람 이름", example = "홍길동")
            @Size(max = 20) String toName,
            @Schema(description = "보내는 사람 이름", example = "김가나")
            @Size(max = 20) String fromName,
            @Schema(description = "전달 메시지", example = "생일 축하해!")
            @Size(max = 200) String note
    ) {}

    public record ShareResponse(
            @Schema(description = "공유 ID")
            Long shareId,
            @Schema(description = "카드 ID")
            Long cardId,
            @Schema(description = "받는 사람 이름")
            String toName,
            @Schema(description = "보내는 사람 이름")
            String fromName,
            @Schema(description = "전달 메시지")
            String note,
            @Schema(description = "읽음 여부")
            Boolean isRead,
            @Schema(description = "공유 일시")
            Instant sharedAt,
            @Schema(description = "보낸 사람 정보")
            SimpleUser sender,
            @Schema(description = "받는 사람 정보")
            SimpleUser receiver,
            @Schema(description = "카드 상세 정보")
            SimpleCard card
    ) {}

    public record SimpleUser(
            @Schema(description = "유저 ID")
            UUID userId,
            @Schema(description = "이름")
            String firstName,
            @Schema(description = "성")
            String lastName
    ) {}

    public record FlowerSummary(
            @Schema(description = "꽃 ID")
            Long flowerId,
            @Schema(description = "한국어 이름", example = "장미")
            String koreanName,
            @Schema(description = "영어 이름", example = "Rose")
            String englishName,
            @Schema(description = "꽃 이미지 URL")
            String imageUrl
    ) {}

    public record SimpleCard(
            @Schema(description = "카드 ID")
            Long cardId,
            @Schema(description = "카드 제목", example = "가나디 최고 :)")
            String title,
            @Schema(description = "카드 이미지 URL")
            String imageUrl,
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
            @Schema(description = "배경 그라데이션 색상 (Hex color codes)", example = "[\"#FFE5E5\", \"#FFF0F0\"]")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            List<String> backgroundColors,
            @Schema(description = "이미지 소스 (PRESET | GENERATED)")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String imageSource,
            @Schema(description = "꽃말", example = "당신을 사랑합니다")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            String floriography,
            @Schema(description = "가격 (원)", example = "10000")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Integer price,
            @Schema(description = "디자인 에셋 ID")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            Long designAssetId,
            @Schema(description = "메인 꽃 정보")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            FlowerSummary mainFlower,
            @Schema(description = "서브 꽃 정보")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            FlowerSummary subFlower
    ) {}

    public record ArchiveResponse(
            @Schema(description = "공유 카드 목록")
            List<ShareResponse> items,
            @Schema(description = "현재 페이지 번호")
            int page,
            @Schema(description = "페이지 당 아이템 수")
            int size,
            @Schema(description = "전체 아이템 수")
            long totalElements,
            @Schema(description = "전체 페이지 수")
            int totalPages
    ) {}

    public record ArchiveMetaResponse(
            @Schema(description = "대기 중인 친구 요청 존재 여부")
            boolean hasPendingFriendRequests
    ) {}
}
