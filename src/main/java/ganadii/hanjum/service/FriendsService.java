package ganadii.hanjum.service;

import ganadii.hanjum.domain.FriendRequest;
import ganadii.hanjum.domain.Friendships;
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

import java.util.ArrayList;
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
        List<Friendships> relations = friendshipsRepository.findByUser_UserId(userId);
        List<UUID> friendIds = relations.stream().map(fr -> fr.getFriend().getUserId()).toList();
        Pageable pageable = PageRequest.of(page, size);
        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Get pending incoming friend requests
        List<FriendRequest> pendingRequests = friendRequestRepository.findByReceiver_UserIdAndStatus(userId, FriendRequestStatus.PENDING);
        Set<UUID> pendingRequestSenderIds = pendingRequests.stream()
                .map(req -> req.getSender().getUserId())
                .collect(Collectors.toSet());

        // Load all friend users
        List<User> allFriends = new ArrayList<>();
        userRepository.findAllById(friendIds).forEach(allFriends::add);

        // Separate into two groups: pending request senders and regular friends
        List<User> pendingRequestUsers = new ArrayList<>();
        List<User> regularFriends = new ArrayList<>();

        for (User friend : allFriends) {
            if (pendingRequestSenderIds.contains(friend.getUserId())) {
                pendingRequestUsers.add(friend);
            } else {
                regularFriends.add(friend);
            }
        }

        // Sort each group by lastName, firstName
        java.util.Comparator<User> nameComparator = java.util.Comparator
                .comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);

        pendingRequestUsers.sort(nameComparator);
        regularFriends.sort(nameComparator);

        // Combine: pending requests first, then regular friends
        List<User> sortedAll = new ArrayList<>();
        sortedAll.addAll(pendingRequestUsers);
        sortedAll.addAll(regularFriends);

        // Paginate
        int total = sortedAll.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<User> slice = sortedAll.subList(from, to);
        return new org.springframework.data.domain.PageImpl<>(slice, pageable, total);
    }

    @Transactional(readOnly = true)
    public List<UUID> friendIds(UUID userId) {
        return friendshipsRepository.findByUser_UserId(userId)
                .stream().map(fr -> fr.getFriend().getUserId()).toList();
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(UUID userId, String q, int page, int size) {
        List<Friendships> relations = friendshipsRepository.findByUser_UserId(userId);
        List<UUID> friendIds = relations.stream().map(fr -> fr.getFriend().getUserId()).toList();
        Pageable pageable = PageRequest.of(page, size);

        // Get outgoing pending friend requests
        List<FriendRequest> outgoingPendingRequests = friendRequestRepository.findBySender_UserIdAndStatus(userId, FriendRequestStatus.PENDING);
        Set<UUID> pendingReceiverIds = outgoingPendingRequests.stream()
                .map(req -> req.getReceiver().getUserId())
                .collect(Collectors.toSet());

        // Get all search results (without special ordering)
        Page<User> searchResultPage = userRepository.searchAll(userId, q, PageRequest.of(0, Integer.MAX_VALUE));
        List<User> allSearchResults = searchResultPage.getContent();

        // Separate into three groups
        List<User> friends = new ArrayList<>();
        List<User> pendingUsers = new ArrayList<>();
        List<User> otherUsers = new ArrayList<>();

        for (User user : allSearchResults) {
            UUID uid = user.getUserId();
            if (friendIds.contains(uid)) {
                friends.add(user);
            } else if (pendingReceiverIds.contains(uid)) {
                pendingUsers.add(user);
            } else {
                otherUsers.add(user);
            }
        }

        // Sort each group by lastName, firstName
        java.util.Comparator<User> nameComparator = java.util.Comparator
                .comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);

        friends.sort(nameComparator);
        pendingUsers.sort(nameComparator);
        otherUsers.sort(nameComparator);

        // Combine: friends first, then pending, then others
        List<User> sortedAll = new ArrayList<>();
        sortedAll.addAll(friends);
        sortedAll.addAll(pendingUsers);
        sortedAll.addAll(otherUsers);

        // Paginate
        int total = sortedAll.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<User> slice = sortedAll.subList(from, to);
        return new org.springframework.data.domain.PageImpl<>(slice, pageable, total);
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
