package ganadii.hanjum.repository;

import ganadii.hanjum.domain.Shares;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@SuppressWarnings("unused")
public interface SharesRepository extends JpaRepository<Shares, Long> {
    List<Shares> findBySender_UserId(UUID senderId);
    List<Shares> findByReceiver_UserId(UUID receiverId);
    List<Shares> findByFlowerCards_CardId(Long cardId);
    List<Shares> findByIsReadFalseAndReceiver_UserId(UUID receiverId);
}

