package ganadii.hanjum.repository;

import ganadii.hanjum.domain.FlowerCards;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface FlowerCardsRepository extends JpaRepository<FlowerCards, Long> {
    List<FlowerCards> findByCreator_UserId(UUID userId);
    List<FlowerCards> findByIsOrdered(boolean isOrdered);
    List<FlowerCards> findByCardIdIn(Collection<Long> ids);
    List<FlowerCards> findByTitleContainingIgnoreCase(String title);
}

