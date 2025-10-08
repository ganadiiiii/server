package ganadii.hanjum.service;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.domain.enums.FriendRequestStatus;
import ganadii.hanjum.repository.FriendRequestRepository;
import ganadii.hanjum.repository.FriendshipsRepository;
import ganadii.hanjum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendshipsRepository friendshipsRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Transactional(readOnly = true)
    public Page<User> listFriends(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Get friend IDs
        List<UUID> friendIds = friendshipsRepository.findByUser_UserId(userId).stream()
                .map(fr -> fr.getFriend().getUserId())
                .toList();

        // Get pending incoming friend request sender IDs
        Set<UUID> pendingRequestSenderIds = friendRequestRepository.findByReceiver_UserIdAndStatus(userId, FriendRequestStatus.PENDING).stream()
                .map(req -> req.getSender().getUserId())
                .collect(Collectors.toSet());

        if (friendIds.isEmpty() && pendingRequestSenderIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Query with DB-level sorting: pending request senders first, then regular friends
        return userRepository.findFriendsAndRequestersSorted(friendIds, pendingRequestSenderIds, pageable);
    }

    @Transactional(readOnly = true)
    public List<UUID> friendIds(UUID userId) {
        return friendshipsRepository.findByUser_UserId(userId)
                .stream().map(fr -> fr.getFriend().getUserId()).toList();
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(UUID userId, String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Get friend IDs
        Set<UUID> friendIds = friendshipsRepository.findByUser_UserId(userId).stream()
                .map(fr -> fr.getFriend().getUserId())
                .collect(Collectors.toSet());

        // Get outgoing pending friend request receiver IDs
        Set<UUID> pendingReceiverIds = friendRequestRepository.findBySender_UserIdAndStatus(userId, FriendRequestStatus.PENDING).stream()
                .map(req -> req.getReceiver().getUserId())
                .collect(Collectors.toSet());

        // Query with DB-level sorting: friends first, then pending requests, then others
        return userRepository.searchUsersWithCustomSort(userId, q, friendIds, pendingReceiverIds, pageable);
    }

    @Transactional
    public void deleteFriend(UUID userId, UUID friendId) {
        // delete both directions if exists
        friendshipsRepository.deleteByUser_UserIdAndFriend_UserId(userId, friendId);
        friendshipsRepository.deleteByUser_UserIdAndFriend_UserId(friendId, userId);
    }

    @Transactional(readOnly = true)
    public long friendCount(UUID userId) {
        return friendshipsRepository.countByUser_UserId(userId);
    }
}
