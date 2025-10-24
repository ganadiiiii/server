package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Friendships;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("unused")
public interface FriendshipsRepository extends JpaRepository<Friendships, Long> {
    List<Friendships> findByUser_UserId(UUID userId);
    List<Friendships> findByFriend_UserId(UUID friendId);
    boolean existsByUser_UserIdAndFriend_UserId(UUID userId, UUID friendId);

    @Modifying
    @Query("DELETE FROM Friendships f WHERE f.user.userId = :userId AND f.friend.userId = :friendId")
    void deleteByUser_UserIdAndFriend_UserId(@Param("userId") UUID userId, @Param("friendId") UUID friendId);

    long countByUser_UserId(UUID userId);
}
