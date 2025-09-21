package ganadii.hanjum.repository;

import ganadii.hanjum.domain.FlowerCards;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@SuppressWarnings("unused")
public interface FlowerCardsRepository extends JpaRepository<FlowerCards, Long> {
    List<FlowerCards> findByCreator_UserId(UUID userId);
    Page<FlowerCards> findByCreator_UserId(UUID userId, Pageable pageable);
    Optional<FlowerCards> findByCardIdAndCreator_UserId(Long cardId, UUID userId);
    List<FlowerCards> findByIsOrdered(boolean isOrdered);
    List<FlowerCards> findByCardIdIn(Collection<Long> ids);
    List<FlowerCards> findByTitleContainingIgnoreCase(String title);
}
