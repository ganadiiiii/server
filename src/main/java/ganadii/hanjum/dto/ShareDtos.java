package ganadii.hanjum.dto;

import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ShareDtos {

    public record SendShareRequest(
            UUID receiverId,
            @Size(max = 20) String toName,
            @Size(max = 20) String fromName,
            @Size(max = 200) String note
    ) {}

    public record ShareResponse(
            Long shareId,
            Long cardId,
            String toName,
            String fromName,
            String note,
            Boolean isRead,
            Instant sharedAt,
            SimpleUser sender,
            SimpleUser receiver,
            SimpleCard card
    ) {}

    public record SimpleUser(UUID userId, String firstName, String lastName) {}

    public record SimpleCard(
            Long cardId,
            String title,
            String imageUrl,
            String whoType,
            String whoLabel,
            String whenType,
            String whenLabel,
            List<String> emotionTypes,
            List<String> emotionLabels,
            String bouquetSize,
            String bouquetLabel,
            String wrappingType,
            String wrappingLabel
    ) {}

    public record ArchiveResponse(List<ShareResponse> items, int page, int size, long totalElements, int totalPages) {}

    public record ArchiveMetaResponse(boolean hasPendingFriendRequests) {}
}
