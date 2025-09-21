package ganadii.hanjum.dto;

import ganadii.hanjum.domain.enums.FriendRequestStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FriendRequestDtos {

    public record CreateRequest(UUID receiverId) {}

    public record FriendRequestResponse(
            Long requestId,
            UserSummary sender,
            UserSummary receiver,
            FriendRequestStatus status,
            Instant createdAt,
            Instant respondedAt
    ) {}

    public record UserSummary(UUID userId, String email, String nickname) {}

    public record ListResponse(List<FriendRequestResponse> items) {}
}

