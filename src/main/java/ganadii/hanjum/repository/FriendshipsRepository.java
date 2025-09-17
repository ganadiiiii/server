package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Friendships;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendshipsRepository extends JpaRepository<Friendships, Long> {
    List<Friendships> findByUser_UserId(UUID userId);
    List<Friendships> findByFriend_UserId(UUID friendId);
    boolean existsByUser_UserIdAndFriend_UserId(UUID userId, UUID friendId);
}

