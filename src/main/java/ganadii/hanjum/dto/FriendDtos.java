package ganadii.hanjum.dto;

import java.util.List;
import java.util.UUID;

public class FriendDtos {
    public record FriendSummary(UUID userId, String email, String firstName, String lastName, long friendCount) {}
    public record FriendListResponse(List<FriendSummary> items, int page, int size, long totalElements, int totalPages) {}

    public record SearchItem(UUID userId, String email, String firstName, String lastName, boolean isFriend, long friendCount) {}
    public record SearchResponse(List<SearchItem> items, int page, int size, long totalElements, int totalPages) {}
}
