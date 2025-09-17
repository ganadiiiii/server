package ganadii.hanjum.repository;

import ganadii.hanjum.domain.CardFlowers;
import ganadii.hanjum.domain.CardFlowersId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardFlowersRepository extends JpaRepository<CardFlowers, CardFlowersId> {
    List<CardFlowers> findById_CardId(Long cardId);
    List<CardFlowers> findById_FlowerId(Long flowerId);
    List<CardFlowers> findByFlowerCards_CardId(Long cardId);
    List<CardFlowers> findByFlowers_FlowerId(Long flowerId);
}

