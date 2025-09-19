package ganadii.hanjum.repository;

import ganadii.hanjum.domain.FriendRequest;
import ganadii.hanjum.domain.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@SuppressWarnings("unused")
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findBySender_UserId(UUID senderId);
    List<FriendRequest> findByReceiver_UserId(UUID receiverId);
    List<FriendRequest> findByStatus(FriendRequestStatus status);
    Optional<FriendRequest> findBySender_UserIdAndReceiver_UserId(UUID senderId, UUID receiverId);
}
