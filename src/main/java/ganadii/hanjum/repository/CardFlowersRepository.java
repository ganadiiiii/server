package ganadii.hanjum.repository;

import ganadii.hanjum.domain.CardFlowers;
import ganadii.hanjum.domain.CardFlowersId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface CardFlowersRepository extends JpaRepository<CardFlowers, CardFlowersId> {
    List<CardFlowers> findById_CardId(Long cardId);
    List<CardFlowers> findById_FlowerId(Long flowerId);
    List<CardFlowers> findByFlowerCards_CardId(Long cardId);
    List<CardFlowers> findByFlowers_FlowerId(Long flowerId);
    List<CardFlowers> findByFlowerCards_CardIdIn(Collection<Long> cardIds);
    void deleteByFlowerCards_CardId(Long cardId);
}
