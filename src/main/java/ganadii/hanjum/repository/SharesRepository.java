package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Shares;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
@SuppressWarnings("unused")
public interface SharesRepository extends JpaRepository<Shares, Long> {
    List<Shares> findBySender_UserId(UUID senderId);
    List<Shares> findByReceiver_UserId(UUID receiverId);
    List<Shares> findByFlowerCards_CardId(Long cardId);
    @Query("select s from Shares s where s.receiver.userId = :receiverId and s.isRead = false")
    List<Shares> findUnreadByReceiver(@Param("receiverId") UUID receiverId);
    Page<Shares> findByReceiver_UserId(UUID receiverId, Pageable pageable);
    Optional<Shares> findFirstByFlowerCards_CardId(Long cardId);
    Optional<Shares> findByIdempotencyKeyAndSender_UserId(String idempotencyKey, UUID senderId);
    Optional<Shares> findBySender_UserIdAndReceiver_UserIdAndFlowerCards_CardId(UUID senderId, UUID receiverId, Long cardId);
    boolean existsByFlowerCards_CardId(Long cardId);
    void deleteByFlowerCards_CardId(Long cardId);

    @Query("SELECT COUNT(s) FROM Shares s WHERE s.receiver.userId = :userId AND s.isRead = false AND s.sender.userId <> :userId")
    long countUnreadFromFriends(@Param("userId") UUID userId);

    @Query("SELECT s FROM Shares s WHERE s.flowerCards.cardId = :cardId " +
           "AND s.sender.userId = :senderId " +
           "AND s.sender.userId <> s.receiver.userId")
    Optional<Shares> findFriendShareByCardAndSender(
            @Param("cardId") Long cardId,
            @Param("senderId") UUID senderId
    );

    Optional<Shares> findByFlowerCards_CardIdAndReceiver_UserId(Long cardId, UUID receiverId);

    @Modifying
    @Query("UPDATE Shares s SET s.isRead = true WHERE s.shareId = :shareId")
    void markAsRead(@Param("shareId") Long shareId);

    @Query("SELECT s FROM Shares s " +
           "WHERE s.flowerCards.cardId = :cardId " +
           "AND EXISTS (SELECT 1 FROM Friendships f " +
           "            WHERE f.user.userId = :userId " +
           "            AND f.friend.userId = s.receiver.userId)")
    Optional<Shares> findByCardIdAndFriendReceiver(
            @Param("cardId") Long cardId,
            @Param("userId") UUID userId
    );
}
