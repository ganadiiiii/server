package ganadii.hanjum.service;

import ganadii.hanjum.domain.Friendships;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.repository.FriendshipsRepository;
import ganadii.hanjum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendsService {

    private final FriendshipsRepository friendshipsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<User> listFriends(UUID userId, int page, int size) {
        List<Friendships> relations = friendshipsRepository.findByUser_UserId(userId);
        List<UUID> friendIds = relations.stream().map(fr -> fr.getFriend().getUserId()).toList();
        Pageable pageable = PageRequest.of(page, size);
        if (friendIds.isEmpty()) {
            return Page.empty(pageable);
        }
        // Load all friend users and paginate in-memory (small-to-mid size lists)
        List<User> all = new java.util.ArrayList<>();
        userRepository.findAllById(friendIds).forEach(all::add);
        all.sort(java.util.Comparator
                .comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER));
        int total = all.size();
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);
        List<User> slice = all.subList(from, to);
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
        if (friendIds.isEmpty()) {
            return userRepository.searchAll(userId, q, pageable);
        }
        return userRepository.searchAllOrderFriendFirst(userId, q, friendIds, pageable);
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
